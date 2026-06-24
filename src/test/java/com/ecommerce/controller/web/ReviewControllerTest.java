package com.ecommerce.controller.web;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewControllerTest {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ProductRepository productRepo;


    @Autowired
    private MockMvc mockMvc;

    private Product product;
    private Review review;
    User user;
    User admin;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Prepara usuarios producto y review antes de cada test
    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .username("user")
                .email("user@gmail.com")
                .password(passwordEncoder.encode("useruseruserA*"))
                .role(Role.ROLE_CUSTOMER)
                .build());
        admin = userRepository.save(User.builder()
                .username("adminadmin")
                .email("adminadmin@gmail.com")
                .password(passwordEncoder.encode("adminadminadmAin8*"))
                .role(Role.ROLE_ADMIN)
                .build());
        product = Product.builder()
                .title("Teclado Gaming")
                .price(59.99)
                .stock(10)
                .build();
        product = productRepo.save(product);

        review = Review.builder()
                .title("Buen product")
                .rating(4)
                .message("Me ha gustado bastante")
                .product(product)
                .build();
        review = reviewRepo.save(review);
    }

    @Test
    // Verifica que el listado de reviews se muestra correctamente
    void reviews() throws Exception {
        // GET /reviews -> Comprobamos que carga el listado
        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/reviews-list"));
    }

    @Test
    // Verifica que el detalle de una review se muestra correctamente
    void reviewsDetail() throws Exception {
        // GET /reviews/{id} -> Detalle de la review que guardamos en el setUp
        mockMvc.perform(get("/reviews/" + review.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews/reviews-detail"))
                .andExpect(model().attributeExists("review"));
    }

    @Test
    // Verifica que un usuario puede añadir una review a un producto
    void addReview() throws Exception {
        long totalAntes = reviewRepo.count();

        // POST /products/{id}/reviews/add -> El formulario del modal para añadir opiniones
        mockMvc.perform(post("/products/" + product.getId() + "/reviews/add")
                        .with(csrf())
                        .with(user(user))
                        .param("title", "Me encantó")
                        .param("rating", "5")
                        .param("title", "ok")
                        .param("message", "Excelente compra"))
                .andExpect(status().is3xxRedirection());

        // Verificamos que se haya guardado una fila más en la base de datos
        assertEquals(totalAntes + 1, reviewRepo.count());
    }

    @Test
    // Verifica que un administrador puede actualizar una review
    void updateReview() throws Exception {
        //  Añadimos .with(csrf()) al final del post()
        mockMvc.perform(post("/reviews/edit/" + review.getId()).with(csrf()).with(user(admin))
                        .param("title", "Título Editado")
                        .param("rating", "5")
                        .param("message", "Mensaje Editado"))
                .andExpect(status().is3xxRedirection());

        Review reviewEdit = reviewRepo.findById(review.getId()).orElseThrow();
        assertEquals("Título Editado", reviewEdit.getTitle());
        assertEquals(5, reviewEdit.getRating());
        assertEquals("Mensaje Editado", reviewEdit.getMessage());
    }

//    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    // Verifica que un administrador puede eliminar una review
    void deleteReview() throws Exception {
        UUID id = review.getId();
        assertTrue(reviewRepo.existsById(id));

        // GET /reviews/delete/{id} -> Simula pulsar el botón de eliminar
        mockMvc.perform(get("/reviews/delete/" + id).with(user(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/reviews*"));

        // Verificamos que ya no exista en la base de datos
        assertFalse(reviewRepo.existsById(id));
    }
}