package com.ecommerce.security;


import com.ecommerce.model.User;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LoginSecurityTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void login_whenCredentialsAreValid_shouldAuthenticateAndRedirectToProducts() throws Exception
    {
        userRepository.save(buildUser("cliente", "cliente@example.com", "Password1!", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "cliente")
                        .param("password", "Password1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(authenticated().withUsername("cliente"));
    }

    @Test
    void login_whenPasswordIsInvalid_shouldRedirectToLoginErrorAndRemainUnauthenticated() throws Exception
    {
        userRepository.save(buildUser("cliente", "cliente@example.com", "Password1!", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "cliente")
                        .param("password", "WrongPassword1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void login_whenUsernameDoesNotExist_shouldRedirectToLoginErrorAndRemainUnauthenticated() throws Exception
    {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "unknown")
                        .param("password", "Password1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void login_whenPostWithoutCsrf_shouldReturnForbiddenAndRemainUnauthenticated() throws Exception
    {
        userRepository.save(buildUser("cliente", "cliente@example.com", "Password1!", Role.ROLE_CUSTOMER));

        mockMvc.perform(post("/login")
                        .param("username", "cliente")
                        .param("password", "Password1!"))
                .andExpect(status().isForbidden())
                .andExpect(unauthenticated());
    }

    @Test
    void logout_whenUserIsAuthenticated_shouldLogoutAndRedirectToLoginLogout() throws Exception
    {
        userRepository.save(buildUser("cliente", "cliente@example.com", "Password1!", Role.ROLE_CUSTOMER));

        mockMvc.perform(logout("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(unauthenticated());
    }

    @Test
    void login_whenAdminCredentialsAreValid_shouldAuthenticateWithAdminAuthority() throws Exception {
        String username = "admin_test_login";
        String password = "Password1!";

        userRepository.save(buildUser(
                username,
                "admin_test_login@example.com",
                password,
                Role.ROLE_ADMIN
        ));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(authenticated()
                        .withUsername(username)
                        .withRoles("ADMIN"));
    }

    private User buildUser(String username, String email, String rawPassword, Role role)
    {
        return User.builder()
                .username(username)
                .name("Nombre")
                .lastName("Apellido")
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .active(true)
                .creationDate(LocalDateTime.now())
                .build();
    }
}
