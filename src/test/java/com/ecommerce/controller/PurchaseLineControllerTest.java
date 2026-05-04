package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

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
class PurchaseLineControllerTest {

    @Autowired
    PurchaseLineRepository purchaseLineRepository;

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MockMvc mockMvc;

    Purchase purchase1;
    Purchase purchase2;
    Purchase purchase3;
    Purchase purchase4;
    PurchaseLine purchaseLine1;
    PurchaseLine purchaseLine2;
    PurchaseLine purchaseLine3;
    PurchaseLine purchaseLine4;
    Product product1;
    Product product2;
    Product product3;
    Product product4;

    @BeforeEach
    void setUp(){

        product1 = Product.builder().title("pr1").price(20.50).build();
        product2 = Product.builder().title("pr2").price(30.00).build();
        product3 = Product.builder().title("pr3").price(10.00).build();
        product4 = Product.builder().title("pr4").price(5.00).build();
        productRepository.saveAll(List.of(product1, product2, product3, product4));

        purchase1 = Purchase.builder().totalPrice(20.50).build();
        purchase2 = Purchase.builder().totalPrice(60.00).build();
        purchase3 = Purchase.builder().totalPrice(40.00).build();
        purchase4 = Purchase.builder().totalPrice(50.00).build();
        purchaseRepository.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));

        purchaseLine1 = PurchaseLine.builder()
                .quantity(1)
                .product(product1)
                .purchase(purchase1)
                .build();
        purchaseLine2 = PurchaseLine.builder()
                .quantity(2)
                .product(product1)
                .purchase(purchase2)
                .build();
        purchaseLine3 = PurchaseLine.builder()
                .quantity(4)
                .product(product3)
                .purchase(purchase3)
                .build();
        purchaseLine4 = PurchaseLine.builder()
                .quantity(10)
                .product(product4)
                .purchase(purchase1)
                .build();
        purchaseLineRepository.saveAll(List.of(purchaseLine1, purchaseLine2, purchaseLine3, purchaseLine4));
    }

    @Test
    void purchaseLinesFull() throws Exception {

        mockMvc.perform(get("/purchase_lines"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase_lines/purchase_line-list"))
                .andExpect(model().attributeExists("purchaseLines"));
    }

    @Test
    void purchaseLinesEmpty() throws Exception {

        purchaseLineRepository.deleteAll();

        mockMvc.perform(get("/purchase_lines"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase_lines/purchase_line-list"))
                .andExpect(model().attributeExists("purchaseLines"))
                .andExpect(model().attribute("purchaseLines", hasSize(0)));
    }

    @Test
    void purchaseLineIsPresentTrue() throws Exception {

        PurchaseLine purchaseLine = PurchaseLine.builder()
                .quantity(3)
                .product(product2)
                .purchase(purchase4)
                .build();
        purchaseLine = purchaseLineRepository.save(purchaseLine);

        mockMvc.perform(get("/purchase_lines"))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase_lines/purchase_line-list"))
                .andExpect(model().attributeExists("purchaseLines"))
                .andExpect(model().attribute("purchaseLines", hasItem(
                        hasProperty("id", is(purchaseLine.getId()))
                )));
    }

    @Test
    void purchaseLineIsPresentFalse() throws Exception {

        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/purchase_lines"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("purchaseLines", not(hasItem(
                        hasProperty("id", is(randomId))
                ))));
    }
}