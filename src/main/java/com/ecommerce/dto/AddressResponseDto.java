package com.ecommerce.dto;

import com.ecommerce.model.enums.AddressType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponseDto {

    private UUID id;
    private String street;
    private String number;
    private String complement;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private AddressType addressType;
    private UUID usersId;
    private String usersName;
    private String usersEmail;
}
