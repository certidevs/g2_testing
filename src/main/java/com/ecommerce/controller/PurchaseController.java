package com.ecommerce.controller;

import com.ecommerce.model.Purchase;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.PurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseLineRepository purchaseLineRepository;
    private final PurchaseService purchaseService;
    private final UserRepository userRepository;

    // Muestra la lista de todas las compras
    @Transactional
    @GetMapping("purchases")
    public String listPurchases(Model model) {
        model.addAttribute("purchases", purchaseRepository.findAll());
        return "purchases/purchase-list";
    }

    // Muestra el detalle de una compra específica con las líneas de compra
    @GetMapping("purchases/{id}")
    public String detailPurchase(Model model, @PathVariable UUID id) {
        model.addAttribute("purchase", purchaseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        model.addAttribute("lines", purchaseLineRepository.findByPurchaseId(id));
        return "purchases/purchase-detail";
    }

    // Muestra el formulario para crear una nueva compra
    @GetMapping("purchases/new")
    public String showCreatePurchaseForm(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("users", userRepository.findAll());
        return "purchases/purchase-form";
    }

    // Procesa el formulario para crear una nueva compra
    @PostMapping("purchases")
    public String createPurchase(@ModelAttribute Purchase newPurchase) {
        purchaseService.createPurchase(newPurchase);
        return "redirect:/purchases";
    }

    // Elimina una compra específica
    @GetMapping("purchases/delete/{id}")
    public String deletePurchase(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        purchaseService.deletePurchase(id);
        redirectAttributes.addFlashAttribute("message", "Purchase deleted successfully");
        return "redirect:/purchases";
    }

    // Agrega un producto a la compra
    @GetMapping("purchases/add/{productId}")
    public String addProduct(@PathVariable UUID productId) {

        // TODO [Requiere reemplazar el usuario simulado por el usuario autenticado en la aplicación en la función de agregar un producto al carrito del usuario actual]
        // ID temporal simulado para identificar al usuario actual en esta petición
        UUID currentUserId = UUID.randomUUID();
        Purchase purchase = purchaseService.addProductToCart(productId, currentUserId);
        return "redirect:/purchases/" + purchase.getId();
    }

    // Muestra el carrito de compras del usuario actual, que es la compra con estatus iniciado (INITIATED) asociada al usuario actual, si no hay ninguna compra iniciada para el usuario actual, se muestra un carrito vacío
    @GetMapping("/purchases/{id}/cart")
    public String showCart(Model model) {

        // ---- [ CAMBIAR EL ID TEMPORAL ] ----
        // ID temporal simulado para identificar al usuario actual
        UUID currentUserId = UUID.randomUUID();

        Optional<Purchase> purchaseOptional = purchaseService.getOrCreateCartForUser(currentUserId);

        // Si hay un carrito iniciado para el usuario actual, pasamos el modelo a la vista
        if (purchaseOptional.isPresent()) {
            Purchase cart = purchaseOptional.get();
            model.addAttribute("cart", cart);

            // Pasamos las línes de la compra al modelo, si no hay líneas, se pasará un array vacío, para mostrar un carrito vacío en la vista
            model.addAttribute("lines", cart.getPurchaseLines());
        } else {
            // Si no hay ningún carrito iniciado para el usuario actual, pasamos un carrito vacío al modelo, con líneas vacías, para mostrar un carrito vacío en la vista
            model.addAttribute("cart", null);
        }
        return "purchases/cart";
    }

    // Finalizar la compra, cambiando su estatus a finalizada (FINISHED) y estableciendo la fecha de finalización a la fecha y hora actual, antes de finalizar la compra se ejecuta una validación para comprobar que la compra tiene líneas de compra, si no tiene líneas, se lanza una excepción y no se finaliza la compra
    @GetMapping("purchases/{id}/finish")
    public String finishPurchase(@PathVariable UUID id) {
        purchaseService.completePurchase(id);
        return "redirect:/purchases";
    }

    // ---- [ BOTONES + Y - ] ----

    // Incrementar cantidad desde los detalles de la compra
    @GetMapping("purchases/{purchaseId}/lines/add/{productId}")
    public String incrementLineQuantity(@PathVariable UUID purchaseId, @PathVariable UUID productId) {
        // ---- [ CAMBIAR EL ID TEMPORAL ] ----
        // ID temporal simulado para identificar al usuario actual
        UUID currentUserId = UUID.randomUUID();

        purchaseService.addProductToCart(productId, currentUserId);
        return "redirect:/purchases/" + purchaseId;
    }

    // Decrementar cantidad desde los detalles de la compra
    @GetMapping("purchases/{purchaseId}/lines/remove/{productId}")
    public String decrementLineQuantity(@PathVariable UUID purchaseId, @PathVariable UUID productId) {
        // ---- [ CAMBIAR EL ID TEMPORAL ] ----
        // ID temporal simulado para identificar al usuario actual
        UUID currentUserId = UUID.randomUUID();

        purchaseService.removeProductFromCart(productId, currentUserId);
        return "redirect:/purchases/" + purchaseId;
    }
}
