package com.ecommers.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@AllArgsConstructor
@ToString
public class Reviews {

    @Id
    private UUID id;
}
