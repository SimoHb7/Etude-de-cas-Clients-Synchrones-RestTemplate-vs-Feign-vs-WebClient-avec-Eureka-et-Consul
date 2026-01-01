package com.microservices.voiture.service;

import com.microservices.voiture.model.Voiture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoitureService {

    @Value("${app.simulated-delay:0}")
    private int simulatedDelay;

    // Données en mémoire pour le lab
    private final Map<Long, List<Voiture>> voituresParClient = new HashMap<>();

    public VoitureService() {
        // Initialiser quelques données de test
        voituresParClient.put(1L, Arrays.asList(
                Voiture.builder().id(10L).marque("Toyota").modele("Yaris").clientId(1L).build(),
                Voiture.builder().id(11L).marque("Honda").modele("Civic").clientId(1L).build()
        ));
        voituresParClient.put(2L, Collections.singletonList(
                Voiture.builder().id(20L).marque("Ford").modele("Focus").clientId(2L).build()
        ));
        voituresParClient.put(3L, Arrays.asList(
                Voiture.builder().id(30L).marque("BMW").modele("Serie 3").clientId(3L).build(),
                Voiture.builder().id(31L).marque("Mercedes").modele("Classe A").clientId(3L).build(),
                Voiture.builder().id(32L).marque("Audi").modele("A4").clientId(3L).build()
        ));
    }

    public List<Voiture> getVoituresParClient(Long clientId) {
        // Simuler un délai de traitement (pour le lab)
        if (simulatedDelay > 0) {
            try {
                Thread.sleep(simulatedDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return voituresParClient.getOrDefault(clientId, Collections.emptyList());
    }

    public Voiture getVoitureParClient(Long clientId) {
        // Retourne la première voiture du client (pour simplifier)
        List<Voiture> voitures = getVoituresParClient(clientId);
        return voitures.isEmpty() ? null : voitures.get(0);
    }
}
