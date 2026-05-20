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

    // proteger rutas
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.authorizeHttpRequests(
                auth -> auth
                //GENERAL
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/webjars/**", "/images/**").permitAll()

                //PRODUCT
//                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
//
//                //CATEGORY
//                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
//
//                //BRANDS
//                .requestMatchers(HttpMethod.GET, "/brands/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/brands/**").hasRole("ADMIN")
//
//                //REVIEW
//                .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/reviews/**").hasRole("ADMIN")
//
//                //PURCHASE
//                .requestMatchers(HttpMethod.GET, "/purchases/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/purchases/**").hasRole("ADMIN")


                .anyRequest().permitAll()
        );

        http.formLogin(
          form -> form
                  .loginPage("/login")
                  .defaultSuccessUrl("/products")
                  .permitAll()
        );
// AVISO: Esta combinación permite añadir comentarios pero abre una vulnerabilidad CSRF.
// Cualquiera podría forzar a un usuario autenticado a postear una review falsa.
        http.csrf(csrf -> csrf
            .ignoringRequestMatchers("/products/*/reviews/add")
    );
http.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    );
        return http.build();
    }
}