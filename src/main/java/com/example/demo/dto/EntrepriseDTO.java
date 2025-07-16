package com.example.demo.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.Entite.Entreprise;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
    public class EntrepriseDTO {
    private Long id;
    @NotBlank(message = "La matricule est obligatoire.")
    private String matricule;
     @NotBlank(message = "Le nom est obligatoire.")
    private String name;
     @NotBlank(message = "Le lien est obligatoire.")
    private String lien;
     @NotBlank(message = "L'adresse est obligatoire.")
    private String address;
    @JsonIgnore

     private List<userDTO> users;
     public EntrepriseDTO() {
}

      public EntrepriseDTO(Entreprise e) {
        this.id = e.getId();
        this.name = e.getName();
        this.address = e.getAddress();
        this.matricule = e.getMatricule();
        this.lien = e.getLien();
          this.users = e.getUsers()
            .stream()
            .map(userDTO::new)
            .collect(Collectors.toList());
    }
}


