package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
    Product productToDeactivate;
    Product productToActivate;


    @BeforeEach
    void setUp() {
        productToDeactivate = new Product();
        productToDeactivate.setTitle("Producto a desactivar");
        productToDeactivate.setAvailable(true);
        productToDeactivate.setPrice(10.0);
        productToDeactivate = productRepository.save(productToDeactivate);

        productToActivate = new Product();
        productToActivate.setTitle("Producto a activar");
        productToActivate.setAvailable(false);
        productToActivate.setPrice(20.0);
        productToActivate = productRepository.save(productToActivate);
    }

    @Test
    void productsDetail() throws Exception {
        UUID id = productToDeactivate.getId();
        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-detail"));
    }

    @Test
    void searchProducts() throws Exception {
        mockMvc.perform(get("/products/search").param("query", "Producto"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-list"));
    }

    @Test
    void deactivateProduct() throws Exception {
        assertTrue(productToDeactivate.isAvailable());

        UUID id = productToDeactivate.getId();

        mockMvc.perform(get("/products/deactivate/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/products*"));

        // traer producto de base de datos y comprobar que available es false
        Product productDB = productRepository.findById(id).orElseThrow();
        assertFalse(productDB.isAvailable());

    }

    @Test
    void activateProduct() throws Exception {
        assertFalse(productToActivate.isAvailable());

        UUID id = productToActivate.getId();

        mockMvc.perform(get("/products/activate/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/products*"));

        // traer producto de base de datos y comprobar que available es true
        Product productDB = productRepository.findById(id).orElseThrow();
        assertTrue(productDB.isAvailable());

    }

    @Test
    void navigateToForm() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-form"));
    }

    @Test
    void createProduct() throws Exception {
        //count products
        long before = productRepository.count();
        //mockmvc perform para enviar producto nuevo a controller
        mockMvc.perform(post("/products")
                .param("title", "Producto de prueba")
                .param("shortDescription", "Descripción corta de prueba")
                .param("longDescription", "Descripción larga de prueba")
                .param("price", "9.99")
                .param("stockStatus", "STOCK")
        ).andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrlPattern("/products*"));


        //count products
        long after = productRepository.count();
        assertEquals(before + 1, after);
    }

    @Test
    void products() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-list"));
    }
}