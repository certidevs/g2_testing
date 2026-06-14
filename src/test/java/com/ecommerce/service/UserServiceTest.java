package com.ecommerce.service;

import com.ecommerce.dto.UserRequestDto;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.PaymentMethod;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest
{
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails()
    {
        User user = buildUser(UUID.randomUUID(), "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findByUsername("cliente")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("cliente");

        assertThat(result.getUsername()).isEqualTo("cliente");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(result.getAuthorities())
                .extracting(authority -> authority.getAuthority())
                .containsExactly("ROLE_CUSTOMER");

        verify(userRepository).findByUsername("cliente");
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowException()
    {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with username: unknown");

        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void findAll_shouldReturnAllUsers()
    {
        User user1 = buildUser(UUID.randomUUID(), "cliente1", "cliente1@example.com", Role.ROLE_CUSTOMER);
        User user2 = buildUser(UUID.randomUUID(), "cliente2", "cliente2@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(User::getUsername)
                .containsExactly("cliente1", "cliente2");

        verify(userRepository).findAll();
    }

    @Test
    void register_whenDataIsValid_shouldEncodePasswordSetCustomerRoleAndSaveUser()
    {
        UserRequestDto form = buildRegisterForm(
                "cliente",
                "cliente@example.com",
                "Password1!",
                "Password1!"
        );

        when(userRepository.existsByUsername("cliente")).thenReturn(false);
        when(userRepository.existsByEmail("cliente@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("encoded-password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        User result = userService.register(form);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("cliente");
        assertThat(result.getEmail()).isEqualTo("cliente@example.com");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(result.getRole()).isEqualTo(Role.ROLE_CUSTOMER);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("cliente");
        assertThat(savedUser.getEmail()).isEqualTo("cliente@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_CUSTOMER);

        verify(userRepository).existsByUsername("cliente");
        verify(userRepository).existsByEmail("cliente@example.com");
        verify(passwordEncoder).encode("Password1!");
    }

    @Test
    void register_whenUsernameAlreadyExists_shouldThrowException()
    {
        UserRequestDto form = buildRegisterForm(
                "cliente",
                "cliente@example.com",
                "Password1!",
                "Password1!"
        );

        when(userRepository.existsByUsername("cliente")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El username ya existe, elige otro username");

        verify(userRepository).existsByUsername("cliente");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_whenEmailAlreadyExists_shouldThrowException()
    {
        UserRequestDto form = buildRegisterForm(
                "cliente",
                "cliente@example.com",
                "Password1!",
                "Password1!"
        );

        when(userRepository.existsByUsername("cliente")).thenReturn(false);
        when(userRepository.existsByEmail("cliente@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email ya existe, elige otro email");

        verify(userRepository).existsByUsername("cliente");
        verify(userRepository).existsByEmail("cliente@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_whenPasswordsDoNotMatch_shouldThrowException()
    {
        UserRequestDto form = buildRegisterForm(
                "cliente",
                "cliente@example.com",
                "Password1!",
                "Different1!"
        );

        when(userRepository.existsByUsername("cliente")).thenReturn(false);
        when(userRepository.existsByEmail("cliente@example.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.register(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Las contraseñas no coinciden");

        verify(userRepository).existsByUsername("cliente");
        verify(userRepository).existsByEmail("cliente@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resolveAdminEmail_whenAdminEmailIsProvided_shouldReturnProvidedEmail()
    {
        String result = userService.resolveAdminEmail("admin@example.com");

        assertThat(result).isEqualTo("admin@example.com");

        verify(userRepository, never()).findFirstByRole(any(Role.class));
    }

    @Test
    void resolveAdminEmail_whenAdminEmailIsBlankAndAdminExists_shouldReturnFirstAdminEmail()
    {
        User admin = buildUser(UUID.randomUUID(), "admin", "admin@example.com", Role.ROLE_ADMIN);

        when(userRepository.findFirstByRole(Role.ROLE_ADMIN)).thenReturn(Optional.of(admin));

        String result = userService.resolveAdminEmail(" ");

        assertThat(result).isEqualTo("admin@example.com");

        verify(userRepository).findFirstByRole(Role.ROLE_ADMIN);
    }

    @Test
    void resolveAdminEmail_whenAdminEmailIsNullAndNoAdminExists_shouldThrowException()
    {
        when(userRepository.findFirstByRole(Role.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resolveAdminEmail(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No hay administradores disponibles");

        verify(userRepository).findFirstByRole(Role.ROLE_ADMIN);
    }

    @Test
    void findByEmail_whenUserExists_shouldReturnUser()
    {
        User user = buildUser(UUID.randomUUID(), "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findFirstByEmail("cliente@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("cliente@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("cliente");

        verify(userRepository).findFirstByEmail("cliente@example.com");
    }

    @Test
    void findByUsername_whenUserExists_shouldReturnUser()
    {
        User user = buildUser(UUID.randomUUID(), "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findByUsername("cliente")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("cliente");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("cliente@example.com");

        verify(userRepository).findByUsername("cliente");
    }

    @Test
    void findProfileByEmail_whenUserExists_shouldReturnUser()
    {
        User user = buildUser(UUID.randomUUID(), "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findFirstByEmail("cliente@example.com")).thenReturn(Optional.of(user));

        User result = userService.findProfileByEmail("cliente@example.com");

        assertThat(result.getUsername()).isEqualTo("cliente");

        verify(userRepository).findFirstByEmail("cliente@example.com");
    }

    @Test
    void findProfileByEmail_whenUserDoesNotExist_shouldThrowException()
    {
        when(userRepository.findFirstByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findProfileByEmail("missing@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(userRepository).findFirstByEmail("missing@example.com");
    }

    @Test
    void findAnyProfileUser_whenUserExists_shouldReturnFirstUser()
    {
        User user = buildUser(UUID.randomUUID(), "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findFirstByOrderByCreationDateAsc()).thenReturn(Optional.of(user));

        User result = userService.findAnyProfileUser();

        assertThat(result.getUsername()).isEqualTo("cliente");

        verify(userRepository).findFirstByOrderByCreationDateAsc();
    }

    @Test
    void findAnyProfileUser_whenNoUserExists_shouldThrowException()
    {
        when(userRepository.findFirstByOrderByCreationDateAsc()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findAnyProfileUser())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No hay usuarios disponibles");

        verify(userRepository).findFirstByOrderByCreationDateAsc();
    }

    @Test
    void findById_whenUserExists_shouldReturnUser()
    {
        UUID id = UUID.randomUUID();
        User user = buildUser(id, "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.findById(id);

        assertThat(result.getId()).isEqualTo(id);

        verify(userRepository).findById(id);
    }

    @Test
    void findById_whenUserDoesNotExist_shouldThrowException()
    {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(userRepository).findById(id);
    }

    @Test
    void updateProfile_whenUserExists_shouldUpdateOnlyProfileFields()
    {
        UUID id = UUID.randomUUID();

        User currentUser = buildUser(id, "cliente", "old@example.com", Role.ROLE_CUSTOMER);
        currentUser.setPassword("encoded-password");
        currentUser.setRole(Role.ROLE_CUSTOMER);

        User updatedUser = new User();
        updatedUser.setName("Nombre actualizado");
        updatedUser.setLastName("Apellido actualizado");
        updatedUser.setPhone("600111222");
        updatedUser.setEmail("new@example.com");
        updatedUser.setGender(Gender.values()[0]);
        updatedUser.setBirthday(LocalDateTime.of(2000, 1, 1, 0, 0));
        updatedUser.setPaymentMethod(PaymentMethod.values()[0]);

        when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateProfile(id, updatedUser);

        assertThat(result.getName()).isEqualTo("Nombre actualizado");
        assertThat(result.getLastName()).isEqualTo("Apellido actualizado");
        assertThat(result.getPhone()).isEqualTo("600111222");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getGender()).isEqualTo(Gender.values()[0]);
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.values()[0]);

        assertThat(result.getUsername()).isEqualTo("cliente");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(result.getRole()).isEqualTo(Role.ROLE_CUSTOMER);

        verify(userRepository).findById(id);
        verify(userRepository).save(currentUser);
    }

    @Test
    void updateProfile_whenUserDoesNotExist_shouldThrowException()
    {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(id, new User()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_whenUserExists_shouldDeleteUser()
    {
        UUID id = UUID.randomUUID();
        User user = buildUser(id, "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        verify(userRepository).findById(id);
        verify(userRepository).delete(user);
    }

    @Test
    void delete_whenUserDoesNotExist_shouldThrowException()
    {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(userRepository).findById(id);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void updateUserByAdmin_whenUserExists_shouldUpdateAllowedFields()
    {
        UUID id = UUID.randomUUID();

        User currentUser = buildUser(id, "cliente", "old@example.com", Role.ROLE_CUSTOMER);

        User updatedUser = new User();
        updatedUser.setName("Nombre admin");
        updatedUser.setLastName("Apellido admin");
        updatedUser.setPhone("611222333");
        updatedUser.setEmail("admin-update@example.com");
        updatedUser.setGender(Gender.values()[0]);
        updatedUser.setBirthday(LocalDateTime.of(1995, 5, 5, 0, 0));
        updatedUser.setPaymentMethod(PaymentMethod.values()[0]);
        updatedUser.setRole(Role.ROLE_ADMIN);

        when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUserByAdmin(id, updatedUser);

        assertThat(result.getName()).isEqualTo("Nombre admin");
        assertThat(result.getLastName()).isEqualTo("Apellido admin");
        assertThat(result.getPhone()).isEqualTo("611222333");
        assertThat(result.getEmail()).isEqualTo("admin-update@example.com");
        assertThat(result.getGender()).isEqualTo(Gender.values()[0]);
        assertThat(result.getBirthday()).isEqualTo(LocalDateTime.of(1995, 5, 5, 0, 0));
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.values()[0]);
        assertThat(result.getRole()).isEqualTo(Role.ROLE_ADMIN);

        verify(userRepository).findById(id);
        verify(userRepository).save(currentUser);
    }

    @Test
    void updateUserByAdmin_whenUpdatedFieldsAreNull_shouldKeepCurrentValues()
    {
        UUID id = UUID.randomUUID();

        User currentUser = buildUser(id, "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);
        currentUser.setName("Nombre original");
        currentUser.setLastName("Apellido original");

        User updatedUser = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUserByAdmin(id, updatedUser);

        assertThat(result.getName()).isEqualTo("Nombre original");
        assertThat(result.getLastName()).isEqualTo("Apellido original");
        assertThat(result.getEmail()).isEqualTo("cliente@example.com");
        assertThat(result.getRole()).isEqualTo(Role.ROLE_CUSTOMER);

        verify(userRepository).findById(id);
        verify(userRepository).save(currentUser);
    }

    @Test
    void toggleUserStatus_whenCustomerExists_shouldToggleActiveStatus()
    {
        UUID id = UUID.randomUUID();
        User user = buildUser(id, "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);
        user.setActive(true);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.toggleUserStatus(id);

        assertThat(user.isActive()).isFalse();

        verify(userRepository).findById(id);
        verify(userRepository).save(user);
    }

    @Test
    void toggleUserStatus_whenTargetIsAdmin_shouldThrowException()
    {
        UUID id = UUID.randomUUID();
        User admin = buildUser(id, "admin", "admin@example.com", Role.ROLE_ADMIN);

        when(userRepository.findById(id)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> userService.toggleUserStatus(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se puede modificar el estado de otro administrador");

        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void softDeleteUser_whenCustomerExists_shouldSetActiveFalse()
    {
        UUID id = UUID.randomUUID();
        User user = buildUser(id, "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);
        user.setActive(true);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.softDeleteUser(id);

        assertThat(user.isActive()).isFalse();

        verify(userRepository).findById(id);
        verify(userRepository).save(user);
    }

    @Test
    void softDeleteUser_whenTargetIsAdmin_shouldThrowException()
    {
        UUID id = UUID.randomUUID();
        User admin = buildUser(id, "admin", "admin@example.com", Role.ROLE_ADMIN);

        when(userRepository.findById(id)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> userService.softDeleteUser(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se puede modificar el estado de otro administrador");

        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void isAdmin_whenUserHasAdminRole_shouldReturnTrue()
    {
        User admin = buildUser(UUID.randomUUID(), "admin", "admin@example.com", Role.ROLE_ADMIN);

        boolean result = userService.isAdmin(admin);

        assertThat(result).isTrue();
    }

    @Test
    void isAdmin_whenUserHasCustomerRole_shouldReturnFalse()
    {
        User user = buildUser(UUID.randomUUID(), "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        boolean result = userService.isAdmin(user);

        assertThat(result).isFalse();
    }

    @Test
    void validateAdminAccess_whenUserIsAdmin_shouldNotThrowException()
    {
        User admin = buildUser(UUID.randomUUID(), "admin", "admin@example.com", Role.ROLE_ADMIN);

        when(userRepository.findFirstByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        userService.validateAdminAccess("admin@example.com");

        verify(userRepository).findFirstByEmail("admin@example.com");
    }

    @Test
    void validateAdminAccess_whenUserDoesNotExist_shouldThrowException()
    {
        when(userRepository.findFirstByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.validateAdminAccess("missing@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(userRepository).findFirstByEmail("missing@example.com");
    }

    @Test
    void validateAdminAccess_whenUserIsNotAdmin_shouldThrowException()
    {
        User user = buildUser(UUID.randomUUID(), "cliente", "cliente@example.com", Role.ROLE_CUSTOMER);

        when(userRepository.findFirstByEmail("cliente@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.validateAdminAccess("cliente@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No tienes permisos para gestionar usuarios");

        verify(userRepository).findFirstByEmail("cliente@example.com");
    }

    @Test
    void validateProfileAccess_whenEmailsAreEqual_shouldNotThrowException()
    {
        userService.validateProfileAccess("cliente@example.com", "cliente@example.com");
    }

    @Test
    void validateProfileAccess_whenEmailsAreDifferent_shouldThrowException()
    {
        assertThatThrownBy(() -> userService.validateProfileAccess("cliente@example.com", "otro@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No puedes acceder al perfil de otro usuario");
    }

    private UserRequestDto buildRegisterForm(
            String username,
            String email,
            String password,
            String passwordConfirm
    ) {
        return UserRequestDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .passwordConfirm(passwordConfirm)
                .build();
    }

    private User buildUser(UUID id, String username, String email, Role role)
    {
        return User.builder()
                .id(id)
                .username(username)
                .name("Nombre")
                .lastName("Apellido")
                .email(email)
                .password("encoded-password")
                .role(role)
                .active(true)
                .creationDate(LocalDateTime.now())
                .build();
    }
}