package com.ecommerce.repository;

import com.ecommerce.model.Address;
import com.ecommerce.model.Users;
import com.ecommerce.model.enums.AddressType;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private UsersRepository usersRepository;

    Users user1;
    Users user2;
    Address address1;
    Address address2;
    Address address3;
    Address address4;

    @BeforeEach
    void setUp() {
        // Create test users
        user1 = usersRepository.save(Users.builder()
            .name("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .phone("123456789")
            .gender(Gender.MALE)
            .role(Role.CUSTOMER)
            .creationDate(java.time.LocalDateTime.now())
            .addresses(new ArrayList<>())
            .build());

        user2 = usersRepository.save(Users.builder()
            .name("Jane")
            .lastName("Smith")
            .email("jane.smith@example.com")
            .phone("987654321")
            .gender(Gender.FEMALE)
            .role(Role.ADMIN)
            .creationDate(java.time.LocalDateTime.now())
            .addresses(new ArrayList<>())
            .build());

        // Create test addresses
        address1 = addressRepository.save(Address.builder()
            .street("Main Street")
            .number("123")
            .city("Madrid")
            .state("Madrid")
            .zipCode("28001")
            .country("Spain")
            .addressType(AddressType.PRIMARY)
            .user(user1)
            .build());

        address2 = addressRepository.save(Address.builder()
            .street("Secondary Street")
            .number("456")
            .city("Barcelona")
            .state("Catalonia")
            .zipCode("08001")
            .country("Spain")
            .addressType(AddressType.SECONDARY)
            .user(user1)
            .build());

        address3 = addressRepository.save(Address.builder()
            .street("Billing Avenue")
            .number("789")
            .city("Madrid")
            .state("Madrid")
            .zipCode("28002")
            .country("Spain")
            .addressType(AddressType.BILLING)
            .user(user2)
            .build());

        address4 = addressRepository.save(Address.builder()
            .street("Shipping Road")
            .number("101")
            .city("Valencia")
            .state("Valencia")
            .zipCode("46001")
            .country("Spain")
            .addressType(AddressType.SHIPPING)
            .user(user2)
            .build());
    }

    @Test
    void findById() {
        UUID addressId = address1.getId();

        Optional<Address> foundAddress = addressRepository.findById(addressId);

        assertTrue(foundAddress.isPresent());
        assertEquals(addressId, foundAddress.get().getId());
        assertEquals("Main Street", foundAddress.get().getStreet());
        assertEquals("Madrid", foundAddress.get().getCity());
    }

    @Test
    void findById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Address> foundAddress = addressRepository.findById(nonExistentId);

        assertFalse(foundAddress.isPresent());
    }

    @Test
    void findByUser() {
        List<Address> addresses = addressRepository.findByUser(user1);

        assertEquals(2, addresses.size());
        for (Address address : addresses) {
            assertEquals(user1, address.getUser());
        }
    }

    @Test
    void findByUser_NotFound() {
        Users nonExistentUser = Users.builder()
            .id(UUID.randomUUID())
            .name("Non")
            .lastName("Existent")
            .build();

        List<Address> addresses = addressRepository.findByUser(nonExistentUser);

        assertEquals(0, addresses.size());
    }

    @Test
    void findByCity() {
        String city = "Madrid";

        List<Address> addresses = addressRepository.findByCity(city);

        assertEquals(2, addresses.size()); // address1 and address3
        for (Address address : addresses) {
            assertEquals(city, address.getCity());
        }
    }

    @Test
    void findByCity_NotFound() {
        String nonExistentCity = "NonExistentCity";

        List<Address> addresses = addressRepository.findByCity(nonExistentCity);

        assertEquals(0, addresses.size());
    }

    @Test
    void findByState() {
        String state = "Madrid";

        List<Address> addresses = addressRepository.findByState(state);

        assertEquals(2, addresses.size()); // address1 and address3
        for (Address address : addresses) {
            assertEquals(state, address.getState());
        }
    }

    @Test
    void findByState_NotFound() {
        String nonExistentState = "NonExistentState";

        List<Address> addresses = addressRepository.findByState(nonExistentState);

        assertEquals(0, addresses.size());
    }

    @Test
    void findByCountry() {
        String country = "Spain";

        List<Address> addresses = addressRepository.findByCountry(country);

        assertEquals(4, addresses.size()); // All addresses
        for (Address address : addresses) {
            assertEquals(country, address.getCountry());
        }
    }

    @Test
    void findByCountry_NotFound() {
        String nonExistentCountry = "NonExistentCountry";

        List<Address> addresses = addressRepository.findByCountry(nonExistentCountry);

        assertEquals(0, addresses.size());
    }

    @Test
    void findByZipCode() {
        String zipCode = "28001";

        List<Address> addresses = addressRepository.findByZipCode(zipCode);

        assertEquals(1, addresses.size());
        assertEquals(zipCode, addresses.get(0).getZipCode());
        assertEquals("Main Street", addresses.get(0).getStreet());
    }

    @Test
    void findByZipCode_NotFound() {
        String nonExistentZipCode = "00000";

        List<Address> addresses = addressRepository.findByZipCode(nonExistentZipCode);

        assertEquals(0, addresses.size());
    }

    @Test
    void findByUserAndCity() {
        List<Address> addresses = addressRepository.findByUserAndCity(user1, "Madrid");

        assertEquals(1, addresses.size());
        assertEquals(user1, addresses.get(0).getUser());
        assertEquals("Madrid", addresses.get(0).getCity());
        assertEquals("Main Street", addresses.get(0).getStreet());
    }

    @Test
    void findByUserAndCity_NotFound() {
        List<Address> addresses = addressRepository.findByUserAndCity(user1, "Valencia");

        assertEquals(0, addresses.size());
    }

    @Test
    void findByUserAndCountry() {
        List<Address> addresses = addressRepository.findByUserAndCountry(user2, "Spain");

        assertEquals(2, addresses.size()); // address3 and address4
        for (Address address : addresses) {
            assertEquals(user2, address.getUser());
            assertEquals("Spain", address.getCountry());
        }
    }

    @Test
    void findByUserAndCountry_NotFound() {
        List<Address> addresses = addressRepository.findByUserAndCountry(user1, "France");

        assertEquals(0, addresses.size());
    }

    @Test
    void countByUser() {
        Long count = addressRepository.countByUser(user1);

        assertEquals(2, count);
    }

    @Test
    void countByUser_NoAddresses() {
        Users userWithoutAddresses = usersRepository.save(Users.builder()
            .name("Alice")
            .lastName("Johnson")
            .email("alice.johnson@example.com")
            .phone("555555555")
            .gender(Gender.FEMALE)
            .role(Role.CUSTOMER)
            .creationDate(java.time.LocalDateTime.now())
            .addresses(new ArrayList<>())
            .build());

        Long count = addressRepository.countByUser(userWithoutAddresses);

        assertEquals(0, count);
    }

    @Test
    void findByAddressType() {
        List<Address> addresses = addressRepository.findByAddressType(AddressType.PRIMARY);

        assertEquals(1, addresses.size());
        assertEquals(AddressType.PRIMARY, addresses.get(0).getAddressType());
        assertEquals("Main Street", addresses.get(0).getStreet());
    }

    @Test
    void findByAddressType_NotFound() {
        List<Address> addresses = addressRepository.findByAddressType(AddressType.BILLING);

        assertEquals(1, addresses.size()); // address3 is BILLING
        assertEquals(AddressType.BILLING, addresses.get(0).getAddressType());
    }

    @Test
    void findByUserAndAddressType() {
        List<Address> addresses = addressRepository.findByUserAndAddressType(user1, AddressType.PRIMARY);

        assertEquals(1, addresses.size());
        assertEquals(user1, addresses.get(0).getUser());
        assertEquals(AddressType.PRIMARY, addresses.get(0).getAddressType());
        assertEquals("Main Street", addresses.get(0).getStreet());
    }

    @Test
    void findByUserAndAddressType_NotFound() {
        List<Address> addresses = addressRepository.findByUserAndAddressType(user1, AddressType.SHIPPING);

        assertEquals(0, addresses.size());
    }

    @Test
    void findByUserAndAddressType_MultipleResults() {
        // Add another PRIMARY address for user1 to test multiple results
        Address anotherPrimary = addressRepository.save(Address.builder()
            .street("Another Primary")
            .number("999")
            .city("Madrid")
            .state("Madrid")
            .zipCode("28003")
            .country("Spain")
            .addressType(AddressType.PRIMARY)
            .user(user1)
            .build());

        List<Address> addresses = addressRepository.findByUserAndAddressType(user1, AddressType.PRIMARY);

        assertEquals(2, addresses.size());
        for (Address address : addresses) {
            assertEquals(user1, address.getUser());
            assertEquals(AddressType.PRIMARY, address.getAddressType());
        }
    }
}
