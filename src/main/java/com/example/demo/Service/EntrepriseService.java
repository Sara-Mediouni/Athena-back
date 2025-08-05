package com.example.demo.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.demo.Dao.EntrpriseRepository;
import com.example.demo.Entite.Entreprise;
import com.example.demo.Entite.User;
import com.example.demo.dto.EntrepriseUpdateDTO;

import jakarta.transaction.Transactional;



@Service
public class EntrepriseService {

    private final EntrpriseRepository entrpriseRepository;

    public EntrepriseService(EntrpriseRepository entrpriseRepository) {
        this.entrpriseRepository = entrpriseRepository;
    }

    public Entreprise addEntreprise(String matricule, String name, String link, String address) {
        try {
            Entreprise ent = new Entreprise();
            ent.setAddress(address);
            ent.setName(name);
            ent.setLien(link);
            ent.setMatricule(matricule);
            return entrpriseRepository.save(ent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de l'entreprise : " + e.getMessage());
        }
    }
    public Entreprise getEntrepriseById(Long id) {
    try {
        return entrpriseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entreprise non trouvée avec l'id : " + id));
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Erreur lors du fetch de l'entreprise : " + e.getMessage());
    }
}

    public Optional<Entreprise> findEntrepriseByName(String name) {
        try {
            return entrpriseRepository.findByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche de l'entreprise : " + e.getMessage());
        }
    }
@Transactional
public void deleteEntrepriseById(Long id) {
    Entreprise entreprise = entrpriseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

     for (User user : entreprise.getUsers()) {
        user.getEntreprises().remove(entreprise);
    }

     entrpriseRepository.delete(entreprise);
}

@Transactional
public void updateEntrepriseById(Long id, EntrepriseUpdateDTO dto) {
    Entreprise entreprise = entrpriseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

    if (dto.getMatricule() != null && !dto.getMatricule().isBlank()) {
        entreprise.setMatricule(dto.getMatricule());
    }

    if (dto.getName() != null && !dto.getName().isBlank()) {
        entreprise.setName(dto.getName());
    }

    if (dto.getLink() != null && !dto.getLink().isBlank()) {
        entreprise.setLien(dto.getLink());
    }

    if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
        entreprise.setAddress(dto.getAddress());
    }
}





}


