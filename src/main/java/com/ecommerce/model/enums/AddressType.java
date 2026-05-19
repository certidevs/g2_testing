package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum AddressType {

    // Tipos de dirección que se pueden asociar a un usuario o a una orden
    BILLING ("DIRECCIÓN DE FACTURACIÓN"), // Dirección de facturación
    SHIPPING ("DIRECCIÓN DE ENVÍO"); // Dirección de envío

    // Etiquetas legibles para cada tipo de dirección, útiles para mostrar en la interfaz de usuario o para propósitos de registro.
    private final String label;
    AddressType(String label) {this.label = label;}
}
