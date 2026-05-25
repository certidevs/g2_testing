package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.PaymentStatus;
import com.ecommerce.model.enums.ProcessStatus;
import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.model.enums.ShippingMode;
import com.ecommerce.model.enums.ShippingStatus;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    PurchaseRepository purchaseRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    PurchaseLineRepository purchaseLineRepository;

    @InjectMocks
    PurchaseService purchaseService;

    User user;
    Product product;
    UUID userId;
    UUID productId;
    UUID purchaseId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        purchaseId = UUID.randomUUID();

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

        // Lenient sirve para evitar que Mockito lance excepciones por stubs no utilizados, lo cual es útil cuando el mismo stub se usa en múltiples pruebas pero no todas las pruebas lo utilizan :v
        lenient().when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> {
            Purchase purchase = invocation.getArgument(0);
            if (purchase.getId() == null) {
                purchase.setId(purchaseId);
            }
            return purchase;
        });

        // Cuando se guarde una línea de compra, simplemente devuelve la misma línea sin modificarla (sin asignarle un ID ni nada)
        lenient().when(purchaseLineRepository.save(any(PurchaseLine.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // Prueba para verificar que al crear una compra se asigna el usuario, se establece la fecha de creación, se calcula el total a partir de las líneas y se asigna la compra a cada línea
    @Test
    void createPurchaseAssignsUserTotalsDateAndLinePurchase() {
        Purchase purchase = new Purchase();
        PurchaseLine line1 = PurchaseLine.builder().product(product).quantity(2).build();
        PurchaseLine line2 = PurchaseLine.builder()
                .product(Product.builder().id(UUID.randomUUID()).title("Other").price(5.0).stock(3).build())
                .quantity(3)
                .build();
        purchase.getLines().addAll(List.of(line1, line2));

        purchaseService.createPurchase(purchase, user);

        assertEquals(user, purchase.getUser());
        assertNotNull(purchase.getCreationDate());
        assertEquals(35.0, purchase.getTotalPrice());
        assertEquals(purchase, line1.getPurchase());
        assertEquals(purchase, line2.getPurchase());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que si se crea una compra con líneas nulas, se asignará el usuario luego establece la fecha de creación, después asigna un total de 0 y no lanza ninguna excepción
    @Test
    void createPurchaseWithNullLinesAssignsUserAndZeroTotal() {
        Purchase purchase = new Purchase();
        purchase.setLines(null);

        purchaseService.createPurchase(purchase, user);

        assertEquals(user, purchase.getUser());
        assertNotNull(purchase.getCreationDate());
        assertEquals(0.0, purchase.getTotalPrice());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que al agregar un producto al carrito, si no hay un carrito activo para el usuario, se crea uno nuevo con el producto agregado como línea, se asigna el usuario a la compra, se calcula el total y se guarda la línea y la compra en el repositorio
    @Test
    void addProductToCartCreatesNewCartWithUserAndLine() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.empty());
        when(purchaseLineRepository.findByPurchase_IdAndProduct_Id(purchaseId, productId))
                .thenReturn(Optional.empty());

        Purchase purchase = purchaseService.addProductToCart(productId, user);

        assertEquals(user, purchase.getUser());
        assertEquals(PurchaseStatus.INITIATED, purchase.getPurchaseStatus());
        assertEquals(10.0, purchase.getTotalPrice());
        assertEquals(1, purchase.getLines().size());
        assertEquals(product, purchase.getLines().get(0).getProduct());
        assertEquals(1, purchase.getLines().get(0).getQuantity());
        verify(purchaseLineRepository).save(purchase.getLines().get(0));
        verify(purchaseRepository, times(2)).save(purchase);
    }

    // Verifica que al agregar un producto al carrito, si ya existe una línea para ese producto en el carrito activo, se incrementa la cantidad de esa línea, se recalcula el total de la compra y se guarda la línea actualizada en el repositorio
    @Test
    void addProductToCartIncrementsExistingLineAndSynchronizesPurchaseLines() {
        Purchase purchase = purchaseWithLine(2);
        Product otherProduct = Product.builder().id(UUID.randomUUID()).title("Other").price(4.0).stock(10).build();
        purchase.getLines().add(PurchaseLine.builder()
                .purchase(purchase)
                .product(otherProduct)
                .quantity(1)
                .build());
        PurchaseLine storedLine = PurchaseLine.builder()
                .purchase(purchase)
                .product(product)
                .quantity(2)
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.of(purchase));
        when(purchaseLineRepository.findByPurchase_IdAndProduct_Id(purchaseId, productId))
                .thenReturn(Optional.of(storedLine));

        Purchase result = purchaseService.addProductToCart(productId, user);

        assertEquals(3, storedLine.getQuantity());
        assertEquals(3, result.getLines().get(0).getQuantity());
        assertEquals(34.0, result.getTotalPrice());
        verify(purchaseLineRepository).save(storedLine);
    }

    // Verifica que al agregar un producto al carrito, si ya existe una línea para ese producto en el carrito activo pero las líneas de la compra son nulas, se incrementa la cantidad de esa línea, se recalcula el total de la compra, se asigna la línea a la compra y se guarda la línea actualizada en el repositorio
    @Test
    void addProductToCartIncrementsExistingLineWhenPurchaseLinesAreNull() {
        Purchase purchase = purchaseWithLine(2);
        purchase.setLines(null);
        PurchaseLine storedLine = PurchaseLine.builder()
                .purchase(purchase)
                .product(product)
                .quantity(2)
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.of(purchase));
        when(purchaseLineRepository.findByPurchase_IdAndProduct_Id(purchaseId, productId))
                .thenReturn(Optional.of(storedLine));

        Purchase result = purchaseService.addProductToCart(productId, user);

        assertEquals(3, storedLine.getQuantity());
        assertEquals(1, result.getLines().size());
        assertEquals(storedLine, result.getLines().get(0));
        assertEquals(30.0, result.getTotalPrice());
        verify(purchaseLineRepository).save(storedLine);
    }

    // Verifica que al intentar agregar un producto al carrito si el usuario es nulo se lance una excepción y no se realice ninguna operación de búsqueda o guardado en los repositorios
    @Test
    void addProductToCartThrowsWhenUserIsNull() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.addProductToCart(productId, null));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verifyNoInteractions(productRepository, purchaseLineRepository);
    }

    // Verifica que al agregar un producto al carrito, si no existe el producto se lance una excepción y no se realice ninguna operación de búsqueda o guardado en el repositorio de líneas de compra
    @Test
    void addProductToCartThrowsWhenProductDoesNotExist() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.addProductToCart(productId, user));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(purchaseRepository, never()).findFirstByUserIdAndPurchaseStatus(any(), any());
    }

    // Verifica que al agregar un producto al carrito, si el producto no tiene stock disponible, se lance una excepción y no se realice ninguna operación de búsqueda o guardado en el repositorio de líneas de compra
    @Test
    void addProductToCartThrowsWhenProductHasNoStock() {
        product.setStock(0);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.addProductToCart(productId, user));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(purchaseRepository, never()).findFirstByUserIdAndPurchaseStatus(any(), any());
    }

    // Verifica que al agregar un producto al carrito, si ya existe una línea para ese producto en el carrito activo pero la cantidad de esa línea es igual al stock del producto, se lance una excepción y no se guarde ninguna línea actualizada en el repositorio
    @Test
    void addProductToCartThrowsWhenStockLimitIsReached() {
        Purchase purchase = purchaseWithLine(5);
        PurchaseLine storedLine = purchase.getLines().get(0);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.of(purchase));
        when(purchaseLineRepository.findByPurchase_IdAndProduct_Id(purchaseId, productId))
                .thenReturn(Optional.of(storedLine));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.addProductToCart(productId, user));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(purchaseLineRepository, never()).save(any());
    }

    // Verifica que al eliminar un producto del carrito, si ya existe una línea para ese producto en el carrito activo, se disminuye la cantidad de esa línea, se recalcula el total de la compra y se guarda la línea actualizada en el repositorio sin eliminarla
    @Test
    void removeProductFromCartDecrementsQuantityAndSynchronizesPurchaseLines() {
        Purchase purchase = purchaseWithLine(3);
        Product otherProduct = Product.builder().id(UUID.randomUUID()).title("Other").price(4.0).stock(10).build();
        purchase.getLines().add(PurchaseLine.builder()
                .purchase(purchase)
                .product(otherProduct)
                .quantity(1)
                .build());
        PurchaseLine storedLine = purchase.getLines().get(0);
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.of(purchase));
        when(purchaseLineRepository.findByPurchase_IdAndProduct_Id(purchaseId, productId))
                .thenReturn(Optional.of(storedLine));

        Purchase result = purchaseService.removeProductFromCart(productId, user);

        assertEquals(2, storedLine.getQuantity());
        assertEquals(2, result.getLines().get(0).getQuantity());
        assertEquals(24.0, result.getTotalPrice());
        verify(purchaseLineRepository).save(storedLine);
        verify(purchaseLineRepository, never()).delete(any());
    }

    // Verifica que al eliminar un producto del carrito si ya existe una línea para ese producto en el carrito activo pero las líneas de la compra son nulas, se disminuye la cantidad de esa línea y se recalcula el total de la compra
    @Test
    void removeProductFromCartDecrementsExistingLineWhenPurchaseLinesAreNull() {
        Purchase purchase = purchaseWithLine(3);
        PurchaseLine storedLine = purchase.getLines().get(0);
        purchase.setLines(null);
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.of(purchase));
        when(purchaseLineRepository.findByPurchase_IdAndProduct_Id(purchaseId, productId))
                .thenReturn(Optional.of(storedLine));

        Purchase result = purchaseService.removeProductFromCart(productId, user);

        assertEquals(2, storedLine.getQuantity());
        assertEquals(1, result.getLines().size());
        assertEquals(storedLine, result.getLines().get(0));
        assertEquals(20.0, result.getTotalPrice());
        verify(purchaseLineRepository).save(storedLine);
        verify(purchaseLineRepository, never()).delete(any());
    }

    // Verifica que al eliminar un producto del carrito si la cantidad de la línea es 1, se elimina la línea de compra y recalcule el total de la compra y se guarde la compra actualizada en el repositorio
    @Test
    void removeProductFromCartDeletesLineWhenOnlyOneUnitRemains() {
        Purchase purchase = purchaseWithLine(1);
        PurchaseLine storedLine = purchase.getLines().get(0);
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.of(purchase));
        when(purchaseLineRepository.findByPurchase_IdAndProduct_Id(purchaseId, productId))
                .thenReturn(Optional.of(storedLine));

        Purchase result = purchaseService.removeProductFromCart(productId, user);

        assertTrue(result.getLines().isEmpty());
        assertEquals(0.0, result.getTotalPrice());
        verify(purchaseLineRepository).delete(storedLine);
        verify(purchaseLineRepository, never()).save(any());
    }

    // Verifica que al intentar eliminar un producto del carrito si el usuario es nulo se lance una excepción y no se realice ninguna operación de eliminación o guardado en el repositorio
    @Test
    void removeProductFromCartThrowsWhenUserIsNull() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.removeProductFromCart(productId, null));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verifyNoInteractions(purchaseLineRepository);
    }

    // Verifica que al intentar eliminar un producto del carrito, si no hay un carrito activo para el usuario se lance una excepción y no se realice ninguna operación de eliminación o guardado en el repositorio
    @Test
    void removeProductFromCartThrowsWhenThereIsNoActiveCart() {
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.removeProductFromCart(productId, user));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(purchaseLineRepository);
    }

    // Verifica que al intentar eliminar un producto del carrito, si el producto no está en el carrito activo se lance una excepción y no se realice ninguna operación de eliminación o guardado en el repositorio
    @Test
    void removeProductFromCartThrowsWhenProductIsNotInCart() {
        Purchase purchase = purchaseWithLine(2);
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.of(purchase));
        when(purchaseLineRepository.findByPurchase_IdAndProduct_Id(purchaseId, productId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.removeProductFromCart(productId, user));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // Verifica que al obtener o crear un carrito para un usuario, si ya existe un carrito activo para ese usuario, se devuelve ese carrito y si no existe se crea uno nuevo asignando el usuario, estableciendo la fecha de creación, y luego guardando en el repositorio y devolviendolo
    @Test
    void getOrCreateCartForUserReturnsRepositoryResult() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findFirstByUserIdAndPurchaseStatus(userId, PurchaseStatus.INITIATED))
                .thenReturn(Optional.of(purchase));

        Optional<Purchase> result = purchaseService.getOrCreateCartForUser(userId);

        assertTrue(result.isPresent());
        assertEquals(purchase, result.get());
    }

    // Verifica que al completar una compra, si la compra existe y tiene líneas se actualiza el estatus de la compra a FINISHED y se establece la fecha de finalización, por último se guarda la compra actualizada en el repositorio
    @Test
    void completePurchaseMarksPurchaseAsFinished() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        purchaseService.completePurchase(purchaseId);

        assertEquals(PurchaseStatus.FINISHED, purchase.getPurchaseStatus());
        assertNotNull(purchase.getFinishedDate());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que al intentar completar una compra que no existe, se lance una excepción y no se guarde ningún cambio en el repositorio
    @Test
    void completePurchaseThrowsWhenPurchaseDoesNotExist() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.completePurchase(purchaseId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // Verifica que al intentar completar una compra que no tiene líneas se lance una excepción y no se guarde ningún cambio en el repositorio
    @Test
    void completePurchaseThrowsWhenPurchaseHasNoLines() {
        Purchase purchase = Purchase.builder().id(purchaseId).lines(new ArrayList<>()).build();
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.completePurchase(purchaseId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(purchaseRepository, never()).save(any());
    }

    // Verifica que al obtener todas las compras, se devuelven los resultados que devuelve el repositorio
    @Test
    void getAllPurchasesReturnsRepositoryResults() {
        List<Purchase> purchases = List.of(purchaseWithLine(1), purchaseWithLine(2));
        when(purchaseRepository.findAll()).thenReturn(purchases);

        assertEquals(purchases, purchaseService.getAllPurchases());
    }

    // Verifica que al buscar una compra por ID, si la compra existe devuelva la compra en un optional, y si no existe devuelva un optional vacío
    @Test
    void getPurchaseByIdReturnsRepositoryResult() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        Optional<Purchase> result = purchaseService.getPurchaseById(purchaseId);

        assertTrue(result.isPresent());
        assertEquals(purchase, result.get());
    }

    // Verifica que al actualizar el modo de envío de una compra, se actualiza correctamente el campo de modo de envío y se guarda la compra actualizada en el repositorio
    @Test
    void updatePurchaseStatusSavesUpdatedPurchase() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        Purchase result = purchaseService.updatePurchaseStatus(purchaseId, PurchaseStatus.INACTIVE);

        assertEquals(PurchaseStatus.INACTIVE, result.getPurchaseStatus());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que al actualizar el modo de envío de una compra, se actualiza correctamente el campo de modo de envío y se guarda la compra actualizada en el repositorio
    @Test
    void updatePaymentStatusSavesUpdatedPurchase() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        Purchase result = purchaseService.updatePaymentStatus(purchaseId, PaymentStatus.PAID);

        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que al actualizar el modo de envío de una compra, se actualiza correctamente el campo de modo de envío y se guarda la compra actualizada en el repositorio
    @Test
    void updateProcessStatusSavesUpdatedPurchase() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        Purchase result = purchaseService.updateProcessStatus(purchaseId, ProcessStatus.COMPLETED);

        assertEquals(ProcessStatus.COMPLETED, result.getProcessStatus());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que al actualizar el modo de envío de una compra, se actualiza correctamente el campo de modo de envío y se guarda la compra actualizada en el repositorio
    @Test
    void updateShippingStatusSavesUpdatedPurchase() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        Purchase result = purchaseService.updateShippingStatus(purchaseId, ShippingStatus.DELIVERED);

        assertEquals(ShippingStatus.DELIVERED, result.getShippingStatus());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que al actualizar el modo de envío de una compra, se actualiza correctamente el campo de modo de envío y se guarda la compra actualizada en el repositorio
    @Test
    void updateShippingModeSavesUpdatedPurchase() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        Purchase result = purchaseService.updateShippingMode(purchaseId, ShippingMode.EXPRESS);

        assertEquals(ShippingMode.EXPRESS, result.getShippingMode());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que al actualizar el comentario de usuario de una compra, se actualiza correctamente el campo de comentario adicional del envío y se guarda la compra actualizada en el repositorio
    @Test
    void updateUserCommentSavesUpdatedPurchase() {
        Purchase purchase = purchaseWithLine(1);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        Purchase result = purchaseService.updateUserComment(purchaseId, "Leave at door");

        assertEquals("Leave at door", result.getUserComment());
        verify(purchaseRepository).save(purchase);
    }

    // Verifica que al intentar actualizar el estatus de una compra que no existe, se lance una excepción
    @Test
    void updatePurchaseStatusThrowsWhenPurchaseDoesNotExist() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseService.updatePurchaseStatus(purchaseId, PurchaseStatus.INACTIVE));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // Verifica que al eliminar una compra se elimine correctamente de la base de datos
    @Test
    void deletePurchaseDelegatesToRepository() {
        purchaseService.deletePurchase(purchaseId);

        verify(purchaseRepository).deleteById(purchaseId);
    }

    // Función auxiliar para crear compras con una purchaseline para evitar repetir código en varias pruebas
    private Purchase purchaseWithLine(int quantity) {
        Purchase purchase = Purchase.builder()
                .id(purchaseId)
                .user(user)
                .purchaseStatus(PurchaseStatus.INITIATED)
                .lines(new ArrayList<>())
                .build();
        PurchaseLine line = PurchaseLine.builder()
                .purchase(purchase)
                .product(product)
                .quantity(quantity)
                .build();
        purchase.getLines().add(line);
        return purchase;
    }
}