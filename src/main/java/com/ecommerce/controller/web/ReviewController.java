package com.ecommerce.controller.web;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;

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

    @GetMapping("/reviews/{id}")
    public String showReviewDetail(@PathVariable UUID id,
                                   Model model,
                                   HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            csrfToken.getToken();
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada: " + id));

        model.addAttribute("review", review);

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
    @PostMapping("/products/{id}/reviews/add")
    public String addReview(@PathVariable UUID id,
                            @RequestParam String title,
                            @RequestParam Integer rating,
                            @RequestParam String message,
                            java.security.Principal principal) {

        // 1. Control de seguridad en el servidor (por si se saltan el HTML)
        if (principal == null) {
            return "redirect:/login";
        }

        // 2. Obtener de forma segura quién está logueado
        String username = principal.getName();

        // 3. Buscar el usuario en la base de datos
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        // 4. Buscar el producto en la base de datos usando el ID de la ruta
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));

        // 5. Crear la Review pasando las variables correctas y guardarla
        Review review = new Review();
        review.setTitle(title);
        review.setRating(rating);
        review.setMessage(message);
        review.setUser(user);
        review.setProduct(product);

        reviewRepository.save(review);

        // 6. Redirigir al detalle del producto
        return "redirect:/products/" + id;
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
