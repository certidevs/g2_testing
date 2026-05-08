package com.ecommerce.controller;

import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.model.Product;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewControllerTest {
    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductRepository productRepository;

    User user1;
    User adminUser;
    Product testProduct;
    Review review1;
    Review review2;
    Review review3;

    @BeforeEach
    void setUp() {
        // Limpiar solo reseñas existentes para evitar contaminación
        reviewRepository.deleteAll();
        
        // Crear usuarios de prueba
        user1 = User.builder()
                .name("User Test")
                .email("user@test.com")
                .role(com.ecommerce.model.enums.Role.CUSTOMER)
                .build();
        user1 = userRepository.save(user1);

        adminUser = User.builder()
                .name("Admin Test")
                .email("admin@test.com")
                .role(com.ecommerce.model.enums.Role.ADMIN)
                .build();
        adminUser = userRepository.save(adminUser);

        // Crear un producto de prueba para las reseñas
        testProduct = productRepository.save(com.ecommerce.model.Product.builder()
                .title("Producto de prueba")
                .price(100.0)
                .available(true)
                .build());

        // Crear reseñas de prueba
        review1 = Review.builder()
                .title("Excelente producto")
                .message("Muy bueno")
                .rating(5)
                .status(com.ecommerce.model.enums.ReviewStatus.APPROVED)
                .user(user1)
                .product(testProduct)
                .creationDate(LocalDateTime.now())
                .build();
        review1 = reviewRepository.save(review1);

        review2 = Review.builder()
                .title("Producto regular")
                .message("Está bien")
                .rating(3)
                .status(com.ecommerce.model.enums.ReviewStatus.PENDING_APPROVAL)
                .user(user1)
                .product(testProduct)
                .creationDate(LocalDateTime.now())
                .build();
        review2 = reviewRepository.save(review2);

        review3 = Review.builder()
                .title("Mal producto")
                .message("No me gustó")
                .rating(1)
                .status(com.ecommerce.model.enums.ReviewStatus.REJECTED)
                .user(user1)
                .product(testProduct)
                .creationDate(LocalDateTime.now())
                .build();
        review3 = reviewRepository.save(review3);
    }

    @Test
    void reviewsListAsAdmin() throws Exception {
        mockMvc.perform(get("/reviews").param("email", "admin@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/reviews-list"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attribute("isAdmin", true))
                .andExpect(model().attribute("reviews", hasSize(3)))
                .andExpect(model().attribute("currentUserEmail", "admin@test.com"));
    }

    @Test
    void reviewsListAsUser() throws Exception {
        mockMvc.perform(get("/reviews").param("email", "user@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/reviews-list"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attribute("isAdmin", false))
                .andExpect(model().attribute("reviews", hasSize(3))) // User ve todas sus reseñas
                .andExpect(model().attribute("currentUserEmail", "user@test.com"));
    }

    @Test
    void reviewsListWithoutEmail() throws Exception {
        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/reviews-list"))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attribute("isAdmin", false))
                .andExpect(model().attribute("reviews", hasSize(0))) // Sin email: lista vacía
                .andExpect(model().attribute("currentUserEmail", nullValue()));
    }

    @Test
    void reviewDetailFound() throws Exception {
        mockMvc.perform(get("/reviews/{id}", review1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/reviews-detail"))
                .andExpect(model().attributeExists("review"))
                .andExpect(model().attribute("review", hasProperty("id", is(review1.getId()))))
                .andExpect(model().attribute("review", hasProperty("title", is("Excelente producto"))));
    }

    @Test
    void reviewDetailNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(get("/reviews/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void reviewsByProductAsJson() throws Exception {
        mockMvc.perform(get("/reviews/product/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(review1.getId().toString())));
    }

    @Test
    void reviewsByRatingAsJson() throws Exception {
        mockMvc.perform(get("/reviews/rating/{rating}", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rating", is(5)));
    }

    @Test
    void deleteReview() throws Exception {
        UUID id = review1.getId();
        assertTrue(reviewRepository.existsById(id));
        mockMvc.perform(get("/reviews/delete/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/reviews*"));
    }

    @Test
    void deleteReviewNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        assertFalse(reviewRepository.existsById(randomId));
        mockMvc.perform(get("/reviews/delete/" + randomId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/reviews*"));
    }
}
