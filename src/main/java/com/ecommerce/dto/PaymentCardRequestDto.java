package com.ecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCardRequestDto
{
    @NotBlank(message = "El nombre del titular es obligatorio")
    private String cardHolderName;

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(
            regexp = "^[0-9 ]{13,23}$",
            message = "El número de tarjeta debe contener entre 13 y 19 dígitos"
    )
    private String cardNumber;

    @NotNull(message = "El mes de expiración es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer expirationMonth;

    @NotNull(message = "El año de expiración es obligatorio")
    @Min(value = 2026, message = "La tarjeta no puede estar expirada")
    private Integer expirationYear;

    @NotBlank(message = "El CVV es obligatorio")
    @Pattern(
            regexp = "^[0-9]{3,4}$",
            message = "El CVV debe tener 3 o 4 dígitos"
    )
    private String cvv;
}
