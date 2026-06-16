package com.ecommerce.repository;

import com.ecommerce.model.Favorite;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private User customer;
    private User otherCustomer;
    private Product product;
    private Product otherProduct;

    @BeforeEach
    void setUp() {
        customer = userRepository.save(User.builder()
                .username("paco")
                .email("paco01@gmail.com")
                .password("password")
                .role(Role.ROLE_CUSTOMER)
                .build());

        otherCustomer = userRepository.save(User.builder()
                .username("luci")
                .email("luci01@gmail.com")
                .password("password")
                .role(Role.ROLE_CUSTOMER)
                .build());

        product = productRepository.save(Product.builder()
                .title("ProductoFavo")
                .price(35.0)
                .stock(5)
                .available(true)
                .build());

        otherProduct = productRepository.save(Product.builder()
                .title("ProductoFaco2")
                .price(45.0)
                .stock(3)
                .available(true)
                .build());
    }

    @Test
    void findByUserIdWithProductsReturnsOnlyFavoritesFromUser() {
        // Guarda favoritos de dos usuarios para comprobar que el filtro por usuario funciona.
        favoriteRepository.save(Favorite.builder()
                .user(customer)
                .product(product)
                .build());
        favoriteRepository.save(Favorite.builder()
                .user(otherCustomer)
                .product(otherProduct)
                .build());

        List<Favorite> favorites = favoriteRepository.findByUserIdWithProducts(customer.getId());

        assertEquals(1, favorites.size());
        assertEquals(product.getId(), favorites.getFirst().getProduct().getId());
    }

    @Test
    void findByUserIdAndProductIdReturnsFavoriteWhenItExists() {
        // Busca por la pareja exacta usuario-producto.
        favoriteRepository.save(Favorite.builder()
                .user(customer)
                .product(product)
                .build());

        Optional<Favorite> favorite = favoriteRepository.findByUserIdAndProductId(customer.getId(), product.getId());

        assertTrue(favorite.isPresent());
        assertEquals(customer.getId(), favorite.get().getUser().getId());
        assertEquals(product.getId(), favorite.get().getProduct().getId());
    }

    @Test
    void findByUserIdAndProductIdReturnsEmptyWhenFavoriteDoesNotExist() {
        // Si no hay relacion guardada, el repositorio debe devolver Optional vacio.
        Optional<Favorite> favorite = favoriteRepository.findByUserIdAndProductId(customer.getId(), product.getId());

        assertTrue(favorite.isEmpty());
    }

    @Test
    void countByUserIdCountsOnlyFavoritesFromThatUser() {
        // Comprueba que el contador no mezcla favoritos de otros usuarios.
        favoriteRepository.save(Favorite.builder()
                .user(customer)
                .product(product)
                .build());
        favoriteRepository.save(Favorite.builder()
                .user(customer)
                .product(otherProduct)
                .build());
        favoriteRepository.save(Favorite.builder()
                .user(otherCustomer)
                .product(product)
                .build());

        assertEquals(2, favoriteRepository.countByUserId(customer.getId()));
        assertEquals(1, favoriteRepository.countByUserId(otherCustomer.getId()));
    }
}
