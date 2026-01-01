package com.microservices.voiture.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voiture {
    private Long id;
    private String marque;
    private String modele;
    private Long clientId;
}
