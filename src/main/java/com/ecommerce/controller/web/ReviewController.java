package com.ecommerce.controller.web;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.ui.Model;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ReviewController {
    private ReviewRepository reviewRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;

    @GetMapping("reviews") // CONTROLADOR
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll()); // MODELO
        return "reviews/reviews-list"; // VISTA
    }

    @GetMapping("reviews/{id}")
    public String reviewsDetail(@PathVariable UUID id, Model model) {
        model.addAttribute("review", reviewRepository.findById(id).orElseThrow());
        return "reviews/reviews-detail";
    }

    @GetMapping("reviews/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        reviewRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Reseña eliminada correctamente");
        return "redirect:/reviews";
    }
    @PostMapping("reviews")
    public String save(@ModelAttribute Review review) {
        reviewRepository.save(review);
        if (review.getProduct() != null)
            return "redirect:/products/" + review.getProduct().getId();
        return "redirect:/reviews";

    }
    @PostMapping("/products/{productId}/reviews/add")
    public String addReview(@PathVariable UUID productId,
                            @RequestParam("rating") int rating,
                            @RequestParam("message") String message,
                            java.security.Principal principal) {

        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            Review.ReviewBuilder reviewBuilder = Review.builder()
                    .rating(rating)
                    .message(message)
                    .product(product);

            // Si hay un usuario logueado (Admin, cliente, etc.), lo enlazamos
            if (principal != null) {
                Optional<User> userOpt = userRepository.findByUsername(principal.getName());
                userOpt.ifPresent(reviewBuilder::user);
            } else {
                // Opcional: Si es invitado, puedes dejar el usuario como null
                // o buscar un usuario comodín en tu BD llamado "Anónimo"
                reviewBuilder.user(null);
            }

            reviewRepository.save(reviewBuilder.build());
        }

        return "redirect:/products/" + productId;
    }
    @PostMapping("/products/{productId}/reviews/edit/{reviewId}")
    public String editReview(@PathVariable UUID productId,
                             @PathVariable UUID reviewId,
                             @RequestParam("rating") int rating,
                             @RequestParam("message") String message) {

        // 1. Buscamos la reseña existente en la base de datos
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();

            // 2. Modificamos los valores con los nuevos datos del formulario
            review.setRating(rating);
            review.setMessage(message);

            // 3. Al hacer un .save() de un objeto que ya tiene ID, Spring Data hace un UPDATE en vez de un INSERT
            reviewRepository.save(review);
        }

        // 4. Redirigimos al detalle del producto para ver el cambio reflejado inmediatamente
        return "redirect:/products/" + productId;
    }
    @PostMapping("/reviews/edit/{id}")
    public String updateReview(@PathVariable UUID id,
                               @RequestParam("title") String title,
                               @RequestParam("rating") int rating,
                               @RequestParam("message") String message) {

        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            review.setTitle(title);
            review.setRating(rating);
            review.setMessage(message);
            reviewRepository.save(review);
        }

        // Redirige de vuelta a la lista o al detalle de la reseña
        return "redirect:/reviews/" + id;
    }
}
