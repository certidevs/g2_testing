package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Reviews;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ProductControler {
    private ProductRepository productRepository;
    private ReviewRepository  reviewRepository;


    @GetMapping("/products")
    public String products(Model model) {
        //MODEl
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("saludo", "Bienvenido a la lista de productos");
        return "products/product-list";

    }
    //agregar el numero de compras en el productos
    @GetMapping("products/{id}")
    public String productsDetail(@PathVariable UUID id, Model model) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()){
            Product product = productOptional.get();
            model.addAttribute("products");
            List<Reviews> reviews = ReviewRepository.findByProduct_idOrderByCreationDateDesc(id);
            model.addAttribute("reviews", reviews);

        }
        return "products/product-detail";
    }
}
