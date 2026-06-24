package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Comprueba que si no está logeado un usuario y accede a la ruta /user/profile, se le redirige a la página de login
    @Test
    void login_whenUserIsAnonymous_shouldReturnLoginView() throws Exception
    {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    // Comprueba que si accedemos a /register aparezca la vista de registro
    @Test
    void registerForm_whenUserIsAnonymous_shouldReturnRegisterView() throws Exception
    {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    // Comprueba que si un usuario ya registrado intenta registrarse de nuevo, se le redirige a la página de login con un mensaje de error
    @Test
    void register_whenDataIsValid_shouldCreateUserAndRedirectToLogin() throws Exception
    {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "cliente")
                        .param("email", "cliente@example.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login*"))
                .andExpect(flash().attributeExists("message"));

        Optional<User> savedUser = userRepository.findByUsername("cliente");

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("cliente@example.com");
        assertThat(savedUser.get().getRole()).isEqualTo(Role.ROLE_CUSTOMER);
        assertThat(savedUser.get().getPassword()).isNotEqualTo("Password1!");
        assertThat(passwordEncoder.matches("Password1!", savedUser.get().getPassword())).isTrue();
    }

    // Comprueba que cuando la contraseña no coincida en el registro, se le muestre un error
    @Test
    void register_whenPasswordConfirmDoesNotMatch_shouldReturnRegisterViewWithFieldError() throws Exception
    {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "cliente")
                        .param("email", "cliente@example.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Different1!"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeHasFieldErrors("user", "passwordConfirm"));

        assertThat(userRepository.existsByUsername("cliente")).isFalse();
    }

    // Comprueba que si hay algún dato mal introducido en el registro, se le muestre un error
    @Test
    void register_whenDtoValidationFails_shouldReturnRegisterViewWithErrors() throws Exception {
        long usersBefore = userRepository.count();

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "")
                        .param("email", "invalid-email")
                        .param("password", "weak")
                        .param("passwordConfirm", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeHasFieldErrors(
                        "user",
                        "username",
                        "email",
                        "password",
                        "passwordConfirm"
                ));

        assertThat(userRepository.count()).isEqualTo(usersBefore);
    }

    // Comprueba que cuando el nombre de usuario ya exista en el registro, se le muestre un error
    @Test
    void register_whenUsernameAlreadyExists_shouldReturnRegisterViewWithError() throws Exception
    {
        userRepository.save(buildUser("cliente", "existing@example.com"));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "cliente")
                        .param("email", "new@example.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("error"));

        assertThat(userRepository.findByEmail("new@example.com")).isEmpty();
    }

    // Comprueba que cuando el correo ya exista en el registro, se le muestre un error
    @Test
    void register_whenEmailAlreadyExists_shouldReturnRegisterViewWithError() throws Exception
    {
        userRepository.save(buildUser("existing", "cliente@example.com"));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "new-user")
                        .param("email", "cliente@example.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("error"));

        assertThat(userRepository.findByUsername("new-user")).isEmpty();
    }

    // Comprueba que si existe el CSRF no pueda registrarse el usuario
    @Test
    void register_whenPostWithoutCsrf_shouldReturnForbidden() throws Exception
    {
        mockMvc.perform(post("/register")
                        .param("username", "cliente")
                        .param("email", "cliente@example.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!"))
                .andExpect(status().isForbidden());

        assertThat(userRepository.existsByUsername("cliente")).isFalse();
    }

    // Función que crea un usuario para los tests
    private User buildUser(String username, String email)
    {
        return User.builder()
                .username(username)
                .name("Nombre")
                .lastName("Apellido")
                .email(email)
                .password(passwordEncoder.encode("Password1!"))
                .role(Role.ROLE_CUSTOMER)
                .active(true)
                .creationDate(LocalDateTime.now())
                .build();
    }
}