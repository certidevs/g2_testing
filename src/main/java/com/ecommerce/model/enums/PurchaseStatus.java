package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PurchaseStatus {
    INITIATED ("INICIADO"),
    INACTIVE ("INACTIVO"),
    FINISHED ("TERMINADO");

    private final String label;

    PurchaseStatus(String label) {
        this.label = label;
    }
}