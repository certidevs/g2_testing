package com.ecommerce.service;

import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.Purchase;
import com.ecommerce.repository.PurchaseLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseLineService {

    private final PurchaseLineRepository purchaseLineRepository;

    // Obtiene todas las líneas de compra
    public List<PurchaseLine> getAllPurchaseLines() {
        return purchaseLineRepository.findAll();
    }

    // Obtiene una línea de compra por su ID
    public Optional<PurchaseLine> getPurchaseLineById(UUID id) {
        return purchaseLineRepository.findById(id);
    }

    // FUnción auxiliar para obtener una línea de compra por su ID o lanzar una excepción si no se encuentra
    private PurchaseLine getPurchaseLineEntityById(UUID id) {
        return purchaseLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseLine not found with id: " + id));
    }

    // Crea una nueva línea de compra
    @Transactional
    public PurchaseLine createPurchaseLine(PurchaseLine purchaseLine) {
        return purchaseLineRepository.save(purchaseLine);
    }

    // Actualiza una línea de compra existente
    @Transactional
    public PurchaseLine updatePurchaseLine(UUID id, PurchaseLine purchaseLineDetails) {
        PurchaseLine line = getPurchaseLineEntityById(id);
        if (purchaseLineDetails.getQuantity() > 0)
            line.setQuantity(purchaseLineDetails.getQuantity());
        if (purchaseLineDetails.getProduct() != null)
            line.setProduct(purchaseLineDetails.getProduct());
        if (purchaseLineDetails.getPurchase() != null)
            line.setPurchase(purchaseLineDetails.getPurchase());
        return purchaseLineRepository.save(line);
    }

    // Actualiza la cantidad de una línea de compra
    @Transactional
    public PurchaseLine updateQuantity(UUID id, int newQuantity) {
        PurchaseLine line = getPurchaseLineEntityById(id);
        line.setQuantity(newQuantity);
        return purchaseLineRepository.save(line);
    }

    // Elimina una línea de compra por su ID
    @Transactional
    public void deletePurchaseLine(UUID id) {
        purchaseLineRepository.deleteById(id);
    }

    // Obtiene todas las líneas de compra asociadas a una compra específica
    public List<PurchaseLine> getPurchaseLinesByPurchase(Purchase purchase) {
        return purchaseLineRepository.findByPurchase(purchase);
    }
}