package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum ShippingStatus {

    // Estados de envío asociados al progreso del envío de un pedido
    PENDING ("PENDIENTE"), // El envío está pendiente, lo que significa que aún no se ha procesado ni enviado el pedido.
    SHIPPED ("ENVIADO"), // El envío ha sido procesado y el pedido ha sido enviado, pero aún no ha llegado al destino.
    IN_TRANSIT ("EN TRÁNSITO"), // El envío está en tránsito, lo que indica que el pedido está en camino hacia su destino, pero aún no ha llegado.
    OUT_FOR_DELIVERY ("EN REPARTO"), // El envío está en reparto, lo que significa que el pedido ha llegado a la ciudad de destino y está siendo entregado al cliente.
    DELIVERED ("ENTREGADO"); // El envío ha sido entregado al cliente, lo que indica que el pedido ha llegado a su destino final y ha sido recibido por el cliente o alguien autorizado en su nombre.

    // Etiquetas legibles para cada estado de envío, útiles para mostrar en la interfaz de usuario o para propósitos de registro.
    private final String label;
    ShippingStatus(String label) {
        this.label = label;
    }
}
