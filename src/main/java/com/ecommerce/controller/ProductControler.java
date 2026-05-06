package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
       if (productOptional.isPresent()) {
           Product product = productOptional.get();
           model.addAttribute("products", product);  // Agrega el producto al modelo con el nombre "products"
           List<Review> reviews = reviewRepository.findByProductId(id);  // Usa la instancia y el método correcto
           model.addAttribute("reviews", reviews);
       } else {
           // Manejo si el producto no existe: redirige a la lista para evitar error 500
           return "redirect:/products";
       }
       return "products/product-detail";
   }
        @GetMapping("/products/search")
        public String searchProducts(@RequestParam String query, Model model) {
            List<Product> products = productRepository.findByTitleContainingIgnoreCaseOrShortDescriptionContainingIgnoreCase(query, query);
            model.addAttribute("products", products);
            model.addAttribute("saludo", "Resultados para: " + query);
            return "products/product-list";
        }
    @GetMapping("products/deactivate/{id}")
    public String deactivateRestaurant(@PathVariable UUID id, Model model) {
        productRepository.findById(id).ifPresent(product -> {
            product.setAvailable(false);
            productRepository.save(product);
        });
        return "redirect:/products";
    }
         @GetMapping("products/activate/{id}")
         public String activateProduct(@PathVariable UUID id, Model model) {
             productRepository.findById(id).ifPresent(product -> {
                 product.setAvailable(true);
                 productRepository.save(product);
             });
             return "redirect:/products";
         }
}
