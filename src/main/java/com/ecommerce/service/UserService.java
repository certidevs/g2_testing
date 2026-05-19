package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    /**
     * Obtiene todos los usuarios
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public String resolveAdminEmail(String adminEmail) {
        if (adminEmail != null && !adminEmail.isBlank()) {
            return adminEmail;
        }

        return userRepository.findFirstByRole(Role.ADMIN)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("No hay administradores disponibles"));
    }

    /**
     * Busca un usuario por email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    public User findProfileByEmail(String email) {
        return userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User findAnyProfileUser() {
        return userRepository.findFirstByOrderByCreationDateAsc()
                .orElseThrow(() -> new RuntimeException("No hay usuarios disponibles"));
    }

    /**
     * Busca un usuario por ID
     */
    public User findById(UUID id) {
        return findUserEntityById(id);
    }

    /**
     * Actualiza el perfil del usuario
     */
    public User updateProfile(UUID currentUserId, User updatedUser) {
        User currentUser = findUserEntityById(currentUserId);

        // Solo permite actualizar campos propios del perfil
        currentUser.setName(updatedUser.getName());
        currentUser.setLastName(updatedUser.getLastName());
        currentUser.setPhone(updatedUser.getPhone());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setGender(updatedUser.getGender());
        currentUser.setBirthday(updatedUser.getBirthday());

        return userRepository.save(currentUser);
    }

    /**
     * Elimina un usuario
     */
    public void delete(UUID id) {
        User user = findUserEntityById(id);
        userRepository.delete(user);
    }

    public User updateUserByAdmin(UUID userId, User updatedUser) {
        User currentUser = findUserEntityById(userId);

        if (updatedUser.getName() != null) currentUser.setName(updatedUser.getName());
        if (updatedUser.getLastName() != null) currentUser.setLastName(updatedUser.getLastName());
        if (updatedUser.getPhone() != null) currentUser.setPhone(updatedUser.getPhone());
        if (updatedUser.getEmail() != null) currentUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getGender() != null) currentUser.setGender(updatedUser.getGender());
        if (updatedUser.getBirthday() != null) currentUser.setBirthday(updatedUser.getBirthday());
        if (updatedUser.getPaymentMethod() != null) currentUser.setPaymentMethod(updatedUser.getPaymentMethod());
        if (updatedUser.getRole() != null) currentUser.setRole(updatedUser.getRole());

        return userRepository.save(currentUser);
    }

    public void toggleUserStatus(UUID userId) {
        User user = findUserEntityById(userId);
        validateTargetIsNotAdmin(user);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public void softDeleteUser(UUID userId) {
        User user = findUserEntityById(userId);
        validateTargetIsNotAdmin(user);
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Verifica si el usuario es ADMIN
     */
    public boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    /**
     * Valida que un usuario tenga permisos de administrador
     */
    public void validateAdminAccess(String email) {
        User user = findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!isAdmin(user)) {
            throw new RuntimeException("No tienes permisos para gestionar usuarios");
        }
   }

    /**
     * Valida que un usuario pueda acceder a su propio perfil
     */
    public void validateProfileAccess(String currentUserEmail, String targetEmail) {
        if (!currentUserEmail.equals(targetEmail)) {
            throw new RuntimeException("No puedes acceder al perfil de otro usuario");
        }
    }

    private User findUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void validateTargetIsNotAdmin(User targetUser) {
        if (targetUser.getRole() == Role.ADMIN) {
            throw new RuntimeException("No se puede modificar el estado de otro administrador");
        }
    }
}
