package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PurchaseStatus {

    INITIATED ("INICIADO"), // El proceso de compra ha comenzado, pero aún no se han agregado productos al carrito o no se ha iniciado el proceso de pago.
    INACTIVE ("INACTIVO"), // El proceso de compra está inactivo, lo que podría indicar que el usuario ha abandonado el carrito o no ha interactuado con el proceso de compra durante un período prolongado.
    FINISHED ("TERMINADO"); // El proceso de compra está finalizado, lo que significa que el usuario ha completado el proceso de pago y la compra está lista para ser procesada y enviada.

    // Etiquetas legibles para cada estado de compra, útiles para mostrar en la interfaz de usuario o para propósitos de registro.
    private final String label;
    PurchaseStatus(String label) {
        this.label = label;
    }
}