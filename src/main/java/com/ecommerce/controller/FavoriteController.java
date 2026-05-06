package com.ecommerce.controller;

import com.ecommerce.model.Favorite;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.FavoriteRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.transaction.annotation.Transactional; // Ya no es necesario aquí

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class FavoriteController {

    private FavoriteRepository favoriteRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;

    // Mostrar lista de favoritos
    @GetMapping("/favorites")
    // @Transactional // Eliminado, ya que JOIN FETCH en el repositorio maneja la carga
    public String listFavorites(Model model) {
        // Por ahora usamos un usuario de ejemplo (después se integraría con autenticación)
        List<User> users = userRepository.findAll();

        if (!users.isEmpty()) {
            UUID userId = users.getFirst().getId();
            // Usar el nuevo método que carga los productos de forma eager
            List<Favorite> favorites = favoriteRepository.findByUserIdWithProducts(userId);
            List<Product> favoriteProducts = favorites.stream()
                    .map(Favorite::getProduct)
                    .collect(Collectors.toList());

            model.addAttribute("favoriteProducts", favoriteProducts);
            model.addAttribute("saludo", "Mis productos favoritos");
        } else {
            model.addAttribute("favoriteProducts", List.of());
            model.addAttribute("saludo", "No hay favoritos");
        }

        return "favorite/favorites-list";
    }

    // Agregar a favoritos
    @PostMapping("/favorites/add/{productId}")
    public String addFavorite(@PathVariable UUID productId) {
        List<User> users = userRepository.findAll();

        if (!users.isEmpty()) {
            User user = users.get(0);
            Optional<Product> productOpt = productRepository.findById(productId);

            if (productOpt.isPresent() &&
                favoriteRepository.findByUserIdAndProductId(user.getId(), productId).isEmpty()) {

                Favorite favorite = Favorite.builder()
                        .user(user)
                        .product(productOpt.get())
                        .build();

                favoriteRepository.save(favorite);
            }
        }

        return "redirect:/products/" + productId;
    }

    // Eliminar de favoritos
    @PostMapping("/favorites/remove/{productId}")
    public String removeFavorite(@PathVariable UUID productId) {
        List<User> users = userRepository.findAll();

        if (!users.isEmpty()) {
            Optional<Favorite> favorite = favoriteRepository.findByUserIdAndProductId(
                    users.get(0).getId(),
                    productId
            );
            favorite.ifPresent(favoriteRepository::delete);
        }

        return "redirect:/products/" + productId;
    }

    // Verificar si un producto está en favoritos
    @GetMapping("/favorites/check/{productId}")
    public String checkFavorite(@PathVariable UUID productId, Model model) {
        List<User> users = userRepository.findAll();

        if (!users.isEmpty()) {
            boolean isFavorite = favoriteRepository.findByUserIdAndProductId(
                    users.get(0).getId(),
                    productId
            ).isPresent();

            model.addAttribute("isFavorite", isFavorite);
        }

        return "favorites/check";
    }
}