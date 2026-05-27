package com.ecommerce.repository;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;



import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product producto;

    @BeforeEach
    void setUp() {
        producto = new Product();
        producto.setTitle("Producto Prueba");
        producto.setPrice(20.0);
        producto.setStock(5);
        producto = productRepository.save(producto);
    }

    @Test
    void findByProduct_IdOrderByCreationDateDesc() {
        // Creamos una reseña antigua (hace 2 días)
        Review antigua = new Review();
        antigua.setRating(3);
        antigua.setMessage("Regular");
        antigua.setProduct(producto);
        antigua.setCreationDate(LocalDateTime.now().minusDays(2));

        // Creamos una reseña nueva (ahora mismo)
        Review nueva = new Review();
        nueva.setRating(5);
        nueva.setMessage("Excelente");
        nueva.setProduct(producto);
        nueva.setCreationDate(LocalDateTime.now());

        // Las guardamos en la base de datos
        reviewRepository.save(antigua);
        reviewRepository.save(nueva);

        // Ejecutamos el método que queremos probar
        List<Review> resultado = reviewRepository.findByProduct_IdOrderByCreationDateDesc(producto.getId());

        // Comprobamos que la primera del resultado sea la más NUEVA ("Excelente")
        assertEquals(2, resultado.size());
        assertEquals("Excelente", resultado.get(0).getMessage());
    }

    @Test
    void findByProductId() {
        // Creamos una reseña para nuestro producto
        Review r1 = new Review();
        r1.setRating(4);
        r1.setMessage("Me gusta");
        r1.setProduct(producto);
        reviewRepository.save(r1);

        // Ejecutamos el método que busca por ID de producto
        List<Review> resultado = reviewRepository.findByProductId(producto.getId());

        // Comprobamos que ha encontrado la reseña que guardamos
        assertEquals(1, resultado.size());
        assertEquals("Me gusta", resultado.get(0).getMessage());
    }
}