package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum ShippingMode {
    STANDARD ("STANDARD"),
    EXPRESS ("EXPRESS"),
    PREMIUM ("PREMIUM");

    private final String label;

    ShippingMode(String label) {
        this.label = label;
    }
}
