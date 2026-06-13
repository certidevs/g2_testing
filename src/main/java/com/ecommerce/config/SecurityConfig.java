package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig
{
    // cifrar y verificar el passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    // proteger rutas
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
//    {
//        http.authorizeHttpRequests(
//                auth -> auth
//                //GENERAL
//                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/webjars/**", "/images/**").permitAll()
//
//                //PRODUCT
////                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
////                .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
////
////                //CATEGORY
////                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
////                .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
////
////                //BRANDS
////                .requestMatchers(HttpMethod.GET, "/brands/**").permitAll()
////                .requestMatchers(HttpMethod.POST, "/brands/**").hasRole("ADMIN")
////
////                //REVIEW
////                .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
////                .requestMatchers(HttpMethod.POST, "/reviews/**").hasRole("ADMIN")
////
////                //PURCHASE
////                .requestMatchers(HttpMethod.GET, "/purchases/**").permitAll()
////                .requestMatchers(HttpMethod.POST, "/purchases/**").hasRole("ADMIN")
//
//
//                .anyRequest().permitAll()
//        );
//
//        http.formLogin(
//          form -> form
//                  .loginPage("/login")
//                  .defaultSuccessUrl("/products")
//                  .permitAll()
//        );
//// AVISO: Esta combinación permite añadir comentarios pero abre una vulnerabilidad CSRF.
//// Cualquiera podría forzar a un usuario autenticado a postear una review falsa.
//
//http.sessionManagement(session -> session
//            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
//    );
//        return http.build();
//    }


@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // El widget envía JSON desde un JS estático; ignoramos CSRF solo para este endpoint.
    http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/chatbot"));

    http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())); // h2 usa iframes

    http.authorizeHttpRequests(
            auth -> auth
                    // ORDEN IMPORTANTE
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/api/v1/**").permitAll()
                    .requestMatchers(
                            "/error",
                            "/login",
                            "/register",
                            "/uploads/**",
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/webjars/**",
                            "/favicon.ico"
                    ).permitAll()

                    // El chatbot puede responder a visitantes; el historial sí exige login.
                    .requestMatchers(HttpMethod.POST, "/api/chatbot").permitAll()
                    .requestMatchers(HttpMethod.GET, "/chatbot/history", "/chatbot/history/*").authenticated()

                    //PRODUCTS
                    .requestMatchers(HttpMethod.GET, "/products").permitAll()
                    .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/products/deactivate/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/products/new").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/products/add").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/products/edit/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/products/*").permitAll()

                    //CATEGORIES
                    .requestMatchers(HttpMethod.GET, "/categories/new").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/categories/*/edit").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/categories").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/categories/*/edit").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/categories/*/delete").hasRole("ADMIN")

                    .requestMatchers(HttpMethod.GET, "/categories").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categories/tree").permitAll()
                    .requestMatchers(HttpMethod.GET, "/categories/*").permitAll()

                    //BRANDS
                    .requestMatchers(HttpMethod.GET, "/brands/new").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/brands/*/edit").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/brands").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/brands/*/edit").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/brands/*/delete").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/brands").permitAll()
                    .requestMatchers(HttpMethod.GET, "/brands/*").permitAll()

                    //USERS
                    .requestMatchers("/admin/users", "/admin/users/**").hasRole("ADMIN")

                    //REVIEWS
                    .requestMatchers(HttpMethod.GET, "/reviews").permitAll()
                    .requestMatchers(HttpMethod.POST, "/reviews").authenticated()
                    .requestMatchers(HttpMethod.GET, "/reviews/new").authenticated()
                    .requestMatchers(HttpMethod.GET, "/reviews/edit/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/reviews/disable/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/reviews/delete/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/reviews/*").permitAll()

                    //PURCHASES
                    .requestMatchers("/purchases", "/purchases/**").authenticated()
                    .anyRequest().authenticated()
    );

    //
    http.formLogin(form ->
            form.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/products", true)
                    .failureUrl("/login?error")
                    .permitAll()
    );

    return http.build();
}
}
