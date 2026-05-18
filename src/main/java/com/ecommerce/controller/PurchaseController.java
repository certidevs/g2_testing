package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.PurchaseLine;
import com.ecommerce.model.enums.ProductStockStatus;
import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.PurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseLineRepository purchaseLineRepository;
    private final PurchaseService purchaseService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("purchases")
    public String listPurchases(Model model) {
        model.addAttribute("purchases", purchaseRepository.findAll());
        return "purchases/purchase-list";
    }

    @GetMapping("purchases/{id}")
    public String detailPurchase(Model model, @PathVariable UUID id) {
        model.addAttribute("purchase", purchaseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        model.addAttribute("purchaseLines", purchaseLineRepository.findByPurchaseId(id));
        return "purchases/purchase-detail";
    }

    @GetMapping("purchases/delete/{id}")
    public String deletePurchase(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        purchaseService.deletePurchase(id);
        redirectAttributes.addFlashAttribute("message", "Purchase deleted successfully");
        return "redirect:/purchases";
    }

    @GetMapping("purchases/new")
    public String showCreatePurchaseForm(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("users", userRepository.findAll());
        return "purchases/purchase-form";
    }

    @PostMapping("purchases")
    public String createPurchase(@ModelAttribute Purchase newPurchase) {
        purchaseService.createPurchase(newPurchase);
        return "redirect:/purchases";
    }

    @GetMapping("purchases/add/{productId}")
    public String addProduct(@PathVariable UUID productId) {

        // primero verificar que el product existe:
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (product.getStockStatus() == ProductStockStatus.NO_STOCK) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto sin stock");
        }

        // primero verificar si existe ya una purchase in progress
        Optional<Purchase> purchaseOptional = purchaseRepository.findFirstByPurchaseStatus(PurchaseStatus.INITIATED);

        Purchase purchase;
        if (purchaseOptional.isPresent()) {
            purchase = purchaseOptional.get();
        } else {
            purchase = new Purchase();
            // TODO purchase.setUser(currentUser);
            purchase.setPurchaseStatus(PurchaseStatus.INITIATED);
            purchase.setCreationDate(LocalDateTime.now());
            purchaseRepository.save(purchase);
        }

        // añadir producto a la compra:
        Optional<PurchaseLine> lineOptional = purchaseLineRepository
                .findByPurchase_IdAndProduct_Id(purchase.getId(), product.getId());

        PurchaseLine purchaseLine;
        if (lineOptional.isPresent()) {
            purchaseLine = lineOptional.get();
            purchaseLine.setQuantity(purchaseLine.getQuantity() + 1);
        } else {
            purchaseLine =  new PurchaseLine();
            purchaseLine.setProduct(product);
            purchaseLine.setPurchase(purchase);
            purchaseLine.setQuantity(1);
        }

        purchaseLineRepository.save(purchaseLine);

        // recalcular precio de la Purchase para mostrarlo por pantalla
        Double totalPrice = purchaseLineRepository.calculateTotalPrice(purchase.getId());
        purchase.setTotalPrice(totalPrice);
        purchaseRepository.save(purchase);



        // TODO atualizar stock y status del product tras añadir al carrito

        return "redirect:/purchases/" + purchase.getId();

    }

    // @GetMapping("purchases/delete/{productId}")

    // @GetMapping("purchases/finish/{id}")
}