package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import com.ecommerce.model.enums.PaymentMethods;
import com.ecommerce.model.enums.Gender;
import com.ecommerce.model.enums.Role;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@AllArgsConstructor
@Builder

public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // User's name
    private String name;

    // User's last name
    private String lastName;

    // User's phone
    private String phone;

    // User's password
    private String password;

    // User's Birthday
    private LocalDateTime birthday;

    // User's gender
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // User's email
    private String email;

    // Role enum
    @Enumerated(EnumType.STRING)
    private Role role;

    // Date of creation
    private LocalDateTime creationDate;

    // Payment method enum
    @Enumerated(EnumType.STRING)
    private PaymentMethods paymentMethod;

    // Un usuario tiene MUCHAS direcciones
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();
}
