package com.ecommerce.dto;

import com.ecommerce.model.enums.PaymentStatus;
import com.ecommerce.model.enums.ProcessStatus;
import com.ecommerce.model.enums.PurchaseStatus;
import com.ecommerce.model.enums.ShippingMode;
import com.ecommerce.model.enums.ShippingStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseResponseDto {

    private UUID id;
    private LocalDateTime creationDate;
    private LocalDateTime finishedDate;
    private PurchaseStatus purchaseStatus;
    private ShippingMode shippingMode;
    private ShippingStatus shippingStatus;
    private PaymentStatus paymentStatus;
    private ProcessStatus processStatus;
    private Double totalPrice;
    private String userComment;
    private UUID userId;
    private String userName;
    private String userEmail;
}
