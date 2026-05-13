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
    //agregar el numero de compras en el productos
    @GetMapping("products/{id}")
    public String productsDetail(@PathVariable UUID id, Model model) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("products", product);
            List<Review> reviews = reviewService.getApprovedReviewsByProduct(id);
            model.addAttribute("reviews", reviews);

        } else {
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
        // Si se proporciona un ID de marca, buscarla en la BD
        // TODO revisar porque creo que no hace falta ya viene asignado desde forms
//        if (product.getBrand() != null && product.getBrand().getId() != null) {
//            Optional<Brand> brand = brandRepository.findById(product.getBrand().getId());
//            brand.ifPresent(product::setBrand);
//        }
//
//        // Si se proporciona un ID de subcategoría, buscarla en la BD
//        if (product.getSubcategory() != null && product.getSubcategory().getId() != null) {
//            Optional<Category> category = categoryRepository.findById(product.getSubcategory().getId());
//            category.ifPresent(product::setSubcategory);
//        }
        
        productRepository.save(product);
        return "redirect:/products";
    }
}
