package com.ecommerce.controller.api;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        productRepository.deleteAll();

        product1 = productRepository.save(Product.builder()
                .title("Producto 1")
                .price(10.0)
                .stock(5)
                .available(true)
                .build());

        product2 = productRepository.save(Product.builder()
                .title("Producto 2")
                .price(20.0)
                .stock(10)
                .available(true)
                .build());
    }

    @Test
    void findAll_shouldReturnAllProducts() throws Exception {
        mockMvc.perform(get("/api/v1/product/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", hasItem(product1.getId().toString())))
                .andExpect(jsonPath("$[*].id", hasItem(product2.getId().toString())))
                .andExpect(jsonPath("$[*].title", hasItem("Producto 1")))
                .andExpect(jsonPath("$[*].title", hasItem("Producto 2")))
                .andExpect(jsonPath("$[*].price", hasItem(10.0)))
                .andExpect(jsonPath("$[*].price", hasItem(20.0)))
                .andExpect(jsonPath("$[*].stock", hasItem(5)))
                .andExpect(jsonPath("$[*].stock", hasItem(10)))
                .andExpect(jsonPath("$[*].available", hasItem(true)));
    }


}