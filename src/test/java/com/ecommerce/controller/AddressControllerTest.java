package com.ecommerce.controller;

import com.ecommerce.model.Address;
import com.ecommerce.model.Users;
import com.ecommerce.model.enums.AddressType;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AddressControllerTest {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    MockMvc mockMvc;

    Users user1;
    Address address1;
    Address address2;
    Address address3;

    @BeforeEach
    void setUp() {
        user1 = usersRepository.save(Users.builder()
                .name("User 1")
                .lastName("Last Name 1")
                .email("user1@gmail.com")
                .phone("123456789")
                .password("password1")
                .birthday(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .gender(Gender.MALE)
                .role(Role.CUSTOMER)
                .build());

        address1 = Address.builder()
                .street("Calle Mayor")
                .number("10")
                .city("Madrid")
                .state("Madrid")
                .zipCode("28001")
                .country("España")
                .addressType(AddressType.PRIMARY)
                .user(user1)
                .build();

        address2 = Address.builder()
                .street("Avenida Diagonal")
                .number("55")
                .city("Barcelona")
                .state("Cataluña")
                .zipCode("08019")
                .country("España")
                .addressType(AddressType.SHIPPING)
                .user(user1)
                .build();

        address3 = Address.builder()
                .street("Gran Via")
                .number("1")
                .city("Bilbao")
                .state("Pais Vasco")
                .zipCode("48001")
                .country("España")
                .addressType(AddressType.BILLING)
                .user(user1)
                .build();

        addressRepository.saveAll(List.of(address1, address2, address3));
    }

    @Test
    void listAddressesFull() throws Exception {
        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(view().name("addresses/addresses-list"))
                .andExpect(model().attributeExists("addresses"))
                .andExpect(model().attribute("addresses", hasSize(3)));
    }

    @Test
    void listAddressesEmpty() throws Exception {
        addressRepository.deleteAll();

        mockMvc.perform(get("/addresses"))
                .andExpect(status().isOk())
                .andExpect(view().name("addresses/addresses-list"))
                .andExpect(model().attributeExists("addresses"))
                .andExpect(model().attribute("addresses", hasSize(0)));
    }

    @Test
    void addressDetailFound() throws Exception {
        mockMvc.perform(get("/addresses/{id}", address1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("addresses/address-detail"))
                .andExpect(model().attributeExists("address"))
                .andExpect(model().attribute("address", hasProperty("id", is(address1.getId()))))
                .andExpect(model().attribute("address", hasProperty("city", is("Madrid"))))
                .andExpect(model().attribute("address", hasProperty("addressType", is(AddressType.PRIMARY))));
    }

    @Test
    void addressDetailNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/addresses/{id}", randomId))
                .andExpect(status().isNotFound());
    }
}

