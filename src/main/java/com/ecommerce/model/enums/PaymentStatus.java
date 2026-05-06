package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING ("PENDIENTE"),
    PAID ("PAGADO"),
    FAILED ("FALLIDO");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }
}
