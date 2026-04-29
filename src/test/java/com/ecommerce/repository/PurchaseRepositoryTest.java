package com.ecommerce.repository;

import com.ecommerce.model.Purchase;
import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.model.enums.ShippingMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PurchaseRepositoryTest {

    @Autowired
    PurchaseRepository purchaseRepository;

    Purchase purchase1;
    Purchase purchase2;
    Purchase purchase3;
    Purchase purchase4;

    @BeforeEach
    void setUp(){

        purchase1 = Purchase.builder()
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 15, 12, 45))
                .finishedDate(LocalDateTime.of(2026, Month.APRIL, 28, 17, 30))
                .purchaseStatus(PurchaseStatus.TERMINADO)
                .shippingMode(ShippingMode.STANDARD)
                .totalPrice(50.00)
                .userComment("Me han llegado el producto en mal estado")
                .build();
        purchase2 = Purchase.builder()
                .creationDate(LocalDateTime.of(2025, Month.JUNE, 10, 18, 35))
                .finishedDate(LocalDateTime.of(2025, Month.DECEMBER, 25, 16, 15))
                .purchaseStatus(PurchaseStatus.TERMINADO)
                .shippingMode(ShippingMode.EXPRESS)
                .totalPrice(15.45)
                .userComment("El producto ha llegado bien pero he tardado mucho más de lo esperado teniendo en cuenta que era EXPRESS")
                .build();
        purchase3 = Purchase.builder()
                .creationDate(LocalDateTime.of(2026, Month.FEBRUARY, 10, 11, 50))
                .finishedDate(null)
                .purchaseStatus(PurchaseStatus.INICIADO)
                .shippingMode(ShippingMode.PREMIUM)
                .totalPrice(150.75)
                .userComment(null)
                .build();
        purchase4 = Purchase.builder()
                .creationDate(LocalDateTime.of(2020, Month.MAY, 30, 8, 30))
                .finishedDate(null)
                .purchaseStatus(PurchaseStatus.INACTIVO)
                .shippingMode(ShippingMode.STANDARD)
                .totalPrice(73.00)
                .userComment(null)
                .build();
        purchaseRepository.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));
    }

    // -------- SIMPLES --------

    @Test
    void findByPurchaseStatus() {
        List<Purchase> finishedPurchases = purchaseRepository.findByPurchaseStatus(PurchaseStatus.TERMINADO);
        System.out.println("-----------------------------------");
        System.out.println(finishedPurchases);
        System.out.println("-----------------------------------");
        assertEquals(2, finishedPurchases.size());
    }

    @Test
    void findByShippingMode() {
        List<Purchase> standardPurchases = purchaseRepository.findByShippingMode(ShippingMode.STANDARD);
        System.out.println("-----------------------------------");
        System.out.println(standardPurchases);
        System.out.println("-----------------------------------");
        assertEquals(2, standardPurchases.size());
    }

    // --------- RANGES --------

    @Test
    void findByCreationDateBetween() {
        List<Purchase> containsCreationDatePurchases = purchaseRepository.findByCreationDateBetween(
                LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0),
                LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59)
        );
        System.out.println("-----------------------------------");
        System.out.println(containsCreationDatePurchases);
        System.out.println("-----------------------------------");
        assertEquals(2, containsCreationDatePurchases.size());
    }

    @Test
    void findByFinishedDateBetween() {
        List<Purchase> containsFinishedDatePurchases = purchaseRepository.findByFinishedDateBetween
                (LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0),
                LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59));
        System.out.println("-----------------------------------");
        System.out.println(containsFinishedDatePurchases);
        System.out.println("-----------------------------------");
        assertEquals(1, containsFinishedDatePurchases.size());
    }

    @Test
    void findByTotalPriceBetween() {
        List<Purchase> containsTotalPricePurchases = purchaseRepository.findByTotalPriceBetween(50.00, 100.00);
        System.out.println("-----------------------------------");
        System.out.println(containsTotalPricePurchases);
        System.out.println("-----------------------------------");
        assertEquals(2, containsTotalPricePurchases.size());
    }

    @Test
    void findByTotalPriceGreaterThan() {
        List<Purchase> greaterThanTotalPricePurchases = purchaseRepository.findByTotalPriceGreaterThan(50.00);
        System.out.println("-----------------------------------");
        System.out.println(greaterThanTotalPricePurchases);
        System.out.println("-----------------------------------");
        assertEquals(2, greaterThanTotalPricePurchases.size());
    }

    @Test
    void finByTotalPriceLessThan() {
        List<Purchase> lessThanTotalPricePurchases = purchaseRepository.findByTotalPriceLessThan(50.00);
        System.out.println("-----------------------------------");
        System.out.println(lessThanTotalPricePurchases);
        System.out.println("-----------------------------------");
        assertEquals(1, lessThanTotalPricePurchases.size());
    }

    // -------- SPECIFICS --------

    @Test
    void findByUserCommentContaining() {
        List<Purchase> containsCommentPurchase = purchaseRepository.findByUserCommentContaining("producto");
        System.out.println("-----------------------------------");
        System.out.println(containsCommentPurchase);
        System.out.println("-----------------------------------");
        assertEquals(2, containsCommentPurchase.size());
    }

    // -------- ORDER --------

    @Test
    void findAllByOrderByCreationDateDesc() {
        List<Purchase> creationDateDesc = purchaseRepository.findAllByOrderByCreationDateDesc();
        System.out.println("-----------------------------------");
        System.out.println(creationDateDesc);
        System.out.println("-----------------------------------");
        assertEquals(4, creationDateDesc.size());
        for (int i = 0; i < creationDateDesc.size() - 1; i++) {
            assertTrue(creationDateDesc.get(i).getCreationDate().isAfter(creationDateDesc.get(i + 1).getCreationDate())
                    || creationDateDesc.get(i).getCreationDate().isEqual(creationDateDesc.get(i + 1).getCreationDate()));
        }
    }

    @Test
    void findAllByOrderByCreationDateAsc(){
        List<Purchase> creationDateAsc = purchaseRepository.findAllByOrderByCreationDateAsc();
        System.out.println("-----------------------------------");
        System.out.println(creationDateAsc);
        System.out.println("-----------------------------------");
        assertEquals(4, creationDateAsc.size());
        for (int i = 0; i < creationDateAsc.size() - 1; i++) {
            assertTrue(creationDateAsc.get(i).getCreationDate().isBefore(creationDateAsc.get(i + 1).getCreationDate())
                    || creationDateAsc.get(i).getCreationDate().isEqual(creationDateAsc.get(i + 1).getCreationDate()));
        }
    }

    // -------- COMPLEX --------

    @Test
    void findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice() {
        List<Purchase> finishedDateAndPurchaseStatusOrdered = purchaseRepository.findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice(
                (LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0)),
                LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59),
                PurchaseStatus.TERMINADO);
        System.out.println("-----------------------------------");
        System.out.println(finishedDateAndPurchaseStatusOrdered);
        System.out.println("-----------------------------------");
        assertEquals(1, finishedDateAndPurchaseStatusOrdered.size());
        for (int i = 0; i < finishedDateAndPurchaseStatusOrdered.size() - 1; i++) {
            assertTrue(finishedDateAndPurchaseStatusOrdered.get(i).getTotalPrice() <= finishedDateAndPurchaseStatusOrdered.get(i + 1).getTotalPrice());
        }
    }
}