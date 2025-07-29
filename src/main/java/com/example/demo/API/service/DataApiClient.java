package com.example.demo.API.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.API.Entity.DataApiEntity;
import com.example.demo.Entite.Entreprise;
import com.example.demo.Dao.EntrpriseRepository;
import com.example.demo.dto.EntrepriseDTO;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service



public class DataApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final EntrpriseRepository entRepo;

    public DataApiClient(EntrpriseRepository entRepo) {
        this.entRepo = entRepo;
    }

    public List<DataApiEntity> fetchData(LocalDate debut, LocalDate fin, String inclureBLs, String modeDate, Long id) {
        Entreprise entreprise = entRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée avec id: " + id));

        String weblink = entreprise.getLien();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Construction de l'URL en fonction des paramètres
        String baseUrl;
        if ("DateBL".equals(modeDate) && "true".equals(inclureBLs)) {
            baseUrl = "/api/CABLDBL";
        } else if ("DateFacture".equals(modeDate) && "true".equals(inclureBLs)) {
            baseUrl = "/api/CABLDF";
        } else if ("DateBL".equals(modeDate)) {
            baseUrl = "/api/CANBLDBL";
        } else {
            baseUrl = "/api/CANBLDF";
        }

        String fullUrl = "http://" + weblink + baseUrl;

        String apiUrl = UriComponentsBuilder.fromHttpUrl(fullUrl)
                .queryParam("_debut", debut.format(formatter))
                .queryParam("_fin", fin.format(formatter))
                .toUriString();

        DataApiEntity[] result = restTemplate.getForObject(apiUrl, DataApiEntity[].class);

        return Arrays.asList(result != null ? result : new DataApiEntity[0]);
    }
}
