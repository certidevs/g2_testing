package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.model.enums.ProductStockStatus;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.FavoriteRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.BrandRepository;
import com.ecommerce.repository.CategoryRepository;
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

    private final ProductRepository productRepository;
    private final ReviewRepository  reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    // ==========================================
    //          VISTAS PÚBLICAS DE LA TIENDA
    // ==========================================

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("saludo", "MODA DE VERANO");
        return "products/product-list";
    }

    @GetMapping("products/{id}")
    public String productsDetail(@PathVariable UUID id, Model model) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);
            List<Review> reviews = reviewRepository.findByProductId(id);
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
        return "products/product-list";
    }

    @GetMapping("/products/categories/{id}")
    public String listProductsByCategory(@PathVariable UUID id, Model model) {
        // TODO: En el futuro filtra aquí usando el ID de la categoría: productRepository.findByCategoryId(id)
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "products/product-list";
    }


    // ==========================================
    //        PANEL DE GESTIÓN / ADMINISTRACIÓN
    // ==========================================

    // Nueva ruta para ver el listado de administración con la tabla y los modales
    @GetMapping("/admin/products/list")
    public String listAdminProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products/product-admin-list";
    }

    // Nueva ruta POST para actualizar el descuento de forma rápida desde el modal
    @PostMapping("/admin/products/update-discount")
    public String updateProductDiscount(@RequestParam("productId") UUID id,
                                        @RequestParam("discountPercentage") Integer discount) {

        productRepository.findById(id).ifPresent(product -> {
            product.setDiscountPercentage(discount);
            productRepository.save(product);
        });

        return "redirect:/admin/products/list";
    }

    @GetMapping("products/deactivate/{id}")
    public String deactivateProduct(@PathVariable UUID id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setAvailable(false);
            productRepository.save(product);
        });
        return "redirect:/admin/products/list"; // Redirige al panel de control tras desactivar
    }

    @GetMapping("products/activate/{id}")
    public String activateProduct(@PathVariable UUID id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setAvailable(true);
            productRepository.save(product);
        });
        return "redirect:/admin/products/list"; // Redirige al panel de control tras activar
    }

    @GetMapping("/products/new")
    public String navigateToForm(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("allStockStatuses", ProductStockStatus.values());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("subcategories", categoryRepository.findAll());
        return "products/product-form";
    }

    @PostMapping("products")
    public String createProduct(@ModelAttribute Product product){
        productRepository.save(product);
        return "redirect:/admin/products/list"; // volver al listado de admin para ver el resultado
    }
}