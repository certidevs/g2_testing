package com.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import com.ecommerce.model.enums.PaymentMethod;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@AllArgsConstructor
@Builder

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    // User's name
    private String name;

    // User's last name
    private String lastName;

    // User's phone
    @Column(unique = true)
    private String phone;

    // User's password
    @Column(nullable = false)
    private String password;

    // User's Birthday
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime birthday;

    // User's gender
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // User's email
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    // Role enum
    @Enumerated(EnumType.STRING)
    private Role role;

    // Date of creation
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    // Admin can deactivate users without removing historical data
    @Builder.Default
    private boolean active = true;

    // Payment method enum
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // Simulated card data for academic/testing purposes.
    // We do NOT store the full card number or CVV.
    private String cardHolderName;

    private String cardBrand;

    private String cardLastFourDigits;

    private Integer cardExpirationMonth;

    private Integer cardExpirationYear;

    // Un usuario tiene MUCHAS direcciones
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // Un usuario tiene muchas compras
    @OneToMany
    @Builder.Default
    private List<Purchase> purchases = new ArrayList<>();
}
