package com.ecommerce.config;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ControllerAdvice
public class WebBindingConfig {

    @InitBinder
    public void registerDateEditors(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }

                // Los formularios HTML de tipo date envían yyyy-MM-dd;
                // el modelo usa LocalDateTime, así que guardamos el inicio del día.
                setValue(LocalDate.parse(text).atStartOfDay());
            }
        });
    }
}
