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
                .product(product1)
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
                .purchase(purchase1)
                .build();
        purchaseLineRepository.saveAll(List.of(purchaseLine1, purchaseLine2, purchaseLine3, purchaseLine4));
    }

    // -------- SIMPLE --------

    @Test
    void findByPurchase() {
        List<PurchaseLine> specificPurchase = purchaseLineRepository.findByPurchase(purchase1);
        System.out.println("-----------------------------------");
        System.out.println(specificPurchase);
        System.out.println("-----------------------------------");
        assertEquals(2, specificPurchase.size());
    }

    @Test
    void findByProduct() {
        List<PurchaseLine> specificProduct = purchaseLineRepository.findByProduct(product1);
        System.out.println("-----------------------------------");
        System.out.println(specificProduct);
        System.out.println("-----------------------------------");
        assertEquals(2, specificProduct.size());
    }

    // --------- RANGE --------

    @Test
    void findByQuantityGreaterThan() {
        List<PurchaseLine> quantityGreaterThan = purchaseLineRepository.findByQuantityGreaterThan(2);
        System.out.println("-----------------------------------");
        System.out.println(quantityGreaterThan);
        System.out.println("-----------------------------------");
        assertEquals(2, quantityGreaterThan.size());
    }

    @Test
    void findByQuantityLessThan() {
        List<PurchaseLine> quantityLessThan = purchaseLineRepository.findByQuantityLessThan(6);
        System.out.println("-----------------------------------");
        System.out.println(quantityLessThan);
        System.out.println("-----------------------------------");
        assertEquals(3, quantityLessThan.size());
    }

    @Test
    void findByQuantityBetween() {
        List<PurchaseLine> quantityBetween = purchaseLineRepository.findByQuantityBetween(2, 10);
        System.out.println("-----------------------------------");
        System.out.println(quantityBetween);
        System.out.println("-----------------------------------");
        assertEquals(3, quantityBetween.size());
    }

    // -------- ORDER --------

    @Test
    void findByPurchaseOrderByQuantityDesc() {
        List<PurchaseLine> purchaseOrderByQuantityDesc = purchaseLineRepository.findByPurchaseOrderByQuantityDesc(purchase1);
        System.out.println("-----------------------------------");
        System.out.println(purchaseOrderByQuantityDesc);
        System.out.println("-----------------------------------");
        assertEquals(2, purchaseOrderByQuantityDesc.size());
        for(int i = 0; i < purchaseOrderByQuantityDesc.size() - 1; i++){
            assertTrue(purchaseOrderByQuantityDesc.get(i).getQuantity() >= purchaseOrderByQuantityDesc.get(i + 1).getQuantity());
        }
    }

    @Test
    void findByPurchaseOrderByQuantityAsc(){
        List<PurchaseLine> purchaseOrderByQuantityAsc = purchaseLineRepository.findByPurchaseOrderByQuantityAsc(purchase1);
        System.out.println("-----------------------------------");
        System.out.println(purchaseOrderByQuantityAsc);
        System.out.println("-----------------------------------");
        assertEquals(2, purchaseOrderByQuantityAsc.size());
        for(int i = 0; i < purchaseOrderByQuantityAsc.size() - 1; i++){
            assertTrue(purchaseOrderByQuantityAsc.get(i).getQuantity() <= purchaseOrderByQuantityAsc.get(i + 1).getQuantity());
        }
    }

    // -------- COMPLEX --------

    @Test
    void findByPurchaseAndProduct() {
        List<PurchaseLine> purchaseAndProduct = purchaseLineRepository.findByPurchaseAndProduct(purchase2,product1);
        System.out.println("-----------------------------------");
        System.out.println(purchaseAndProduct);
        System.out.println("-----------------------------------");
        assertEquals(1, purchaseAndProduct.size());
    }

    @Test
    void findByProductAndQuantity(){
        List<PurchaseLine> productAndQuantity = purchaseLineRepository.findByProductAndQuantity(product3,4);
        System.out.println("-----------------------------------");
        System.out.println(productAndQuantity);
        System.out.println("-----------------------------------");
        assertEquals(1, productAndQuantity.size());
    }

    @Test
    void findByProductAndQuantityGreaterThan() {
        List<PurchaseLine> productAndQuantityGreaterThan = purchaseLineRepository.findByProductAndQuantityGreaterThan(product1, 1);
        System.out.println("-----------------------------------");
        System.out.println(productAndQuantityGreaterThan);
        System.out.println("-----------------------------------");
        assertEquals(1, productAndQuantityGreaterThan.size());
    }

    @Test
    void findByProductAndQuantityLessThan() {
        List<PurchaseLine> productAndQuantityLessThan = purchaseLineRepository.findByProductAndQuantityLessThan(product1, 2);
        System.out.println("-----------------------------------");
        System.out.println(productAndQuantityLessThan);
        System.out.println("-----------------------------------");
        assertEquals(1, productAndQuantityLessThan.size());
    }

    @Test
    void findByProductAndQuantityBetween(){
        List<PurchaseLine> productAndQuantityBetween = purchaseLineRepository.findByProductAndQuantityBetween(product1, 1,6);
        System.out.println("-----------------------------------");
        System.out.println(productAndQuantityBetween);
        System.out.println("-----------------------------------");
        assertEquals(2, productAndQuantityBetween.size());
    }
}