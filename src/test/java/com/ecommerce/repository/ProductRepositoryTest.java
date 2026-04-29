package com.ecommerce.repository;

import com.ecommerce.model.Brand;
import com.ecommerce.model.Product;
import com.ecommerce.model.enums.ProductStockStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductRepository productRepository;
    @BeforeEach
    void setUp() {
        Brand brandNike = Brand.builder()

                .name("Nike")
                .nif("123456789")
                .build();
        brandNike = brandRepository.save(brandNike);

        var produc1 = Product.builder()
                .title("Camiseta Deporte")

                .price(32.0)
                .available(true)
                .stockStatus(ProductStockStatus.STOCK)
                .brand(brandNike)
                .build();
        var produc2 = Product.builder()
                .title("Pantalon deporte")

                .price(43.0)
                .available(true)
                .stockStatus(ProductStockStatus.STOCK)
                .brand(brandNike)
                .build();
        var produc3 = Product.builder()
                .title("Zapatillas deporte")
                .price(80.0)
                .available(true)
                .stockStatus(ProductStockStatus.STOCK)
                .brand(brandNike)
                .build();
        var produc4 = Product.builder()
                .title("Calcetines deporte")

                .price(8.0)
                .available(true)
                .stockStatus(ProductStockStatus.STOCK)
                .brand(brandNike)
                .build();
        productRepository.saveAll(List.of(produc1, produc2, produc3, produc4));
    }

    @Test
    void findByTitle() {
        List<Product> products = productRepository.findByTitle("Camiseta Deporte");
        assertNotNull(products);
        assertEquals(1, products.size());
    }

    @Test
    void findByTitleContainsIgnoreCase() {
            List<Product> products = productRepository.findByTitleContainsIgnoreCase("deporte");
            assertNotNull(products);
            assertEquals(4, products.size());
    }

    @Test
    void findByPriceBetween() {
        List<Product> products = productRepository.findByPriceBetween(30.0, 50.0);
        assertNotNull(products);
        assertEquals(2, products.size());
    }

    @Test
    void findByBrandId() {
        List<Product> products = productRepository.findByBrandId(brandRepository.findAll().getFirst().getId());
        assertNotNull(products);
        assertEquals(4, products.size());
    }

    @Test
    void findByAvailableTrueOrderByPriceAsc() {
        List<Product> products = productRepository.findByAvailableTrueOrderByPriceAsc();
        assertNotNull(products);
        assertEquals(4, products.size());
        assertTrue(products.get(0).getPrice() <= products.get(1).getPrice());
        assertTrue(products.get(1).getPrice() <= products.get(2).getPrice());
        assertTrue(products.get(2).getPrice() <= products.get(3).getPrice());
    }

    @Test
    void findByAvailableTrueOrderByPriceDesc() {
        List<Product> products = productRepository.findByAvailableTrueOrderByPriceDesc();
        assertNotNull(products);
        assertEquals(4, products.size());
        assertTrue(products.get(0).getPrice() >= products.get(1).getPrice());
        assertTrue(products.get(1).getPrice() >= products.get(2).getPrice());
        assertTrue(products.get(2).getPrice() >= products.get(3).getPrice());
    }
}