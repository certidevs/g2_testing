package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum ProcessStatus {
    PENDING ("PENDIENTE"),
    PROCESSING ("PROCESANDO"),
    ON_HOLD ("EN ESPERA"),
    COMPLETED ("COMPLETADO"),
    CANCELLED ("CANCELADO");

    private final String label;

    ProcessStatus(String label) {
        this.label = label;
    }
}
