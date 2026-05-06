package com.ecommerce.controller;

import com.ecommerce.model.Review;
import com.ecommerce.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewControllerTest {
    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    MockMvc mockMvc;

    Review review1;

    @BeforeEach
    void setUp() {
        review1 = reviewRepository.save(Review.builder().title("Test").build());
    }

    @Test
    void deleteReview() throws Exception {
        UUID id = review1.getId();
        assertTrue(reviewRepository.existsById(id));
            mockMvc.perform(get("/reviews/delete/" + id))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/reviews"));

    }
}
