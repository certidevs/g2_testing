package com.ecommerce.repository;

import com.ecommerce.model.Users;
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
public class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    Users user1;
    Users user2;
    Users user3;

    @BeforeEach
    void setUp() {
        // Create test users with different data
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksAgo = now.minusWeeks(2);
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        
        user1 = usersRepository.save(Users.builder()
            .name("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .phone("123456789")
            .gender(Gender.MALE)
            .role(Role.CUSTOMER)
            .creationDate(now)
            .addresses(new ArrayList<>()) // Inicializar para evitar null
            .build());

        user2 = usersRepository.save(Users.builder()
            .name("Jane")
            .lastName("Smith")
            .email("jane.smith@example.com")
            .phone("987654321")
            .gender(Gender.FEMALE)
            .role(Role.ADMIN)
            .creationDate(twoWeeksAgo)
            .addresses(new ArrayList<>()) // Inicializar para evitar null
            .build());

        user3 = usersRepository.save(Users.builder()
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

        Optional<Users> foundUser = usersRepository.findById(userId);
        

        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
        assertEquals("John", foundUser.get().getName());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
    }

    @Test
    void findById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Users> foundUser = usersRepository.findById(nonExistentId);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByName() {
        String name = "John";

        List<Users> users = usersRepository.findByName(name);

        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getName());
        assertEquals("Doe", users.get(0).getLastName());
    }

    @Test
    void findByName_NotFound() {
        String nonExistentName = "NonExistent";

        List<Users> users = usersRepository.findByName(nonExistentName);

        assertEquals(0, users.size());
    }

    @Test
    void findByEmail() {
        String email = "jane.smith@example.com";

        List<Users> users = usersRepository.findByEmail(email);

        assertEquals(1, users.size());
        assertEquals(email, users.get(0).getEmail());
        assertEquals("Jane", users.get(0).getName());
    }

    @Test
    void findByEmail_NotFound() {
        String nonExistentEmail = "nonexistent@example.com";

        List<Users> users = usersRepository.findByEmail(nonExistentEmail);

        assertEquals(0, users.size());
    }

    @Test
    void findByPhone() {
        String phone = "555555555";

        List<Users> users = usersRepository.findByPhone(phone);

        assertEquals(1, users.size());
        assertEquals(phone, users.get(0).getPhone());
        assertEquals("Alice", users.get(0).getName());
    }

    @Test
    void findByPhone_NotFound() {
        String nonExistentPhone = "000000000";

        List<Users> users = usersRepository.findByPhone(nonExistentPhone);

        assertEquals(0, users.size());
    }

    @Test
    void findByGender() {
        Gender gender = Gender.FEMALE;

        List<Users> users = usersRepository.findByGender(gender);

        assertEquals(2, users.size()); // Jane and Alice
        for (Users user : users) {
            assertEquals(Gender.FEMALE, user.getGender());
        }
    }

    @Test
    void findByGender_NotFound() {
        Gender gender = Gender.OTHER;

        List<Users> users = usersRepository.findByGender(gender);

        assertEquals(0, users.size());
    }

    @Test
    void findByRole() {
        Role role = Role.CUSTOMER;

        List<Users> users = usersRepository.findByRole(role);

        assertEquals(2, users.size()); // John and Alice
        for (Users user : users) {
            assertEquals(Role.CUSTOMER, user.getRole());
        }
    }

    @Test
    void findByRole_NotFound() {
        Role role = Role.ADMIN; // Solo hay 1 admin (Jane)

        List<Users> users = usersRepository.findByRole(role);

        assertEquals(1, users.size()); // Solo Jane es ADMIN
    }

    @Test
    void findByCreationDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(3);
        LocalDateTime endDate = LocalDateTime.now();

        List<Users> users = usersRepository.findByCreationDateBetween(startDate, endDate);

        assertEquals(2, users.size());

        for (Users user : users) {
            assertTrue(user.getCreationDate().isAfter(startDate));
            assertTrue(user.getCreationDate().isBefore(endDate));
        }
    }

    @Test
    void findByCreationDateBetween_NoResults() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        List<Users> users = usersRepository.findByCreationDateBetween(startDate, endDate);

        assertEquals(0, users.size());
    }

    @Test
    void findByCreationDateBetween_AllUsers() {

        LocalDateTime startDate = LocalDateTime.now().minusMonths(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Users> users = usersRepository.findByCreationDateBetween(startDate, endDate);

        assertEquals(3, users.size()); // All users
    }

}
