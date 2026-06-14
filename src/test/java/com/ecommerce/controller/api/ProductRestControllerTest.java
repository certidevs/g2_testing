package com.ecommerce.controller.api;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Quitamos los filtros de seguridad
@Transactional

class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    Product product1;
    Product product2;
    Review review1;
    Review review2;
    @BeforeEach
    void setUp() {
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
        review1 = reviewRepository.save(Review.builder()
                .product(product1)
                .rating(5)
                .message("Excelente producto")
                .build());
        review2 = reviewRepository.save(Review.builder()
                .product(product2)
                .rating(4)
                .message("Buen producto")
                .build());
    }

@Disabled
@Test
    void findAll() throws Exception{
        mockMvc.perform(get("/api/v1/products"))
                 .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(product1.getId().toString()))
                .andExpect(jsonPath("$[0].title").value(product1.getTitle()))
                .andExpect(jsonPath("$[0].price").value(product1.getPrice()))
                .andExpect(jsonPath("$[0].stock").value(product1.getStock()))
                .andExpect(jsonPath("$[0].available").value(product1.isAvailable()))

        ;

}

@Test
    void findById() throws Exception {
}

@Test
    void create() throws Exception {
}

@Test
    void update() throws Exception {
}


}