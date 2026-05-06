package com.ecommerce.repository;

import com.ecommerce.model.User;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp() {
        // Create test users with different data
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksAgo = now.minusWeeks(2);
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        
        user1 = userRepository.save(User.builder()
            .name("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .phone("123456789")
            .gender(Gender.MALE)
            .role(Role.CUSTOMER)
            .creationDate(now)
            .addresses(new ArrayList<>()) // Inicializar para evitar null
            .build());

        user2 = userRepository.save(User.builder()
            .name("Jane")
            .lastName("Smith")
            .email("jane.smith@example.com")
            .phone("987654321")
            .gender(Gender.FEMALE)
            .role(Role.ADMIN)
            .creationDate(twoWeeksAgo)
            .addresses(new ArrayList<>()) // Inicializar para evitar null
            .build());

        user3 = userRepository.save(User.builder()
            .name("Alice")
            .lastName("Johnson")
            .email("alice.johnson@example.com")
            .phone("555555555")
            .gender(Gender.FEMALE)
            .role(Role.CUSTOMER)
            .creationDate(oneMonthAgo)
            .addresses(new ArrayList<>()) // Inicializar para evitar null
            .build());
    }

    @Test
    void findById() {
        UUID userId = user1.getId();

        Optional<User> foundUser = userRepository.findById(userId);
        

        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
        assertEquals("John", foundUser.get().getName());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
    }

    @Test
    void findById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<User> foundUser = userRepository.findById(nonExistentId);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByName() {
        String name = "John";

        List<User> users = userRepository.findByName(name);

        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getName());
        assertEquals("Doe", users.get(0).getLastName());
    }

    @Test
    void findByName_NotFound() {
        String nonExistentName = "NonExistent";

        List<User> users = userRepository.findByName(nonExistentName);

        assertEquals(0, users.size());
    }

    @Test
    void findByEmail() {
        String email = "jane.smith@example.com";

        List<User> users = userRepository.findByEmail(email);

        assertEquals(1, users.size());
        assertEquals(email, users.get(0).getEmail());
        assertEquals("Jane", users.get(0).getName());
    }

    @Test
    void findByEmail_NotFound() {
        String nonExistentEmail = "nonexistent@example.com";

        List<User> users = userRepository.findByEmail(nonExistentEmail);

        assertEquals(0, users.size());
    }

    @Test
    void findByPhone() {
        String phone = "555555555";

        List<User> users = userRepository.findByPhone(phone);

        assertEquals(1, users.size());
        assertEquals(phone, users.get(0).getPhone());
        assertEquals("Alice", users.get(0).getName());
    }

    @Test
    void findByPhone_NotFound() {
        String nonExistentPhone = "000000000";

        List<User> users = userRepository.findByPhone(nonExistentPhone);

        assertEquals(0, users.size());
    }

    @Test
    void findByGender() {
        Gender gender = Gender.FEMALE;

        List<User> users = userRepository.findByGender(gender);

        assertEquals(2, users.size()); // Jane and Alice
        for (User user : users) {
            assertEquals(Gender.FEMALE, user.getGender());
        }
    }

    @Test
    void findByGender_NotFound() {
        Gender gender = Gender.OTHER;

        List<User> users = userRepository.findByGender(gender);

        assertEquals(0, users.size());
    }

    @Test
    void findByRole() {
        Role role = Role.CUSTOMER;

        List<User> users = userRepository.findByRole(role);

        assertEquals(2, users.size()); // John and Alice
        for (User user : users) {
            assertEquals(Role.CUSTOMER, user.getRole());
        }
    }

    @Test
    void findByRole_NotFound() {
        Role role = Role.ADMIN; // Solo hay 1 admin (Jane)

        List<User> users = userRepository.findByRole(role);

        assertEquals(1, users.size()); // Solo Jane es ADMIN
    }

    @Test
    void findByCreationDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(3);
        LocalDateTime endDate = LocalDateTime.now();

        List<User> users = userRepository.findByCreationDateBetween(startDate, endDate);

        assertEquals(2, users.size());

        for (User user : users) {
            assertTrue(user.getCreationDate().isAfter(startDate));
            assertTrue(user.getCreationDate().isBefore(endDate));
        }
    }

    @Test
    void findByCreationDateBetween_NoResults() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        List<User> users = userRepository.findByCreationDateBetween(startDate, endDate);

        assertEquals(0, users.size());
    }

    @Test
    void findByCreationDateBetween_AllUsers() {

        LocalDateTime startDate = LocalDateTime.now().minusMonths(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<User> users = userRepository.findByCreationDateBetween(startDate, endDate);

        assertEquals(3, users.size()); // All users
    }

}
