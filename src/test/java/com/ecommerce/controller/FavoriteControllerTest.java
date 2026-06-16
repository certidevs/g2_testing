package com.ecommerce.controller;

import com.ecommerce.model.Favorite;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.FavoriteRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private User customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = userRepository.save(User.builder()
                .username("juli")
                .email("juli01@gmail.com")
                .password("password")
                .role(Role.ROLE_CUSTOMER)
                .build());

        product = productRepository.save(Product.builder()
                .title("Producto favorito")
                .shortDescription("Producto usado para probar favoritos")
                .price(25.0)
                .stock(8)
                .available(true)
                .build());
    }

    @Test
    void listFavoritesWithoutSessionRedirectsToLogin() throws Exception {
        // Si no hay Principal, el controlador no muestra la lista y redirige a login.
        mockMvc.perform(get("/favorites"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login*"));
    }

    @Test
    void listFavoritesWithSessionShowsFavoriteProducts() throws Exception {
        // Creamos un favorito previo para comprobar que la vista recibe la lista.
        favoriteRepository.save(Favorite.builder()
                .user(customer)
                .product(product)
                .build());

        mockMvc.perform(get("/favorites").principal(customerPrincipal()))
                .andExpect(status().isOk())
                .andExpect(view().name("favorite/favorites-list"))
                .andExpect(model().attributeExists("favoriteProducts"))
                .andExpect(model().attribute("saludo", "Mis productos favoritos"));
    }

    @Test
    void addFavoriteCreatesFavoriteAndRedirectsToProductDetail() throws Exception {
        // El POST debe guardar el favorito para el usuario autenticado.
        mockMvc.perform(post("/favorites/add/" + product.getId()).principal(customerPrincipal()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/products/" + product.getId() + "*"));

        assertTrue(favoriteRepository.findByUserIdAndProductId(customer.getId(), product.getId()).isPresent());
    }

    @Test
    void addFavoriteDoesNotCreateDuplicateFavorites() throws Exception {
        // Si el favorito ya existe, el controlador no debe crear otro registro igual.
        favoriteRepository.save(Favorite.builder()
                .user(customer)
                .product(product)
                .build());

        mockMvc.perform(post("/favorites/add/" + product.getId()).principal(customerPrincipal()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/products/" + product.getId() + "*"));

        assertEquals(1, favoriteRepository.findByUserIdWithProducts(customer.getId()).size());
    }

    @Test
    void removeFavoriteDeletesFavoriteAndRedirectsToFavorites() throws Exception {
        // Al eliminar, desaparece la relacion usuario-producto de la tabla favorites.
        favoriteRepository.save(Favorite.builder()
                .user(customer)
                .product(product)
                .build());

        mockMvc.perform(post("/favorites/remove/" + product.getId()).principal(customerPrincipal()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/favorites*"));

        assertTrue(favoriteRepository.findByUserIdAndProductId(customer.getId(), product.getId()).isEmpty());
    }

    @Test
    void checkFavoriteReturnsTrueWhenProductIsFavorite() throws Exception {
        // El endpoint auxiliar devuelve true cuando el producto esta guardado.
        favoriteRepository.save(Favorite.builder()
                .user(customer)
                .product(product)
                .build());

        mockMvc.perform(get("/favorites/check/" + product.getId()).principal(customerPrincipal()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkFavoriteReturnsFalseWhenProductIsNotFavorite() throws Exception {
        // Si no existe favorito para ese producto, el endpoint debe responder false.
        mockMvc.perform(get("/favorites/check/" + product.getId()).principal(customerPrincipal()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    private Principal customerPrincipal() {
        return () -> customer.getUsername();
    }
}
