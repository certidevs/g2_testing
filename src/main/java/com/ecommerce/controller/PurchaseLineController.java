package com.ecommerce.controller;

import com.ecommerce.repository.PurchaseLineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class PurchaseLineController {

    private final PurchaseLineRepository purchaseLineRepository;

    @GetMapping
    public String listPurchaseLines(Model model) {
        model.addAttribute("purchaseLines", purchaseLineRepository.findAll());
        return "purchase_line/purchase_line-list";
    }
}
