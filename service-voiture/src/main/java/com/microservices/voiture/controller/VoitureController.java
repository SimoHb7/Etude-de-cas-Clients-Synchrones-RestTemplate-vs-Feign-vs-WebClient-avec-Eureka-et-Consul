package com.microservices.voiture.controller;

import com.microservices.voiture.model.Voiture;
import com.microservices.voiture.service.VoitureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class VoitureController {

    @Autowired
    private VoitureService voitureService;

    /**
     * Endpoint principal pour le lab : récupérer la voiture d'un client
     * GET /api/cars/byClient/{clientId}
     */
    @GetMapping("/byClient/{clientId}")
    public ResponseEntity<Voiture> getVoitureByClient(@PathVariable Long clientId) {
        Voiture voiture = voitureService.getVoitureParClient(clientId);
        
        if (voiture == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(voiture);
    }

    /**
     * Endpoint supplémentaire : récupérer toutes les voitures d'un client
     */
    @GetMapping("/byClient/{clientId}/all")
    public ResponseEntity<List<Voiture>> getAllVoituresByClient(@PathVariable Long clientId) {
        List<Voiture> voitures = voitureService.getVoituresParClient(clientId);
        return ResponseEntity.ok(voitures);
    }

    /**
     * Endpoint de santé simple
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service Voiture is UP");
    }
}
