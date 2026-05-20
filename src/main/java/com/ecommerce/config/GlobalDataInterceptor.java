package com.ecommerce.config;

import com.ecommerce.repository.FavoriteRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.model.User;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class GlobalDataInterceptor implements HandlerInterceptor {

    private FavoriteRepository favoriteRepository;
    private UserRepository userRepository;

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                           @NonNull Object handler, ModelAndView modelAndView) throws Exception {

        if (modelAndView != null) {
            // 1. Intentamos obtener el nombre del usuario autenticado en Spring Security
            java.security.Principal principal = request.getUserPrincipal();

            if (principal != null) {
                String username = principal.getName();
                // Buscamos en la base de datos SOLO al usuario que está navegando
                java.util.Optional<User> userOpt = userRepository.findByUsername(username);

                if (userOpt.isPresent()) {
                    UUID userId = userOpt.get().getId();

                    // 2. Agregamos sus favoritos reales
                    modelAndView.addObject("favoritesCount", favoriteRepository.countByUserId(userId));

                    // 3. Agregamos el carrito (Si tu entidad User tiene relación con Cart, pónselo.
                    // Si no, dejamos un 0 temporal para que el Navbar no explote buscando la variable)
                    modelAndView.addObject("cartCount", 0L);
                    return;
                }
            }

            // Si no hay nadie logueado (invitado), los contadores van a 0 obligatoriamente
            modelAndView.addObject("favoritesCount", 0L);
            modelAndView.addObject("cartCount", 0L);
        }
    }
}