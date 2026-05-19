package com.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum ProcessStatus {

    PENDING ("PENDIENTE"), // El proceso está pendiente, lo que significa que aún no se ha iniciado o está en espera de ser procesado.
    PROCESSING ("PROCESANDO"), // El proceso está en curso, lo que indica que se están realizando las acciones necesarias para completar el proceso, como la preparación de un pedido o la ejecución de una tarea.
    ON_HOLD ("EN ESPERA"), // El proceso está en espera, lo que podría indicar que se ha detenido temporalmente debido a una condición específica, como la falta de información o la necesidad de una acción adicional antes de continuar.
    COMPLETED ("COMPLETADO"), // El proceso se ha completado con éxito, lo que significa que todas las acciones necesarias se han realizado y el proceso ha llegado a su fin.
    CANCELLED ("CANCELADO"); // El proceso ha sido cancelado, lo que indica que se ha detenido de manera permanente y no se completará, posiblemente debido a una solicitud del usuario o a una condición que impide su finalización.

    // Etiquetas legibles para cada estado del proceso, útiles para mostrar en la interfaz de usuario o para propósitos de registro.
    private final String label;
    ProcessStatus(String label) {
        this.label = label;
    }
}
