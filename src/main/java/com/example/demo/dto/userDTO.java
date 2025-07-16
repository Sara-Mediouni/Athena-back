package com.example.demo.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.Entite.User;
import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Data
public class userDTO {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire.")
    private String name;

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email n'est pas valide.")
    private String email;

    @NotBlank(message = "Le rôle est obligatoire.")
    private String role;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    private String password;

    @NotEmpty(message = "Au moins une entreprise doit être sélectionnée.")
    private List<@Valid EntrepriseIdDTO> entreprises;

    // Constructeur vide obligatoire pour Jackson
    public userDTO() {}

    public userDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.entreprises = user.getEntreprises()
            .stream()
            .map(e -> new EntrepriseIdDTO(e.getId(),e.getName()))
            .collect(Collectors.toList());
    }
}
