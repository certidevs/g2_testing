package com.ecommerce.controller;

import com.ecommerce.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class ProductControler {
    private ProductRepository productRepository;


    @GetMapping("/products")
    public String products(Model model) {
        //MODEl
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("saludo", "Bienvenido a la lista de productos");
        return "products/product-list";


    }
}
