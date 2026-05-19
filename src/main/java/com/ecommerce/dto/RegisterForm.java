package com.ecommerce.dto;

import lombok.Builder;
import lombok.Data;


@Data
public class RegisterForm {

    private String username;
    private String email;
    private String password;
    private String passwordConfirm;
    // private Boolean acceptRGPD;
}
