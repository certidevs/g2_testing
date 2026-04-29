package com.ecommerce.repository;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.enums.ShippingMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PurchaseLineRepositoryTest {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PurchaseLineRepository purchaseLineRepository;

    Purchase purchase1;
    Purchase purchase2;
    Purchase purchase3;
    Purchase purchase4;
    PurchaseLine purchaseLine1;
    PurchaseLine purchaseLine2;
    PurchaseLine purchaseLine3;
    PurchaseLine purchaseLine4;
    Product product1;
    Product product2;
    Product product3;
    Product product4;

    @BeforeEach
    void setUp(){

        product1 = Product.builder().title("pr1").price(20.50).build();
        product2 = Product.builder().title("pr2").price(30.00).build();
        product3 = Product.builder().title("pr3").price(10.00).build();
        product4 = Product.builder().title("pr4").price(5.00).build();
        productRepository.saveAll(List.of(product1, product2, product3, product4));

        purchase1 = Purchase.builder().totalPrice(20.50).build();
        purchase2 = Purchase.builder().totalPrice(60.00).build();
        purchase3 = Purchase.builder().totalPrice(40.00).build();
        purchase4 = Purchase.builder().totalPrice(50.00).build();
        purchaseRepository.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));

        purchaseLine1 = PurchaseLine.builder()
                .quantity(1)
                .product(product1)
                .purchase(purchase1)
                .build();
        purchaseLine2 = PurchaseLine.builder()
                .quantity(2)
                .product(product2)
                .purchase(purchase2)
                .build();
        purchaseLine3 = PurchaseLine.builder()
                .quantity(4)
                .product(product3)
                .purchase(purchase3)
                .build();
        purchaseLine4 = PurchaseLine.builder()
                .quantity(10)
                .product(product4)
                .purchase(purchase4)
                .build();
        purchaseLineRepository.saveAll(List.of(purchaseLine1, purchaseLine2, purchaseLine3, purchaseLine4));
    }

    @Test
    void findByPurchase() {
        List<PurchaseLine> specificPurchase = purchaseLineRepository.findByPurchase(purchase1);
        System.out.println("-----------------------------------");
        System.out.println(specificPurchase);
        System.out.println("-----------------------------------");
        assertEquals(1, specificPurchase.size());
    }

    @Test
    void findByProduct() {
    }

    @Test
    void findByQuantityGreaterThan() {
    }

    @Test
    void findByQuantityLessThan() {
    }

    @Test
    void findByQuantityBetween() {
    }

    @Test
    void findByPurchaseOrderByQuantityDesc() {
    }

    @Test
    void findByPurchaseAndProduct() {
    }
}