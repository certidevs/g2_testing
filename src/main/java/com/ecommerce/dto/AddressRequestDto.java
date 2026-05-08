package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequestDto {

    @NotBlank(message = "La calle es obligatoria")
    private String street;

    @NotBlank(message = "El número es obligatorio")
    private String number;

    private String complement;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El estado/provincia es obligatorio")
    private String state;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    @NotBlank(message = "El código postal es obligatorio")
    private String zipCode;

    @NotNull(message = "El tipo de dirección es obligatorio")
    private com.ecommerce.model.enums.AddressType addressType;

    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID usersId;
}
