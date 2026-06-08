package com.ecommerce.service;

import com.ecommerce.model.*;
import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.repository.PurchaseLineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseLineServiceTest {

    @Mock
    PurchaseLineRepository purchaseLineRepository;

    @InjectMocks
    PurchaseLineService purchaseLineService;

    User user;
    Product product;
    Purchase purchase;
    PurchaseLine purchaseLine1;
    PurchaseLine purchaseLine2;
    UUID purchaseLineId1;
    UUID purchaseLineId2;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID purchaseId = UUID.randomUUID();
        purchaseLineId1 = UUID.randomUUID();
        purchaseLineId2 = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("customer")
                .email("customer@test.com")
                .password("password")
                .build();

        product = Product.builder()
                .id(productId)
                .title("Product")
                .price(10.0)
                .stock(5)
                .build();

        purchase = Purchase.builder()
                .id(purchaseId)
                .user(user)
                .purchaseStatus(PurchaseStatus.INITIATED)
                .build();

        purchaseLine1 = PurchaseLine.builder()
                .id(purchaseLineId1)
                .product(product)
                .purchase(purchase)
                .quantity(2)
                .build();

        purchaseLine2 = PurchaseLine.builder()
                .id(purchaseLineId2)
                .product(Product.builder().id(UUID.randomUUID()).title("Other Product").price(15.0).stock(10).build())
                .purchase(purchase)
                .quantity(1)
                .build();

        lenient().when(purchaseLineRepository.save(any(PurchaseLine.class))).thenAnswer(invocation -> {
            PurchaseLine pl = invocation.getArgument(0);
            if (pl.getId() == null) {
                pl.setId(UUID.randomUUID());
            }
            return pl;
        });
    }

    // Verifica que se devuelven todas las líneas de compra
    @Test
    void getAllPurchaseLinesReturnsAllLines() {
        List<PurchaseLine> expectedLines = Arrays.asList(purchaseLine1, purchaseLine2);
        when(purchaseLineRepository.findAll()).thenReturn(expectedLines);

        List<PurchaseLine> actualLines = purchaseLineService.getAllPurchaseLines();

        assertEquals(expectedLines, actualLines);
        verify(purchaseLineRepository).findAll();
    }

    // Verifica que se devuelve una línea de compra cuando se encuentra
    @Test
    void getPurchaseLineByIdReturnsLineWhenFound() {
        when(purchaseLineRepository.findById(purchaseLineId1)).thenReturn(Optional.of(purchaseLine1));

        Optional<PurchaseLine> result = purchaseLineService.getPurchaseLineById(purchaseLineId1);

        assertTrue(result.isPresent());
        assertEquals(purchaseLine1, result.get());
        verify(purchaseLineRepository).findById(purchaseLineId1);
    }

    // Verifica que devuelve un optional vacío cuando no se encuentra la línea de compra
    @Test
    void getPurchaseLineByIdReturnsEmptyOptionalWhenNotFound() {
        when(purchaseLineRepository.findById(purchaseLineId1)).thenReturn(Optional.empty());

        Optional<PurchaseLine> result = purchaseLineService.getPurchaseLineById(purchaseLineId1);

        assertFalse(result.isPresent());
        verify(purchaseLineRepository).findById(purchaseLineId1);
    }

    // Verifica que guarda y devuelve una nueva línea de compra
    @Test
    void createPurchaseLineSavesAndReturnsLine() {
        PurchaseLine newPurchaseLine = PurchaseLine.builder()
                .product(product)
                .purchase(purchase)
                .quantity(3)
                .build();

        PurchaseLine createdLine = purchaseLineService.createPurchaseLine(newPurchaseLine);

        assertNotNull(createdLine.getId());
        assertEquals(newPurchaseLine.getProduct(), createdLine.getProduct());
        assertEquals(newPurchaseLine.getQuantity(), createdLine.getQuantity());
        verify(purchaseLineRepository).save(newPurchaseLine);
    }

    // Verifica que actualiza y devuelve una línea de compra existente
    @Test
    void updatePurchaseLineUpdatesAndReturnsLine() {
        PurchaseLine updatedDetails = PurchaseLine.builder()
                .quantity(5)
                .build();
        when(purchaseLineRepository.findById(purchaseLineId1)).thenReturn(Optional.of(purchaseLine1));

        PurchaseLine result = purchaseLineService.updatePurchaseLine(purchaseLineId1, updatedDetails);

        assertEquals(updatedDetails.getQuantity(), result.getQuantity());
        verify(purchaseLineRepository).findById(purchaseLineId1);
        verify(purchaseLineRepository).save(purchaseLine1);
    }

    // Verifica que se lanza una excepción cuando la línea de compra no se encuentra
    @Test
    void updatePurchaseLineThrowsWhenNotFound() {
        PurchaseLine updatedDetails = PurchaseLine.builder().quantity(5).build();
        when(purchaseLineRepository.findById(purchaseLineId1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseLineService.updatePurchaseLine(purchaseLineId1, updatedDetails));

        assertEquals("PurchaseLine not found with id: " + purchaseLineId1, exception.getMessage());
        verify(purchaseLineRepository, never()).save(any());
    }

    // Verifica que se actualiza la cantidad de una línea de compra existente
    @Test
    void updateQuantityUpdatesAndReturnsLine() {
        int newQuantity = 10;
        when(purchaseLineRepository.findById(purchaseLineId1)).thenReturn(Optional.of(purchaseLine1));

        PurchaseLine result = purchaseLineService.updateQuantity(purchaseLineId1, newQuantity);

        assertEquals(newQuantity, result.getQuantity());
        verify(purchaseLineRepository).findById(purchaseLineId1);
        verify(purchaseLineRepository).save(purchaseLine1);
    }

    // Verifica que lanza una excepción cuando la línea de compra no se encuentra
    @Test
    void updateQuantityThrowsWhenNotFound() {
        int newQuantity = 10;
        when(purchaseLineRepository.findById(purchaseLineId1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseLineService.updateQuantity(purchaseLineId1, newQuantity));

        assertEquals("PurchaseLine not found with id: " + purchaseLineId1, exception.getMessage());
        verify(purchaseLineRepository, never()).save(any());
    }

    // Verifica que se elimina una línea de compra por su ID
    @Test
    void deletePurchaseLineDeletesLine() {
        purchaseLineService.deletePurchaseLine(purchaseLineId1);

        verify(purchaseLineRepository).deleteById(purchaseLineId1);
    }

    // Verifica que se devuelven las líneas de compra asociadas a una compra específica
    @Test
    void getPurchaseLinesByPurchaseReturnsLines() {
        List<PurchaseLine> expectedLines = Arrays.asList(purchaseLine1);
        when(purchaseLineRepository.findByPurchase(purchase)).thenReturn(expectedLines);

        List<PurchaseLine> actualLines = purchaseLineService.getPurchaseLinesByPurchase(purchase);

        assertEquals(expectedLines, actualLines);
        verify(purchaseLineRepository).findByPurchase(purchase);
    }
}