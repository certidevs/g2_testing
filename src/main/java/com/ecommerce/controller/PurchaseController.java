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

        // 1. Verificar que el producto existe y validar stock
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (product.getStock() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto sin stock disponible");
        }

        // 2. Verificar si existe ya una compra en progreso
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

        // Guardamos para asegurar que la compra tenga un ID válido
        purchase = purchaseRepository.save(purchase);

        // 3. Añadir o actualizar producto en la compra
        Optional<PurchaseLine> lineOptional = purchaseLineRepository
                .findByPurchase_IdAndProduct_Id(purchase.getId(), product.getId());

        PurchaseLine purchaseLine;
        if (lineOptional.isPresent()) {
            purchaseLine = lineOptional.get();

            if (purchaseLine.getQuantity() >= product.getStock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes añadir más unidades que las disponibles en stock");
            }

            // Sumamos 1 a la cantidad de la línea
            purchaseLine.setQuantity(purchaseLine.getQuantity() + 1);


            // Recorremos la lista interna de la compra en memoria y actualizamos la línea con la nueva cantidad
            if (purchase.getLines() != null) {
                for (PurchaseLine l : purchase.getLines()) {
                    if (l.getProduct().getId().equals(product.getId())) {
                        l.setQuantity(purchaseLine.getQuantity());
                    }
                }
            }
        } else {
            purchaseLine = new PurchaseLine(); //
            purchaseLine.setProduct(product);
            purchaseLine.setPurchase(purchase);
            purchaseLine.setQuantity(1);
            purchase.getLines().add(purchaseLine);
        }

        // Guardamos la línea de forma independiente
        purchaseLineRepository.save(purchaseLine);

        // 4. Recalcular precio de la Purchase de forma segura
        Double totalPrice = purchaseLineRepository.calculateTotalPrice(purchase.getId());
        if (totalPrice == null) {
            totalPrice = product.getPrice() * purchaseLine.getQuantity();
        }

        purchase.setTotalPrice(totalPrice);

        // Ahora que la lista interna de 'purchase' y 'purchaseLine' coinciden, guardamos la compra sin pisar los datos
        purchaseRepository.save(purchase);

        return "redirect:/purchases/" + purchase.getId();

    }
    //Programando el botn de carrito de la navbar
    @GetMapping("/purchases/cart")
    public String showCart(Model model) {
        // 1. Buscamos si el usuario ya tiene un carrito iniciado en la base de datos
        Optional<Purchase> purchaseOptional = purchaseRepository.findFirstByPurchaseStatus(PurchaseStatus.INITIATED);

        if (purchaseOptional.isPresent()) {
            Purchase cart = purchaseOptional.get();
            model.addAttribute("cart", cart);
            // Pasamos también las líneas de pedido (los productos que están dentro del carrito)
            model.addAttribute("lines", cart.getPurchaseLines());
        } else {
            // Si no hay carrito iniciado, pasamos un atributo vacío o indicamos que está vacío
            model.addAttribute("cart", null);
        }

        return "purchases/cart"; // Esto buscará un archivo llamado cart.html dentro de templates/purchases/
    }

    // @GetMapping("purchases/delete/{productId}")

    // @GetMapping("purchases/finish/{id}")
}