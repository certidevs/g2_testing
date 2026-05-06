package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum ShippingStatus {
    PENDING ("PENDIENTE"),
    SHIPPED ("ENVIADO"),
    IN_TRANSIT ("EN TRÁNSITO"),
    OUT_FOR_DELIVERY ("EN REPARTO"),
    DELIVERED ("ENTREGADO");

    private final String label;

    ShippingStatus(String label) {
        this.label = label;
    }
}
