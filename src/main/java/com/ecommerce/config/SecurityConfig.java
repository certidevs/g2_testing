package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig
{
    // cifrar y verificar el passwords
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    // proteger rutas
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.authorizeHttpRequests(
                auth -> auth
                        //GENERAL
                        .requestMatchers("/login", "/register", "/css/**", "/webjars/**", "/images/**").permitAll()

                        //PRODUCT
                        .requestMatchers(HttpMethod.GET, "/products/", "").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")

                        //CATEGORY
                        .requestMatchers(HttpMethod.GET, "/categories/tree").permitAll()
                        .requestMatchers(HttpMethod.POST, "").hasRole("ADMIN")

                        //BRANDS
                        .requestMatchers(HttpMethod.GET, "").permitAll()
                        .requestMatchers(HttpMethod.POST, "").hasRole("ADMIN")

                        //REVIEW
                        .requestMatchers(HttpMethod.GET, "").permitAll()
                        .requestMatchers(HttpMethod.POST, "").hasRole("ADMIN")

                        //PURCHASE
                        .requestMatchers(HttpMethod.GET, "").permitAll()
                        .requestMatchers(HttpMethod.POST, "").hasRole("ADMIN")
        );

        http.formLogin(
          form -> form
                  .loginPage("/login")
                  .defaultSuccessUrl("/products/")
                  .permitAll()
        );

        return http.build();
    }
}
