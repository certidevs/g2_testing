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
    private UUID id; // Id de la dirección

    private String country; // País
    private String city; // Ciudad
    private String state; // Estado o provincia
    private String zipCode; // Código postal
    private String street; // Calle
    private String number; // Número de la calle
    private String complement; // Complemento de la dirección, como apartamento, casa, etc.

    // Tipo de dirección, puede ser de facturación o de envío, se utiliza para diferenciar entre las direcciones asociadas a un usuario o a una orden
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    // Asociación con el usuario al que pertenece esta dirección, un usuario puede tener varias direcciones, pero cada dirección pertenece a un solo usuario
    @ManyToOne
    private User user;

    // Asociación con la compra a la que pertenece esta dirección: una compra puede tener varias direcciones,
    // pero cada dirección pertenece a una sola compra. Por tanto aquí debe ser ManyToOne.
    @ManyToOne
    private Purchase purchase;
}
