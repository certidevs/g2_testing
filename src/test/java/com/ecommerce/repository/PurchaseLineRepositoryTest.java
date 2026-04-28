package com.ecommerce.repository;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

@DataJpaTest
class PurchaseLineRepositoryTest {

    // @Autowired de los repositorios a utilizar
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
        product1 = Product.builder().price(20.50).build();
        product2 = Product.builder().price(30.50).build();
        product3 = Product.builder().price(15.50).build();
        product4 = Product.builder().price(10.50).build();
        productRepository.saveAll(List.of(product1, product2, product3, product4));

        purchaseLine1 = PurchaseLine.builder()
                .quatinty(1)
                .product(product1)
                .purchase(purchase1)
                .build();
        purchaseLine2 = PurchaseLine.builder()
                .quatinty(4)
                .product(product2)
                .purchase(purchase1)
                .build();
        purchaseLine3 = PurchaseLine.builder()
                .quatinty(10)
                .product(product2)
                .purchase(purchase1)
                .build();
        purchaseLine4 = PurchaseLine.builder()
                .quatinty(8)
                .product(product2)
                .purchase(purchase1)
                .build();
        purchaseLineRepository.saveAll(List.of(purchaseLine1, purchaseLine2, purchaseLine3, purchaseLine4));

        purchase1 = Purchase.builder().totalPrice(purchaseLine1.getTotal(purchaseLine1.getQuatinty(), purchaseLine1.getProduct().getPrice())).build();
        purchase2 = Purchase.builder().totalPrice(purchaseLine2.getTotal(purchaseLine2.getQuatinty(), purchaseLine2.getProduct().getPrice())).build();
        purchase3 = Purchase.builder().totalPrice(purchaseLine3.getTotal(purchaseLine3.getQuatinty(), purchaseLine3.getProduct().getPrice())).build();
        purchase4 = Purchase.builder().totalPrice(purchaseLine4.getTotal(purchaseLine4.getQuatinty(), purchaseLine4.getProduct().getPrice())).build();
        purchaseRepository.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));
    }

    @Test
    void findByPurchase() {
    }

    @Test
    void findByProduct() {
    }

    @Test
    void findByQuatintyGreaterThan() {
    }

    @Test
    void findByQuatintyLessThan() {
    }

    @Test
    void findByQuatintyBetween() {
    }

    @Test
    void findByPurchaseOrderByQuatintyDesc() {
    }

    @Test
    void findByPurchaseAndProduct() {
    }
}