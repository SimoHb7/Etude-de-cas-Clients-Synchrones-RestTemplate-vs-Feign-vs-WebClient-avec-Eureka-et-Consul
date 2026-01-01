package com.microservices.client.controller;

import com.microservices.client.model.Voiture;
import com.microservices.client.service.VoitureClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private VoitureClientService voitureClientService;

    /**
     * Endpoint 1 : Appel avec RestTemplate
     * GET /api/clients/{id}/car/rest
     */
    @GetMapping("/{id}/car/rest")
    public ResponseEntity<Voiture> getCarWithRestTemplate(@PathVariable Long id) {
        try {
            Voiture voiture = voitureClientService.getVoitureWithRestTemplate(id);
            return ResponseEntity.ok(voiture);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Endpoint 2 : Appel avec Feign
     * GET /api/clients/{id}/car/feign
     */
    @GetMapping("/{id}/car/feign")
    public ResponseEntity<Voiture> getCarWithFeign(@PathVariable Long id) {
        try {
            Voiture voiture = voitureClientService.getVoitureWithFeign(id);
            return ResponseEntity.ok(voiture);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Endpoint 3 : Appel avec WebClient
     * GET /api/clients/{id}/car/webclient
     */
    @GetMapping("/{id}/car/webclient")
    public ResponseEntity<Voiture> getCarWithWebClient(@PathVariable Long id) {
        try {
            Voiture voiture = voitureClientService.getVoitureWithWebClient(id);
            return ResponseEntity.ok(voiture);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Endpoint de santé
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service Client is UP");
    }
}
