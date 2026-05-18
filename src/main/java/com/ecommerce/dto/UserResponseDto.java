package com.ecommerce.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto
{
    private UUID id;
    private String name;
    private String email;
    private Boolean active;
}
