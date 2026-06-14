package com.ecommerce.repository;

import com.ecommerce.model.User;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save_whenUserIsValid_shouldPersistUserWithGeneratedId()
    {
        User user = buildUser("yepe", "yepe@example.com", Role.ROLE_CUSTOMER);

        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("yepe");
        assertThat(savedUser.getEmail()).isEqualTo("yepe@example.com");
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_CUSTOMER);
        assertThat(savedUser.isActive()).isTrue();
        assertThat(savedUser.getCreationDate()).isNotNull();
    }

    @Test
    void existsByUsername_whenUsernameExists_shouldReturnTrue()
    {
        persistUser(buildUser("yuca", "yuca@example.com", Role.ROLE_CUSTOMER));

        boolean exists = userRepository.existsByUsername("yuca");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_whenUsernameDoesNotExist_shouldReturnFalse()
    {
        boolean exists = userRepository.existsByUsername("unknown");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_whenEmailExists_shouldReturnTrue()
    {
        persistUser(buildUser("alba", "alba@example.com", Role.ROLE_CUSTOMER));

        boolean exists = userRepository.existsByEmail("alba@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_whenEmailDoesNotExist_shouldReturnFalse()
    {
        boolean exists = userRepository.existsByEmail("unknown@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void findByUsername_whenUsernameExists_shouldReturnUser()
    {
        persistUser(buildUser("jeanpaul", "jeanpaul@example.com", Role.ROLE_CUSTOMER));

        Optional<User> result = userRepository.findByUsername("jeanpaul");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("jeanpaul@example.com");
    }

    @Test
    void findByUsername_whenUsernameDoesNotExist_shouldReturnEmpty()
    {
        Optional<User> result = userRepository.findByUsername("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_whenEmailExists_shouldReturnUser()
    {
        persistUser(buildUser("kent", "kent@example.com", Role.ROLE_CUSTOMER));

        Optional<User> result = userRepository.findByEmail("kent@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("kent");
    }

    @Test
    void findByEmail_whenEmailDoesNotExist_shouldReturnEmpty()
    {
        Optional<User> result = userRepository.findByEmail("unknown@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void findFirstByEmail_whenEmailExists_shouldReturnUser()
    {
        persistUser(buildUser("shota", "shota@example.com", Role.ROLE_CUSTOMER));

        Optional<User> result = userRepository.findFirstByEmail("shota@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("shota");
    }

    @Test
    void findByName_whenNameExists_shouldReturnUsersWithThatName()
    {
        User user1 = buildUser("danger1", "danger1@example.com", Role.ROLE_CUSTOMER);
        user1.setName("Danger");

        User user2 = buildUser("danger2", "danger2@example.com", Role.ROLE_CUSTOMER);
        user2.setName("Danger");

        User user3 = buildUser("ana", "ana@example.com", Role.ROLE_CUSTOMER);
        user3.setName("Ana");

        persistUser(user1);
        persistUser(user2);
        persistUser(user3);

        List<User> result = userRepository.findByName("Danger");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("danger1", "danger2");
    }

    @Test
    void findByPhone_whenPhoneExists_shouldReturnUser()
    {
        User user = buildUser("alan", "alan@example.com", Role.ROLE_CUSTOMER);
        user.setPhone("600111222");
        persistUser(user);

        List<User> result = userRepository.findByPhone("600111222");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("alan");
    }

    @Test
    void findByGender_whenGenderExists_shouldReturnUsersWithThatGender()
    {
        Gender gender = Gender.values()[0];

        User user = buildUser("carlos", "carlos@example.com", Role.ROLE_CUSTOMER);
        user.setGender(gender);
        persistUser(user);

        List<User> result = userRepository.findByGender(gender);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGender()).isEqualTo(gender);
    }

    @Test
    void findByRole_whenRoleExists_shouldReturnUsersWithThatRole()
    {
        persistUser(buildUser("admin", "admin@example.com", Role.ROLE_ADMIN));
        persistUser(buildUser("customer", "customer@example.com", Role.ROLE_CUSTOMER));

        List<User> result = userRepository.findByRole(Role.ROLE_ADMIN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("admin");
    }

    @Test
    void findFirstByRole_whenAdminExists_shouldReturnAdmin()
    {
        persistUser(buildUser("customer", "customer@example.com", Role.ROLE_CUSTOMER));
        persistUser(buildUser("admin", "admin@example.com", Role.ROLE_ADMIN));

        Optional<User> result = userRepository.findFirstByRole(Role.ROLE_ADMIN);

        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void findFirstByOrderByCreationDateAsc_shouldReturnOldestUser()
    {
        User oldest = buildUser("oldest", "oldest@example.com", Role.ROLE_CUSTOMER);
        oldest.setCreationDate(LocalDateTime.now().minusDays(10));

        User newest = buildUser("newest", "newest@example.com", Role.ROLE_CUSTOMER);
        newest.setCreationDate(LocalDateTime.now());

        persistUser(newest);
        persistUser(oldest);

        Optional<User> result = userRepository.findFirstByOrderByCreationDateAsc();

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("oldest");
    }

    @Test
    void findByCreationDateBetween_whenUsersExistInRange_shouldReturnMatchingUsers()
    {
        User userInRange = buildUser("inrange", "inrange@example.com", Role.ROLE_CUSTOMER);
        userInRange.setCreationDate(LocalDateTime.of(2026, 1, 10, 12, 0));

        User userOutOfRange = buildUser("outrange", "outrange@example.com", Role.ROLE_CUSTOMER);
        userOutOfRange.setCreationDate(LocalDateTime.of(2025, 1, 10, 12, 0));

        persistUser(userInRange);
        persistUser(userOutOfRange);

        List<User> result = userRepository.findByCreationDateBetween(
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 31, 23, 59)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("inrange");
    }

    private User persistUser(User user)
    {
        User savedUser = entityManager.persistAndFlush(user);
        entityManager.clear();
        return savedUser;
    }

    private User buildUser(String username, String email, Role role)
    {
        return User.builder()
                .username(username)
                .name("Nombre " + username)
                .lastName("Apellido " + username)
                .email(email)
                .password("encoded-password")
                .role(role)
                .active(true)
                .creationDate(LocalDateTime.now())
                .build();
    }
}
