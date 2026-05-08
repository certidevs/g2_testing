package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .name("Juan")
                .lastName("Pérez")
                .email("juan.perez@gmail.com")
                .phone("123456789")
                .password("password123")
                .birthday(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .gender(Gender.MALE)
                .role(Role.CUSTOMER)
                .creationDate(LocalDateTime.now())
                .build());

        user2 = userRepository.save(User.builder()
                .name("María")
                .lastName("García")
                .email("maria.garcia@gmail.com")
                .phone("987654321")
                .password("password456")
                .birthday(LocalDateTime.of(1985, Month.MARCH, 15, 0, 0))
                .gender(Gender.FEMALE)
                .role(Role.ADMIN)
                .creationDate(LocalDateTime.now())
                .build());

        user3 = userRepository.save(User.builder()
                .name("Carlos")
                .lastName("López")
                .email("carlos.lopez@gmail.com")
                .phone("555555555")
                .password("password789")
                .birthday(LocalDateTime.of(1995, Month.JULY, 20, 0, 0))
                .gender(Gender.MALE)
                .role(Role.CUSTOMER)
                .creationDate(LocalDateTime.now())
                .build());
    }

    @Test
    void listUsersFull() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", hasSize(3)));
    }

    @Test
    void listUsersEmpty() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", hasSize(0)));
    }

    @Test
    void userDetailFound() throws Exception {
        mockMvc.perform(get("/users/{id}", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user-detail"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("id", is(user1.getId()))))
                .andExpect(model().attribute("user", hasProperty("name", is("Juan"))))
                .andExpect(model().attribute("user", hasProperty("lastName", is("Pérez"))))
                .andExpect(model().attribute("user", hasProperty("email", is("juan.perez@gmail.com"))))
                .andExpect(model().attribute("user", hasProperty("role", is(Role.CUSTOMER))));
    }

    @Test
    void userDetailNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/users/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminUsersListOk() throws Exception {
        mockMvc.perform(get("/admin/users").param("adminEmail", user2.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users-admin-list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", hasSize(3)));
    }

    @Test
    void adminEditUserFormOk() throws Exception {
        mockMvc.perform(get("/admin/users/{id}/edit", user1.getId()).param("adminEmail", user2.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user-admin-edit"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", hasProperty("id", is(user1.getId()))));
    }

    @Test
    void adminUpdatesUserData() throws Exception {
        mockMvc.perform(post("/admin/users/{id}/edit", user1.getId())
                        .param("adminEmail", user2.getEmail())
                        .param("name", "Juan Actualizado")
                        .param("lastName", "Pérez")
                        .param("email", "juan.actualizado@gmail.com")
                        .param("phone", "111111111")
                        .param("gender", "MALE")
                        .param("role", "CUSTOMER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?adminEmail=" + user2.getEmail()));

        User updatedUser = userRepository.findById(user1.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("Juan Actualizado", updatedUser.getName());
        org.junit.jupiter.api.Assertions.assertEquals("juan.actualizado@gmail.com", updatedUser.getEmail());
    }

    @Test
    void adminCanToggleCustomerStatus() throws Exception {
        mockMvc.perform(post("/admin/users/{id}/toggle-status", user1.getId())
                        .param("adminEmail", user2.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?adminEmail=" + user2.getEmail()));

        User updatedUser = userRepository.findById(user1.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertFalse(updatedUser.isActive());
    }

    @Test
    void adminCanSoftDeleteCustomer() throws Exception {
        mockMvc.perform(post("/admin/users/{id}/delete", user3.getId())
                        .param("adminEmail", user2.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?adminEmail=" + user2.getEmail()));

        User deletedUser = userRepository.findById(user3.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertFalse(deletedUser.isActive());
    }

    @Test
    void adminCannotToggleAnotherAdmin() throws Exception {
        mockMvc.perform(post("/admin/users/{id}/toggle-status", user2.getId())
                        .param("adminEmail", user2.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?adminEmail=" + user2.getEmail()));

        User adminUser = userRepository.findById(user2.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(adminUser.isActive());
    }
}

