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
            List<User> users = userRepository.findAll();

            if (!users.isEmpty()) {
                UUID userId = users.getFirst().getId();
                long favoritesCount = favoriteRepository.countByUserId(userId);
                modelAndView.addObject("favoritesCount", favoritesCount);
            } else {
                modelAndView.addObject("favoritesCount", 0L);
            }
        }
    }
}