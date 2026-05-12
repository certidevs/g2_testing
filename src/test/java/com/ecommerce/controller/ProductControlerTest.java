package com.ecommerce.controller;

import com.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Activa Spring
@AutoConfigureMockMvc // Activa MockMvc para testing de controller
@Transactional // deshace los cambios al final de cada test para no afectar al siguiente test

class ProductControlerTest {

    @Autowired
    ProductControler productControler;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
    }

    @Test
    void products() {
    }

    @Test
    void productsDetail() {
    }

    @Test
    void searchProducts() {
    }

    @Test
    void deactivateRestaurant() {
    }

    @Test
    void activateProduct() {
    }

    @Test
    void navigateToForm() {
    }

//    @Test
//    void createProduct() throws Exception {
//        //count products
//        long before = productRepository.count();
//        //mockmvc perform para enviar producto nuevo a controller
//        mockMvc.perform(post("/products")
//                .param("title", "Producto de prueba")
//                .param("shortDescription", "Descripción corta de prueba")
//                .param("longDescription", "Descripción larga de prueba")
//                .param("price", "9.99")
//                .param("stockStatus", "IN_STOCK")
//        ).andExpect(status().is3xxRedirection())
//         .andExpect(redirectedUrl("/products"));
//
//
//        //count products
//        long after = productRepository.count();
//    }
}