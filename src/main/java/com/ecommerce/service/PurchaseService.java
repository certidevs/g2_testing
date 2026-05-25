package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final PurchaseLineRepository purchaseLineRepository;

    // Crea una nueva compra, calculando el total a partir de las líneas de compra y estableciendo la fecha de compra
    public void createPurchase(Purchase purchase, User user) {
        double total = 0.0;
        for (PurchaseLine line : purchase.getPurchaseLines()) {
            line.setPurchase(purchase);
            total += line.getPrice() * line.getQuantity();
        }
        purchase.setTotalAmount(total);
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setPaymentStatus(PaymentStatus.PENDING);
        purchase.setProcessStatus(ProcessStatus.PENDING);
        purchase.setShippingMode(ShippingMode.STANDARD);
        purchase.setShippingStatus(ShippingStatus.PENDING);
        purchase.setUserComment("Sin comentario adicional para el envío");
        purchase.setUser(user);
        purchaseRepository.save(purchase);
    }

    // ---- [ AGREGAR AL CARRITO DE COMPRAS ] ----

    // Agrega un producto al carrito de compras del usuario
    @Transactional
    public Purchase addProductToCart(UUID productId, User user) {
        UUID currentUserId = getCurrentUserId(user);

        // Verifieca que el producto exista
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verifica que el producto tenga stock disponible, si no, lanza una excepción indicando que el producto está agotado
        if (product.getStock() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is out of stock");
        }

        // Verifica que el usuario tenga un carrito de compras iniciado
        Optional<Purchase> purchaseOptional = purchaseRepository.findFirstByUserIdAndPurchaseStatus(currentUserId, PurchaseStatus.INITIATED);

        // Si el carrito de compras ya existe, lo obtenemos, si no, lo creamos y lo asociamos al usuario
        Purchase purchase;
        if (purchaseOptional.isPresent()) {
            purchase = purchaseOptional.get();
        } else {
            purchase = new Purchase();
            purchase.setUser(user);
            purchase.setPurchaseStatus(PurchaseStatus.INITIATED);
            purchase.setCreationDate(LocalDateTime.now());
            purchase.setTotalPrice(0.0);
        }

        // Guardamos la compra para obtener su ID and poder asociar las líneas de compra, si es que se ha creado una nueva compra, si ya existía, esta operación no tiene efecto
        purchase = purchaseRepository.save(purchase);

        // Verificamos si ya existe una línea de compra para ese producto en la compra
        Optional<PurchaseLine> lineOptional = purchaseLineRepository
                .findByPurchase_IdAndProduct_Id(purchase.getId(), product.getId());

        // Si la línea de compra ya existe, verificamos que no se supere el stock disponible al agregar una unidad más
        PurchaseLine purchaseLine;
        if (lineOptional.isPresent()) {
            purchaseLine = lineOptional.get();

            // Si la cantidad actual de la línea de compra es mayor o igual al stock disponible, se lanza una excepción indicando que no se pueden agregar más unidades de ese producto por límite de stock
            if (purchaseLine.getQuantity() >= product.getStock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not add more units of this product, stock limit reached");
            }

            // Agregamos una unidad más a la cantidad de la línea de compra existente
            purchaseLine.setQuantity(purchaseLine.getQuantity() + 1);

            //  Mantener el estado del objeto compra actualizado con los cambios en las líneas de compra
            boolean lineSynchronized = false;
            for (PurchaseLine l : getMutableLines(purchase)) {
                if (l.getProduct().getId().equals(product.getId())) {
                    l.setQuantity(purchaseLine.getQuantity());
                    lineSynchronized = true;
                }
            }
            if (!lineSynchronized) {
                getMutableLines(purchase).add(purchaseLine);
            }
        } else {
            // Si el producto no tiene una línea de compra en la compra, se crea una nueva línea de compra con cantidad 1 y se asocia al producto y a la compra
            purchaseLine = new PurchaseLine();
            purchaseLine.setProduct(product);
            purchaseLine.setPurchase(purchase);
            purchaseLine.setQuantity(1);
            getMutableLines(purchase).add(purchaseLine);
        }

        purchaseLineRepository.save(purchaseLine);

        // Calcula el precio total de la compra sumando el precio de cada línea de compra (price * quantity) y lo actualiza en la compra
        double totalPrice = 0.0;
        for (PurchaseLine line : getMutableLines(purchase)) {
            totalPrice += line.getProduct().getPrice() * line.getQuantity();
        }

        purchase.setTotalPrice(totalPrice);

        // Guarda la compra actualizada
        return purchaseRepository.save(purchase);
    }

    // ---- [ ELIMINAR DE CARRITO DE COMPRAS ] ----

    // Resta un producto o disminuye su cantidad en el carrito de compras del usuario
    @Transactional
    public Purchase removeProductFromCart(UUID productId, User user) {
        UUID currentUserId = getCurrentUserId(user);

        // Verifica que el usuario tenga un carrito de compras iniciado
        Purchase purchase = purchaseRepository.findFirstByUserIdAndPurchaseStatus(currentUserId, PurchaseStatus.INITIATED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active cart found"));

        // Buscamos si existe la línea de compra para ese producto
        PurchaseLine purchaseLine = purchaseLineRepository
                .findByPurchase_IdAndProduct_Id(purchase.getId(), productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart"));

        if (purchaseLine.getQuantity() > 1) {
            // Si hay más de una unidad, restamos una
            purchaseLine.setQuantity(purchaseLine.getQuantity() - 1);
            purchaseLineRepository.save(purchaseLine);

            // Mantener el estado sincronizado en memoria
            boolean lineSynchronized = false;
            for (PurchaseLine l : getMutableLines(purchase)) {
                if (l.getProduct().getId().equals(productId)) {
                    l.setQuantity(purchaseLine.getQuantity());
                    lineSynchronized = true;
                }
            }
            // En caso de que la línea no esté sincronizada en memoria, la agregamos para mantener el estado actualizado
            if (!lineSynchronized) {
                getMutableLines(purchase).add(purchaseLine);
            }
        } else {
            // Si queda solo una unidad, eliminamos la línea por completo
            getMutableLines(purchase).remove(purchaseLine);
            purchaseLineRepository.delete(purchaseLine);
        }

        // Volvemos a calcular el precio total actualizado de la compra
        double totalPrice = 0.0;
        for (PurchaseLine line : getMutableLines(purchase)) {
            totalPrice += line.getProduct().getPrice() * line.getQuantity();
        }
        purchase.setTotalPrice(totalPrice);

        return purchaseRepository.save(purchase);
    }

    // Verifica si el usuario tiene un carrito de compra iniciado, si lo tiene, lo devuelve, si no, devuelve un Optional vacío
    public Optional<Purchase> getOrCreateCartForUser(UUID currentUserId) {
        return purchaseRepository.findFirstByUserIdAndPurchaseStatus(currentUserId, PurchaseStatus.INITIATED);
    }

    // Función auxiliar para obtener el ID del usuario autenticado
    private UUID getCurrentUserId(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required");
        }
        return user.getId();
    }

    // Asegura que las operaciones de carrito puedan trabajar aunque la lista de líneas venga sin inicializar
    private List<PurchaseLine> getMutableLines(Purchase purchase) {
        if (purchase.getLines() == null) {
            purchase.setLines(new ArrayList<>());
        }
        return purchase.getLines();
    }

    // Verifica que la compra exista y tenga líneas de compra antes de marcarla como finalizada
    @Transactional
    public void completePurchase(UUID id) {
        // Se utiliza la función interna getPurchaseEntityById para reutilizar código y evitar repetir la verificación de existencia
        Purchase purchase = getPurchaseEntityById(id);

        // Verifica que la compra tenga líneas de compra
        if (!purchase.hasLines()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not finish a purchase without lines");
        }

        // Actualiza el estatus de la compra a finalizada y establece la fecha de finalización a la fecha y hora actual, luego guarda los cambios en la base de datos
        purchase.setPurchaseStatus(PurchaseStatus.FINISHED);
        purchase.setFinishedDate(LocalDateTime.now());
        purchaseRepository.save(purchase);
    }

    // Muestra todas las compras
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    // Muestra la compra por su ID, devuelve un Optional vacío si no existe
    public Optional<Purchase> getPurchaseById(UUID id) {
        return purchaseRepository.findById(id);
    }

    // Ayuda para obtener la entidad de compra por su ID, lanza una excepción si no existe, se utiliza internamente en las funciones de actualización para evitar repetir el código de verificación de existencia
    private Purchase getPurchaseEntityById(UUID id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found with id: " + id));
    }

    // ---- [ FUNCIONES DE ACTUALIZACIONES NO UTILIZADAS ] ----

    // Actualiza el estatus de la compra
    public Purchase updatePurchaseStatus(UUID id, PurchaseStatus purchaseStatus) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setPurchaseStatus(purchaseStatus);
        return purchaseRepository.save(purchase);
    }

    // Actualiza el estatus del pago
    public Purchase updatePaymentStatus(UUID id, PaymentStatus paymentStatus) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setPaymentStatus(paymentStatus);
        return purchaseRepository.save(purchase);
    }

    // Actualiza el estatus del proceso
    public Purchase updateProcessStatus(UUID id, ProcessStatus processStatus) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setProcessStatus(processStatus);
        return purchaseRepository.save(purchase);
    }

    // Actualiza el estatus del envío
    public Purchase updateShippingStatus(UUID id, ShippingStatus shippingStatus) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setShippingStatus(shippingStatus);
        return purchaseRepository.save(purchase);
    }

    // Actualiza el modo de envío
    public Purchase updateShippingMode(UUID id, ShippingMode shippingMode) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setShippingMode(shippingMode);
        return purchaseRepository.save(purchase);
    }

    // Actualiza el comentario opcional del usuario para la entrega de la compra
    public Purchase updateUserComment(UUID id, String comment) {
        Purchase purchase = getPurchaseEntityById(id);
        purchase.setUserComment(comment);
        return purchaseRepository.save(purchase);
    }

    // ---- [ FUNCIONES DE ELIMINACIÓN ] ----

    // Elimina una compra por su ID
    public void deletePurchase(UUID id) {
        purchaseRepository.deleteById(id);
    }
}