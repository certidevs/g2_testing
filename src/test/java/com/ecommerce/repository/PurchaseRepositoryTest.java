package com.ecommerce.repository;

import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PurchaseRepositoryTest {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PurchaseLineRepository purchaseLineRepository;

    User user1;
    User user2;

    Product product1;
    Product product2;

    Purchase purchase1;
    Purchase purchase2;
    Purchase purchase3;
    Purchase purchase4;

    PurchaseLine purchaseLine1;
    PurchaseLine purchaseLine2;

    @BeforeEach
    void setUp(){

        user1 = User.builder()
                .username("user1.purchase")
                .name("User 1")
                .lastName("Last Name 1")
                .email("user1@gmail.com")
                .phone("123456789")
                .password("password1")
                .birthday(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .gender(Gender.MALE)
                .role(Role.ROLE_CUSTOMER)
                .build();

        user2 = User.builder()
                .username("user2.purchase")
                .name("User 2")
                .lastName("Last Name 2")
                .email("user2@gmail.com")
                .phone("987654321")
                .password("password2")
                .birthday(LocalDateTime.of(1995, Month.JUNE, 15, 0, 0))
                .gender(Gender.FEMALE)
                .role(Role.ROLE_CUSTOMER)
                .build();

        userRepository.saveAll(List.of(user1, user2));

        product1 = Product.builder()
                .title("Product 1")
                .price(25.00)
                .build();

        product2 = Product.builder()
                .title("Product 2")
                .price(15.00)
                .build();

        productRepository.saveAll(List.of(product1, product2));

        purchase1 = Purchase.builder()
                .user(user1)
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 15, 12, 45))
                .finishedDate(LocalDateTime.of(2026, Month.APRIL, 28, 17, 30))
                .purchaseStatus(PurchaseStatus.FINISHED)
                .paymentStatus(PaymentStatus.PAID)
                .processStatus(ProcessStatus.COMPLETED)
                .shippingStatus(ShippingStatus.DELIVERED)
                .shippingMode(ShippingMode.STANDARD)
                .totalPrice(50.00)
                .userComment("Me ha llegado el producto en mal estado")
                .build();

        purchase2 = Purchase.builder()
                .user(user2)
                .creationDate(LocalDateTime.of(2025, Month.JUNE, 10, 18, 35))
                .finishedDate(LocalDateTime.of(2025, Month.DECEMBER, 25, 16, 15))
                .purchaseStatus(PurchaseStatus.FINISHED)
                .paymentStatus(PaymentStatus.PAID)
                .processStatus(ProcessStatus.COMPLETED)
                .shippingStatus(ShippingStatus.DELIVERED)
                .shippingMode(ShippingMode.EXPRESS)
                .totalPrice(15.45)
                .userComment("El producto ha llegado bien pero he tardado mucho más de lo esperado teniendo en cuenta que era EXPRESS")
                .build();

        purchase3 = Purchase.builder()
                .user(user1)
                .creationDate(LocalDateTime.of(2026, Month.FEBRUARY, 10, 11, 50))
                .finishedDate(null)
                .purchaseStatus(PurchaseStatus.INITIATED)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.PENDING)
                .shippingStatus(ShippingStatus.PENDING)
                .shippingMode(ShippingMode.PREMIUM)
                .totalPrice(150.75)
                .userComment(null)
                .build();

        purchase4 = Purchase.builder()
                .user(user2)
                .creationDate(LocalDateTime.of(2020, Month.MAY, 30, 8, 30))
                .finishedDate(null)
                .purchaseStatus(PurchaseStatus.INACTIVE)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.PENDING)
                .shippingStatus(ShippingStatus.PENDING)
                .shippingMode(ShippingMode.STANDARD)
                .totalPrice(73.00)
                .userComment(null)
                .build();

        purchaseRepository.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));

        purchaseLine1 = PurchaseLine.builder()
                .purchase(purchase1)
                .product(product1)
                .quantity(2)
                .build();

        purchaseLine2 = PurchaseLine.builder()
                .purchase(purchase2)
                .product(product2)
                .quantity(1)
                .build();

        purchaseLineRepository.saveAll(List.of(purchaseLine1, purchaseLine2));
    }

    // -------- SIMPLE --------

    @Test
    void findByUsersId(){
        List<Purchase> specificUserPurchases = purchaseRepository.findByUserId(user1.getId());
        System.out.println("-----------------------------------");
        System.out.println(specificUserPurchases);
        System.out.println("-----------------------------------");
        assertEquals(2, specificUserPurchases.size());
    }

    @Test
    void findById(){

        Optional<Purchase> specificPurchase = purchaseRepository.findById(purchase1.getId());
        System.out.println("-----------------------------------");
        System.out.println(specificPurchase);
        System.out.println("-----------------------------------");
        assertTrue(specificPurchase.isPresent());
        assertEquals(purchase1.getId(), specificPurchase.get().getId());
    }

    @Test
    void findByPurchaseStatus() {
        List<Purchase> finishedPurchases = purchaseRepository.findByPurchaseStatus(PurchaseStatus.FINISHED);
        System.out.println("-----------------------------------");
        System.out.println(finishedPurchases);
        System.out.println("-----------------------------------");
        assertEquals(2, finishedPurchases.size());
    }

    @Test
    void findByPaymentStatus(){
        List<Purchase> containsPaymentStatus = purchaseRepository.findByPaymentStatus(PaymentStatus.PAID);
        System.out.println("-----------------------------------");
        System.out.println(containsPaymentStatus);
        System.out.println("-----------------------------------");
        assertEquals(2, containsPaymentStatus.size());
    }

    @Test
    void findByProcessStatus(){
        List<Purchase> containsProcessStatus = purchaseRepository.findByProcessStatus(ProcessStatus.PENDING);
        System.out.println("-----------------------------------");
        System.out.println(containsProcessStatus);
        System.out.println("-----------------------------------");
        assertEquals(2, containsProcessStatus.size());
    }

    @Test
    void findByShippingStatus(){
        List<Purchase> containsShippingStatus = purchaseRepository.findByShippingStatus(ShippingStatus.DELIVERED);
        System.out.println("-----------------------------------");
        System.out.println(containsShippingStatus);
        System.out.println("-----------------------------------");
        assertEquals(2, containsShippingStatus.size());
    }

    @Test
    void findByShippingMode() {
        List<Purchase> standardPurchases = purchaseRepository.findByShippingMode(ShippingMode.STANDARD);
        System.out.println("-----------------------------------");
        System.out.println(standardPurchases);
        System.out.println("-----------------------------------");
        assertEquals(2, standardPurchases.size());
    }

    // --------- RANGE --------

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

    // -------- SPECIFIC --------

    @Test
    void findByIdAndUserCommentContaining(){
        List<Purchase> containsIdAndCommentPurchases = purchaseRepository.findByIdAndUserCommentContaining(purchase1.getId(), "mal estado");
        System.out.println("-----------------------------------");
        System.out.println(containsIdAndCommentPurchases);
        System.out.println("-----------------------------------");
        assertEquals(1, containsIdAndCommentPurchases.size());
        assertEquals(purchase1.getId(), containsIdAndCommentPurchases.get(0).getId());
    }

    @Test
    void findByUserCommentContaining() {
        List<Purchase> containsCommentPurchase = purchaseRepository.findByUserCommentContaining("producto");
        System.out.println("-----------------------------------");
        System.out.println(containsCommentPurchase);
        System.out.println("-----------------------------------");
        assertEquals(2, containsCommentPurchase.size());
    }

    @Test
    void findByUserIdAndUserCommentContaining(){
        List<Purchase> containsUserIdAndCommentPurchases = purchaseRepository.findByUserIdAndUserCommentContaining(purchase1.getUser().getId(), "mal estado");
        System.out.println("-----------------------------------");
        System.out.println(containsUserIdAndCommentPurchases);
        System.out.println("-----------------------------------");
        assertEquals(1, containsUserIdAndCommentPurchases.size());
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
                PurchaseStatus.FINISHED);
        System.out.println("-----------------------------------");
        System.out.println(finishedDateAndPurchaseStatusOrdered);
        System.out.println("-----------------------------------");
        assertEquals(1, finishedDateAndPurchaseStatusOrdered.size());
        for (int i = 0; i < finishedDateAndPurchaseStatusOrdered.size() - 1; i++) {
            assertTrue(finishedDateAndPurchaseStatusOrdered.get(i).getTotalPrice() <= finishedDateAndPurchaseStatusOrdered.get(i + 1).getTotalPrice());
        }
    }

    @Test
    void findByIdAndShippingMode() {
        List<Purchase> idAndShippingMode = purchaseRepository.findByIdAndShippingMode(purchase3.getId(), ShippingMode.PREMIUM);
        System.out.println("-----------------------------------");
        System.out.println(idAndShippingMode);
        System.out.println("-----------------------------------");
        assertEquals(1, idAndShippingMode.size());
    }

    // -------- FUNCTIONS --------

    @Test
    void existsByUsersIdAndProductIdReturnsTrueWhenUserBoughtProduct() {
        boolean exists = purchaseRepository.existsByUsersIdAndProductId(user1.getId(), product1.getId());

        assertTrue(exists);
    }

    @Test
    void existsByUsersIdAndProductIdReturnsFalseWhenUserDidNotBuyProduct() {
        boolean exists = purchaseRepository.existsByUsersIdAndProductId(user1.getId(), product2.getId());

        assertFalse(exists);
    }

    @Test
    void findFirstByPurchaseStatus() {
        Optional<Purchase> initiatedPurchase = purchaseRepository.findFirstByPurchaseStatus(PurchaseStatus.INITIATED);

        assertTrue(initiatedPurchase.isPresent());
        assertEquals(purchase3.getId(), initiatedPurchase.get().getId());
    }

    @Test
    void findFirstByUserIdAndPurchaseStatusReturnsPurchaseForMatchingUser() {
        Optional<Purchase> initiatedPurchase = purchaseRepository.findFirstByUserIdAndPurchaseStatus(
                user1.getId(),
                PurchaseStatus.INITIATED
        );

        assertTrue(initiatedPurchase.isPresent());
        assertEquals(purchase3.getId(), initiatedPurchase.get().getId());
    }

    @Test
    void findFirstByUserIdAndPurchaseStatusReturnsEmptyWhenNoMatch() {
        Optional<Purchase> initiatedPurchase = purchaseRepository.findFirstByUserIdAndPurchaseStatus(
                user2.getId(),
                PurchaseStatus.INITIATED
        );

        assertTrue(initiatedPurchase.isEmpty());
    }
}
