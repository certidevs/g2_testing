package com.ecommerce.controller;

import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.service.PurchaseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("purchases")
    public String listPurchases(Model model) {
        model.addAttribute("purchases", purchaseRepository.findAll());
        return "purchases/purchases-list";
    }

    @GetMapping("purchases/{id}")
    public String detailPurchase(Model model, @PathVariable UUID id) {
        model.addAttribute("purchase", purchaseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        model.addAttribute("purchaseLines", purchaseLineRepository.findByPurchaseId(id));
        return "purchases/purchase-detail";
    }

    @GetMapping("purchases/delete/{id}")
    public String deletePurchase(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        purchaseRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Purchase deleted successfully");
        return "redirect:/purchases";
    }
}