package com.ecommerce.controller;

import com.ecommerce.model.Purchase;
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

import java.util.UUID;

@Controller
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseLineRepository purchaseLineRepository;
    private final PurchaseService purchaseService;
    private final UserRepository userRepository;

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

    @PostMapping("purchases/create")
    public String createPurchase(@ModelAttribute Purchase newPurchase) {
        purchaseService.createPurchase(newPurchase);
        return "redirect:/purchases";
    }

    @GetMapping("purchases/create")
    public String showCreatePurchaseForm(Model model) {
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("users", userRepository.findAll());
        return "purchases/purchase-form";
    }
}