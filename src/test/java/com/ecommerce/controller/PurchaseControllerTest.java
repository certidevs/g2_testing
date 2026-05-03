package com.ecommerce.controller;

import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PurchaseControllerTest {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    PurchaseLineRepository purchaseLineRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp(){

    }

    @Test
    void purchases() {
    }

    @Test
    void purchase() {
    }
}