package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AdminUserControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void listUsers_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void listUsers_whenUserHasUserRole_shouldReturnForbidden() throws Exception
    {
        mockMvc.perform(get("/admin/users")
                        .with(user("normal").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listUsers_whenUserIsAdminAndAdminEmailIsProvided_shouldReturnAdminListView() throws Exception {
        User admin = userRepository.save(buildUser(
                "admin_list_provided",
                "admin_list_provided@example.com",
                Role.ROLE_ADMIN
        ));

        userRepository.save(buildUser(
                "cliente_list_provided",
                "cliente_list_provided@example.com",
                Role.ROLE_CUSTOMER
        ));

        mockMvc.perform(get("/admin/users")
                        .with(user(admin.getUsername()).roles("ADMIN"))
                        .param("adminEmail", admin.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users-admin-list"))
                .andExpect(model().attributeExists("adminEmail"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("adminEmail", admin.getEmail()));
    }

    @Test
    void listUsers_whenUserIsAdminAndAdminEmailIsMissing_shouldResolveFirstAdminAndReturnAdminListView() throws Exception {
        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.save(
                        buildUser("admin", "admin@example.com", Role.ROLE_ADMIN)
                ));

        userRepository.save(buildUser(
                "cliente_list_users",
                "cliente_list_users@example.com",
                Role.ROLE_CUSTOMER
        ));

        mockMvc.perform(get("/admin/users")
                        .with(user(admin.getUsername()).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("users/users-admin-list"))
                .andExpect(model().attributeExists("adminEmail"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("adminEmail", admin.getEmail()));
    }

    @Test
    void listUsers_whenAdminEmailBelongsToCustomer_shouldThrowException()
    {
        User customer = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        assertThatThrownBy(() ->
                mockMvc.perform(get("/admin/users")
                        .with(user("admin").roles("ADMIN"))
                        .param("adminEmail", customer.getEmail()))
        )
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tienes permisos para gestionar usuarios");
    }

    @Test
    void editUser_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(get("/admin/users/{id}/edit", target.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void editUser_whenUserHasUserRole_shouldReturnForbidden() throws Exception
    {
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(get("/admin/users/{id}/edit", target.getId())
                        .with(user("normal").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void editUser_whenUserIsAdmin_shouldReturnEditView() throws Exception {
        User admin = userRepository.save(buildUser(
                "admin_edit_user",
                "admin_edit_user@example.com",
                Role.ROLE_ADMIN
        ));

        User target = userRepository.save(buildUser(
                "cliente_edit_user",
                "cliente_edit_user@example.com",
                Role.ROLE_CUSTOMER
        ));

        mockMvc.perform(get("/admin/users/{id}/edit", target.getId())
                        .with(user(admin.getUsername()).roles("ADMIN"))
                        .param("adminEmail", admin.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/user-admin-edit"))
                .andExpect(model().attributeExists("adminEmail"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("adminEmail", admin.getEmail()));
    }

    @Test
    void updateUser_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/admin/users/{id}/edit", target.getId())
                        .with(csrf())
                        .param("name", "Nombre actualizado")
                        .param("lastName", "Apellido actualizado")
                        .param("email", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void updateUser_whenUserHasUserRole_shouldReturnForbidden() throws Exception
    {
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/admin/users/{id}/edit", target.getId())
                        .with(user("normal").roles("USER"))
                        .with(csrf())
                        .param("name", "Nombre actualizado")
                        .param("lastName", "Apellido actualizado")
                        .param("email", "updated@example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_whenAdminAndDataIsValid_shouldUpdateUserAndRedirect() throws Exception {
        User admin = userRepository.save(buildUser(
                "admin_update_user",
                "admin_update_user@example.com",
                Role.ROLE_ADMIN
        ));

        User target = userRepository.save(buildUser(
                "cliente_update_user",
                "cliente_update_user@example.com",
                Role.ROLE_CUSTOMER
        ));

        mockMvc.perform(post("/admin/users/{id}/edit", target.getId())
                        .with(user(admin.getUsername()).roles("ADMIN"))
                        .with(csrf())
                        .param("adminEmail", admin.getEmail())
                        .param("name", "Nombre actualizado")
                        .param("lastName", "Apellido actualizado")
                        .param("phone", "600111222")
                        .param("email", "updated_update_user@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/users*"))
                .andExpect(flash().attributeExists("success"));

        User updatedUser = userRepository.findById(target.getId()).orElseThrow();

        assertThat(updatedUser.getName()).isEqualTo("Nombre actualizado");
        assertThat(updatedUser.getLastName()).isEqualTo("Apellido actualizado");
        assertThat(updatedUser.getPhone()).isEqualTo("600111222");
        assertThat(updatedUser.getEmail()).isEqualTo("updated_update_user@example.com");
    }

    @Test
    void updateUser_whenPostWithoutCsrf_shouldReturnForbidden() throws Exception
    {
        User admin = userRepository.save(buildUser("admin", "admin@example.com", Role.ROLE_ADMIN));
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/admin/users/{id}/edit", target.getId())
                        .with(user("admin").roles("ADMIN"))
                        .param("adminEmail", admin.getEmail())
                        .param("name", "Nombre actualizado"))
                .andExpect(status().isForbidden());
    }

    @Test
    void toggleStatus_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/admin/users/{id}/toggle-status", target.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void toggleStatus_whenUserHasUserRole_shouldReturnForbidden() throws Exception
    {
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/admin/users/{id}/toggle-status", target.getId())
                        .with(user("normal").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void toggleStatus_whenAdminAndTargetIsCustomer_shouldToggleUserStatusAndRedirect() throws Exception {
        User admin = userRepository.save(buildUser(
                "admin_toggle_customer",
                "admin_toggle_customer@example.com",
                Role.ROLE_ADMIN
        ));

        User target = userRepository.save(buildUser(
                "cliente_toggle_customer",
                "cliente_toggle_customer@example.com",
                Role.ROLE_CUSTOMER
        ));

        assertThat(target.isActive()).isTrue();

        mockMvc.perform(post("/admin/users/{id}/toggle-status", target.getId())
                        .with(user(admin.getUsername()).roles("ADMIN"))
                        .with(csrf())
                        .param("adminEmail", admin.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/users*"))
                .andExpect(flash().attributeExists("success"));

        User updatedUser = userRepository.findById(target.getId()).orElseThrow();

        assertThat(updatedUser.isActive()).isFalse();
    }

    @Test
    void toggleStatus_whenAdminAndTargetIsAdmin_shouldRedirectWithError() throws Exception {
        User admin = userRepository.save(buildUser(
                "admin_toggle_status",
                "admin_toggle_status@example.com",
                Role.ROLE_ADMIN
        ));

        User otherAdmin = userRepository.save(buildUser(
                "admin2_toggle_status",
                "admin2_toggle_status@example.com",
                Role.ROLE_ADMIN
        ));

        mockMvc.perform(post("/admin/users/{id}/toggle-status", otherAdmin.getId())
                        .with(user(admin.getUsername()).roles("ADMIN"))
                        .with(csrf())
                        .param("adminEmail", admin.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/users*"))
                .andExpect(flash().attributeExists("error"));

        User unchangedAdmin = userRepository.findById(otherAdmin.getId()).orElseThrow();

        assertThat(unchangedAdmin.isActive()).isTrue();
    }

    @Test
    void softDelete_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/admin/users/{id}/delete", target.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void softDelete_whenUserHasUserRole_shouldReturnForbidden() throws Exception
    {
        User target = userRepository.save(buildUser("cliente", "cliente@example.com", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/admin/users/{id}/delete", target.getId())
                        .with(user("normal").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void softDelete_whenAdminAndTargetIsCustomer_shouldSetActiveFalseAndRedirect() throws Exception {
        User admin = userRepository.save(buildUser(
                "admin_soft_delete",
                "admin_soft_delete@example.com",
                Role.ROLE_ADMIN
        ));

        User target = userRepository.save(buildUser(
                "cliente_soft_delete",
                "cliente_soft_delete@example.com",
                Role.ROLE_CUSTOMER
        ));

        mockMvc.perform(post("/admin/users/{id}/delete", target.getId())
                        .with(user(admin.getUsername()).roles("ADMIN"))
                        .with(csrf())
                        .param("adminEmail", admin.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/users*"))
                .andExpect(flash().attributeExists("success"));

        User updatedUser = userRepository.findById(target.getId()).orElseThrow();

        assertThat(updatedUser.isActive()).isFalse();
    }

    private User buildUser(String username, String email, Role role)
    {
        return User.builder()
                .username(username)
                .name("Nombre")
                .lastName("Apellido")
                .email(email)
                .password(passwordEncoder.encode("Password1!"))
                .role(role)
                .active(true)
                .creationDate(LocalDateTime.now())
                .build();
    }
}