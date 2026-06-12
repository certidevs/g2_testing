package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum ShippingMode {

    // Modos de envío asociados a la velocidad y el tipo de servicio de envío seleccionado por el cliente
    STANDARD ("Estándar"), // Modo de envío estándar, con un tiempo de entrega típico.
    EXPRESS ("Express"), // Modo de envío express, con un tiempo de entrega más rápido que el estándar.
    PREMIUM ("Premium"); // Modo de envío premium, con un tiempo de entrega aún más rápido que el express y posiblemente con servicios adicionales como seguimiento en tiempo real o entrega en horarios específicos.

    // Etiquetas legibles para cada modo de envío, útiles para mostrar en la interfaz de usuario o para propósitos de registro.
    private final String label;
    ShippingMode(String label) {
        this.label = label;
    }
}
