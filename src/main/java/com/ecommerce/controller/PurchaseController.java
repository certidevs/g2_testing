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

    // Show all purchases
    @GetMapping("purchases")
    public String listPurchases(Model model) {
        model.addAttribute("purchases", purchaseRepository.findAll());
        return "purchases/purchase-list";
    }

    // Show purchase details of an specific purchase
    @GetMapping("purchases/{id}")
    public String detailPurchase(Model model, @PathVariable UUID id) {
        model.addAttribute("purchase", purchaseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        model.addAttribute("purchaseLines", purchaseLineRepository.findByPurchaseId(id));
        return "purchases/purchase-detail";
    }

    // Create a new purchase for an existing user
    @GetMapping("purchases/new")
    public String showCreatePurchaseForm(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("users", userRepository.findAll());
        return "purchases/purchase-form";
    }

    // Handle form submission to create a new purchase
    @PostMapping("purchases")
    public String createPurchase(@ModelAttribute Purchase newPurchase) {
        purchaseService.createPurchase(newPurchase);
        return "redirect:/purchases";
    }

    // Delete an specific purchase
    @GetMapping("purchases/delete/{id}")
    public String deletePurchase(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        purchaseService.deletePurchase(id);
        redirectAttributes.addFlashAttribute("message", "Purchase deleted successfully");
        return "redirect:/purchases";
    }

    // Add a product to the purchase
    @GetMapping("purchases/add/{productId}")
    public String addProduct(@PathVariable UUID productId) {

        // Verifies that the product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verifies that the product has stock available
        if (product.getStock() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto sin stock disponible");
        }

        // Verifies if the user has an initiated purchase, if not, creates a new one
        Optional<Purchase> purchaseOptional = purchaseRepository.findFirstByPurchaseStatus(PurchaseStatus.INITIATED);

        Purchase purchase;
        if (purchaseOptional.isPresent()) {
            purchase = purchaseOptional.get();
        } else {
            purchase = new Purchase();
            // TODO purchase.setUser(currentUser);
            purchase.setPurchaseStatus(PurchaseStatus.INITIATED);
            purchase.setCreationDate(LocalDateTime.now());
            purchase.setTotalPrice(0.0);
        }

        // Save the purchase to ensure it has an ID for the purchase line
        purchase = purchaseRepository.save(purchase);

        // Add or update the purchase line for the product
        Optional<PurchaseLine> lineOptional = purchaseLineRepository
                .findByPurchase_IdAndProduct_Id(purchase.getId(), product.getId());

        // If the purchaseline already exists, we update the quantity, if not, we create a new one
        PurchaseLine purchaseLine;
        if (lineOptional.isPresent()) {
            purchaseLine = lineOptional.get();

            if (purchaseLine.getQuantity() >= product.getStock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes añadir más unidades que las disponibles en stock");
            }

            // Add one unit to the quantity of the purchase line
            purchaseLine.setQuantity(purchaseLine.getQuantity() + 1);

            // Upload the new quantity to the purchase line in the purchase object, to keep the data consistent
            if (purchase.getLines() != null) {
                for (PurchaseLine l : purchase.getLines()) {
                    if (l.getProduct().getId().equals(product.getId())) {
                        l.setQuantity(purchaseLine.getQuantity());
                    }
                }
            }
        } else {

            // If the purchase line does not exist, we create a new one with quantity one
            purchaseLine = new PurchaseLine();
            purchaseLine.setProduct(product);
            purchaseLine.setPurchase(purchase);
            purchaseLine.setQuantity(1);
            purchase.getLines().add(purchaseLine);
        }

        purchaseLineRepository.save(purchaseLine);

        // Calculate the total price of the purchase based on the purchase lines, and update the purchase total price
        Double totalPrice = purchaseLineRepository.calculateTotalPrice(purchase.getId());
        if (totalPrice == null) {
            totalPrice = product.getPrice() * purchaseLine.getQuantity();
        }

        purchase.setTotalPrice(totalPrice);

        // Once it coincides, we save the purchase to update the total price and the purchase lines in the purchase object
        purchaseRepository.save(purchase);

        return "redirect:/purchases/" + purchase.getId();
    }

    // Show the cart with the products added to the purchase
    @GetMapping("/purchases/cart")
    public String showCart(Model model) {

        // Search for an initiated purchase
        Optional<Purchase> purchaseOptional = purchaseRepository.findFirstByPurchaseStatus(PurchaseStatus.INITIATED);

        // If there is an initiated purchase, we pass it to the model
        if (purchaseOptional.isPresent()) {
            Purchase cart = purchaseOptional.get();
            model.addAttribute("cart", cart);

            // Also pass the purchase lines to the model, so we can show the products in the cart
            model.addAttribute("lines", cart.getPurchaseLines());

        } else {

            // If the cart is empty, we pass a null value or an empty purchase object to the model
            model.addAttribute("cart", null);
        }

        return "purchases/cart"; // Esto buscará un archivo llamado cart.html dentro de templates/purchases/
    }

    // Finish the purchase
    @GetMapping("purchases/{id}/finish")
    public String finishPurchase(@PathVariable UUID id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verifies that the purchase has lines, if not, it cannot be finished
        if (purchase.getPurchaseLines() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not finish a purchase without lines");
        }

        // Update the purchase status to finished and save it
        purchase.setPurchaseStatus(PurchaseStatus.FINISHED);
        purchase.setFinishedDate(LocalDateTime.now());
        purchaseRepository.save(purchase);

        return "redirect:/purchases/" + id;
    }
}