package com.ecommerce.model;

import com.ecommerce.model.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "address")
@AllArgsConstructor
@Builder

public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String street;
    private String number;
    private String complement;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    // Tipo de dirección (principal, secundaria, etc.)
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @ManyToOne
    private Users user;
}
