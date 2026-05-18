package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.model.Brand;
import com.ecommerce.model.Category;
import com.ecommerce.model.enums.ProductStockStatus;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.FavoriteRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.BrandRepository;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ProductControler {
    private ProductRepository productRepository;
    private ReviewRepository  reviewRepository;
    private FavoriteRepository favoriteRepository;
    private UserRepository userRepository;
    private ReviewService reviewService;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;


    @GetMapping("/products")
    public String products(Model model) {
        //MODEl
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("saludo", "MODA DE VERANO");
        return "products/product-list";

    }
    //agregar el numero de compras y reviews en el productos
    @GetMapping("products/{id}")
    public String productsDetail(@PathVariable UUID id, Model model) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            // TODO cambiar products a product porque es solo uno
            model.addAttribute("product", product);
            List<Review> reviews = reviewService.getApprovedReviewsByProduct(id);
            model.addAttribute("reviews", reviews);

        } else {
            return "redirect:/products";
        }
        return "products/product-detail";
    }
//Buscador de productos
        @GetMapping("/products/search")
        public String searchProducts(@RequestParam String query, Model model) {
            List<Product> products = productRepository.findByTitleContainingIgnoreCaseOrShortDescriptionContainingIgnoreCase(query, query);
            model.addAttribute("products", products);
            return "products/product-list";
        }
        //Desactivar un producsto
    @GetMapping("products/deactivate/{id}")
    public String deactivateRestaurant(@PathVariable UUID id, Model model) {
        productRepository.findById(id).ifPresent(product -> {
            product.setAvailable(false);
            productRepository.save(product);
        });
        return "redirect:/products";
    }
    //Activar un producto
         @GetMapping("products/activate/{id}")
         public String activateProduct(@PathVariable UUID id, Model model) {
             productRepository.findById(id).ifPresent(product -> {
                 product.setAvailable(true);
                 productRepository.save(product);
             });
             return "redirect:/products";
         }
         //CREACION DE PRODUCTO
    @GetMapping("/products/new")
    public String navigateToForm(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("allStockStatuses", ProductStockStatus.values());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("subcategories", categoryRepository.findAll());
        return "products/product-form";
    }


    //RECIBIR LOS DATOS DEL PRODUCTO
    @PostMapping("products")
    public String createProduct(@ModelAttribute Product product){

        productRepository.save(product);
        return "redirect:/products";
    }
    @GetMapping("/products/categories/{id}")
    public String listProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        // Si no pones esta línea, la línea 131 de tu HTML saldrá en amarillo/error
        model.addAttribute("categories", categoryRepository.findAll());
        return "products/product-list";
    }
}
