package com.microservices.client.service;

import com.microservices.client.feign.VoitureFeignClient;
import com.microservices.client.model.Voiture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class VoitureClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VoitureFeignClient voitureFeignClient;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${voiture.service.name:service-voiture}")
    private String voitureServiceName;

    /**
     * Méthode 1 : RestTemplate (synchrone classique)
     */
    public Voiture getVoitureWithRestTemplate(Long clientId) {
        String url = "http://" + voitureServiceName + "/api/cars/byClient/" + clientId;
        return restTemplate.getForObject(url, Voiture.class);
    }

    /**
     * Méthode 2 : Feign Client (déclaratif)
     */
    public Voiture getVoitureWithFeign(Long clientId) {
        return voitureFeignClient.getVoitureByClient(clientId);
    }

    /**
     * Méthode 3 : WebClient (utilisé en mode synchrone avec block())
     */
    public Voiture getVoitureWithWebClient(Long clientId) {
        String url = "http://" + voitureServiceName + "/api/cars/byClient/" + clientId;
        
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Voiture.class)
                .block(); // Blocking pour mode synchrone dans ce lab
    }
}
