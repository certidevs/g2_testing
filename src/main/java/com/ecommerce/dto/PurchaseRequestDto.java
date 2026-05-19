package com.ecommerce.dto;

import com.ecommerce.model.enums.PaymentStatus;
import com.ecommerce.model.enums.ProcessStatus;
import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.model.enums.ShippingMode;
import com.ecommerce.model.enums.ShippingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRequestDto {

    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID userId;

    private PurchaseStatus purchaseStatus;
    private ShippingMode shippingMode;
    private ShippingStatus shippingStatus;
    private PaymentStatus paymentStatus;
    private ProcessStatus processStatus;
    private Double totalPrice;
    private String userComment;
}
