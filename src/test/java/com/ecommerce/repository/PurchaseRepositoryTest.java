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

@DataJpaTest
class PurchaseRepositoryTest {

    // @Autowired de los repositorios a utilizar
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
                .totalPrice(purchase1.getTotalPrice())
                .userComment("Me han llegado los productos en mal estado")
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

    @Test
    void findByPurchaseStatus() {
    }

    @Test
    void findByShippingMode() {
    }

    @Test
    void findByProduct() {
    }

    @Test
    void findByCreationDateBetween() {
    }

    @Test
    void findByFinishedDateBetween() {
    }

    @Test
    void findByUnitPriceBetween() {
    }

    @Test
    void findByTotalPriceBetween() {
    }

    @Test
    void findByTotalPriceGreaterThan() {
    }

    @Test
    void finByTotalPriceLessThan() {
    }

    @Test
    void findByUserCommentContaining() {
    }

    @Test
    void findAllByOrderByCreationDateDesc() {
    }

    @Test
    void findByFinishedDateBetweenAndPurchaseStatusOrderByTotalPrice() {
    }
}