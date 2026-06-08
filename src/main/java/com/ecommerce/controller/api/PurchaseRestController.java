package com.ecommerce.controller.api;

import com.ecommerce.model.Purchase;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.*;

// http://localhost:8080/swagger-ui/index.html
@RestController
@RequestMapping("/api/v1/purchases")
@AllArgsConstructor
public class PurchaseRestController {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseLineRepository purchaseLineRepository;

    // Muestra todas las compras
    @GetMapping
    public List<Purchase> findAllPurchases() { return purchaseRepository.findAll(); }

    // Muestra una compra por id
    @GetMapping("{id}")
    public Purchase findOnePurchase(@PathVariable UUID id){
        return purchaseRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra con id " + id + " no encontrada")
        );
    }

    // Crear una compra
    @PostMapping
    public ResponseEntity<Purchase> create(@RequestBody Purchase purchase) {
        if (purchase.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase ID tiene que ser null");
        }
        Purchase saved = purchaseRepository.save(purchase);
        //        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        return ResponseEntity.created(URI.create("/api/v1/purchases/" + saved.getId())).body(saved);
    }

    // Actualizar compra: actualización completa, si un campo se manda null también se guarda como null o default
    @PutMapping("{id}")
    public ResponseEntity<Purchase> update(@PathVariable UUID id, @RequestBody Purchase purchase) {
        Purchase existing = purchaseRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase con id " + id + " no encontrada")
        );
        existing.setUser(purchase.getUser());
        existing.setAddress(purchase.getAddress());
        existing.setCreationDate(purchase.getCreationDate());
        existing.setPaymentStatus(purchase.getPaymentStatus());
        existing.setPurchaseStatus(purchase.getPurchaseStatus());
        existing.setShippingStatus(purchase.getShippingStatus());
        existing.setProcessStatus(purchase.getProcessStatus());
        existing.setShippingMode(purchase.getShippingMode());
        existing.setLines(purchase.getLines());
        existing.setTotalPrice(purchase.getTotalPrice());
        existing.setUserComment(purchase.getUserComment());
        existing.setFinishedDate(purchase.getFinishedDate());

        // Como alternativa se podría usar DTOs y mappers
        // existing.setStartDate(purchase.getStartDate()); // Conservar fecha original
        return ResponseEntity.ok(purchaseRepository.save(existing));
    }



    // Update partial

    // Delete
}
