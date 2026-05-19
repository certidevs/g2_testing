package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    PENDING ("PENDIENTE"), // El pago está pendiente, lo que significa que el cliente ha iniciado el proceso de pago pero aún no ha completado la transacción.
    PAID ("PAGADO"), // El pago ha sido completado con éxito, lo que indica que el cliente ha realizado el pago y la transacción ha sido procesada correctamente.
    FAILED ("FALLIDO"); // El pago ha fallado, lo que podría indicar que hubo un problema durante el proceso de pago, como una tarjeta de crédito rechazada o un error en la plataforma

    // Etiquetas legibles para cada estado de pago, útiles para mostrar en la interfaz de usuario o para propósitos de registro.
    private final String label;
    PaymentStatus(String label) {
        this.label = label;
    }
}
