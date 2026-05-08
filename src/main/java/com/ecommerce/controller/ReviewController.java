package com.ecommerce.controller;

import com.ecommerce.model.Review;
import com.ecommerce.service.ReviewService;
import com.ecommerce.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService usersService;


    @GetMapping(value = "reviews", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8") // CONTROLADOR
    public String reviews(Model model, @RequestParam(required = false) String email) {
        boolean isAdmin = isAdmin(email);
        
        List<Review> reviews;
        if (isAdmin) {
            // ADMIN: Ve todas las reseñas de todos los usuarios
            reviews = reviewService.getAllReviews();
        } else if (email != null && !email.isBlank()) {
            // USER: Solo ve sus propias reseñas (puede ver el status)
            var user = usersService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            reviews = reviewService.getReviewsByUser(user.getId());
        } else {
            // Sin email: No mostrar reseñas (o mostrar vacío)
            reviews = List.of();
        }
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUserEmail", email);
        return "reviews/reviews-list"; // VISTA
    }

    // @GetMapping("reviews/{id}")
    @GetMapping(value = "reviews/{id}", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public String reviewDetail(Model model, @PathVariable UUID id, @RequestParam(required = false) String email) {
        boolean isAdmin = isAdmin(email);
        var review = reviewService.getReviewById(id).orElseThrow();
        model.addAttribute("review", review);

        if (review.getProduct() != null) {
            var baseRelated = isAdmin
                    ? reviewService.getReviewsByProduct(review.getProduct().getId())
                    : reviewService.getApprovedReviewsByProduct(review.getProduct().getId());
            var related = baseRelated.stream()
                    .filter(r -> !r.getId().equals(id))
                    .toList();
            model.addAttribute("relatedReviews", related);
        }

        model.addAttribute("isAdmin", isAdmin);
        return "reviews/reviews-detail";
    }

    // @GetMapping("reviews/product/{id}")
    @GetMapping(value = "reviews/product/{id}", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseBody
    public List<Review> findByProductId(@PathVariable UUID id) {
        return reviewService.getReviewsByProduct(id);
    }

    // @GetMapping("reviews/rating/{rating}")
    @GetMapping(value = "reviews/rating/{rating}", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseBody
    public List<Review> findByRating(@PathVariable Integer rating) {
        return reviewService.getReviewsByRating(rating);
    }

    @GetMapping("reviews/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        reviewService.deleteReview(id);
        redirectAttributes.addFlashAttribute("message", "Borrado exitosamente");
        return "redirect:/reviews";
    }

    private boolean isAdmin(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return usersService.findByEmail(email)
                .map(usersService::isAdmin)
                .orElse(false);
    }

}