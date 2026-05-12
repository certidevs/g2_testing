package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum ProductStockStatus {
    STOCK ("EN STOCK") ,
    RUN_OUT_STOCK ("POCO STOCK -10"),
    NO_STOCK("NO HAY STOCK");
    private final String label;
    ProductStockStatus(String label) {
        this.label = label;
    }
}
