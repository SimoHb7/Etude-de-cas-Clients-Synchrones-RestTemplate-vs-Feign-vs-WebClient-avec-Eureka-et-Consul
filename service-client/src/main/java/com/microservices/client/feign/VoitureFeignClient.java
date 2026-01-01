package com.microservices.client.feign;

import com.microservices.client.model.Voiture;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client pour le service voiture
 * Le nom "service-voiture" correspond au spring.application.name du service cible
 */
@FeignClient(name = "service-voiture")
public interface VoitureFeignClient {

    @GetMapping("/api/cars/byClient/{clientId}")
    Voiture getVoitureByClient(@PathVariable("clientId") Long clientId);
}
