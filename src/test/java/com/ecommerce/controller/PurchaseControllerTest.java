package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.UserRepository;
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
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PurchaseControllerTest {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    User user1;
    User user2;

    Product product1;
    Product product2;

    Purchase purchase1;
    Purchase purchase2;
    Purchase purchase3;
    Purchase purchase4;

    @BeforeEach
    void setUp(){

        user1 = User.builder()
                .name("User 1")
                .lastName("Last Name 1")
                .email("user1@gmail.com")
                .phone("123456789")
                .password("password1")
                .birthday(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .gender(Gender.MALE)
                .role(Role.CUSTOMER)
                .build();

        user2 = User.builder()
                .name("User 2")
                .lastName("Last Name 2")
                .email("user2@gmail.com")
                .phone("987654321")
                .password("password2")
                .birthday(LocalDateTime.of(1995, Month.JUNE, 15, 0, 0))
                .gender(Gender.FEMALE)
                .role(Role.CUSTOMER)
                .build();

        userRepository.saveAll(List.of(user1, user2));

        product1 = Product.builder()
                .title("Product 1")
                .available(true)
                .price(20.00)
                .purchase(purchase1)
                .purchase(purchase3)
                .build();

        product2 = Product.builder()
                .title("Product 2")
                .available(true)
                .price(10.00)
                .purchase(purchase2)
                .purchase(purchase4)
                .build();

        productRepository.saveAll(List.of(product1, product2));

        purchase1 = Purchase.builder()
                .user(user1)
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 15, 12, 45))
                .finishedDate(LocalDateTime.of(2026, Month.APRIL, 28, 17, 30))
                .purchaseStatus(PurchaseStatus.FINISHED)
                .paymentStatus(PaymentStatus.PAID)
                .processStatus(ProcessStatus.COMPLETED)
                .shippingStatus(ShippingStatus.DELIVERED)
                .shippingMode(ShippingMode.STANDARD)
                .totalPrice(50.00)
                .userComment("Me ha llegado el producto en mal estado")
                .build();

        purchase2 = Purchase.builder()
                .user(user2)
                .creationDate(LocalDateTime.of(2025, Month.JUNE, 10, 18, 35))
                .finishedDate(LocalDateTime.of(2025, Month.DECEMBER, 25, 16, 15))
                .purchaseStatus(PurchaseStatus.FINISHED)
                .paymentStatus(PaymentStatus.PAID)
                .processStatus(ProcessStatus.COMPLETED)
                .shippingStatus(ShippingStatus.DELIVERED)
                .shippingMode(ShippingMode.EXPRESS)
                .totalPrice(15.45)
                .userComment("El producto ha llegado bien pero he tardado mucho más de lo esperado teniendo en cuenta que era EXPRESS")
                .build();

        purchase3 = Purchase.builder()
                .user(user1)
                .creationDate(LocalDateTime.of(2026, Month.FEBRUARY, 10, 11, 50))
                .finishedDate(null)
                .purchaseStatus(PurchaseStatus.INITIATED)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.PENDING)
                .shippingStatus(ShippingStatus.PENDING)
                .shippingMode(ShippingMode.PREMIUM)
                .totalPrice(150.75)
                .userComment(null)
                .build();

        purchase4 = Purchase.builder()
                .user(user2)
                .creationDate(LocalDateTime.of(2020, Month.MAY, 30, 8, 30))
                .finishedDate(null)
                .purchaseStatus(PurchaseStatus.INACTIVE)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.PENDING)
                .shippingStatus(ShippingStatus.PENDING)
                .shippingMode(ShippingMode.STANDARD)
                .totalPrice(73.00)
                .userComment(null)
                .build();

        purchaseRepository.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));
    }

    @Test
    void purchasesFull() throws Exception {
        mockMvc.perform(get("/purchases"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/purchases-list"))
                .andExpect(model().attributeExists("purchases"))
                .andExpect(model().attribute("purchases", hasSize(4)));
    }

    @Test
    void purchasesEmpty() throws Exception {
        purchaseRepository.deleteAll();

        mockMvc.perform(get("/purchases"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/purchases-list"))
                .andExpect(model().attributeExists("purchases"))
                .andExpect(model().attribute("purchases", hasSize(0)));
    }

    @Test
    void purchaseDetailIsPresentTrue() throws Exception {

        Purchase purchase = new Purchase();
        purchase = purchaseRepository.save(purchase);

        mockMvc.perform(get("/purchases/{id}", purchase.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/purchase-detail"))
                .andExpect(model().attributeExists("purchase"))
                .andExpect(model().attribute("purchase", hasProperty("id", is(purchase.getId()))));
    }

    @Test
    void purchaseDetailIsPresentFalse() throws Exception {

        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/purchases/{id}", randomId))
                .andExpect(status().isNotFound());
    }
}