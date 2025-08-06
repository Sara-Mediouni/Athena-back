package com.example.demo.Service;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Dao.EntrpriseRepository;
import com.example.demo.Dao.UserRepository;
import com.example.demo.Entite.Entreprise;
import com.example.demo.Entite.User;
import com.example.demo.dto.EntrepriseIdDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.dto.userDTO;
import com.example.demo.enums.Role;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired
private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final EntrpriseRepository entrepriseRepository;
        private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public UserService(UserRepository userRepository, EntrpriseRepository entrepriseRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.passwordEncoder = passwordEncoder;
    }

public User addUser(String name, String email, String role, String password, List<Long> entrepriseIds) {
    try {
        
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un utilisateur avec cet email existe déjà.");
        }
        System.out.println("add user called");
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setRole(Role.valueOf(role.toUpperCase()));

        List<Entreprise> entreprises = entrepriseRepository.findAllById(entrepriseIds);
        if (entreprises.size() != entrepriseIds.size()) {
            throw new IllegalArgumentException("Une ou plusieurs entreprises sont introuvables.");
        }
        user.setEntreprises(entreprises);

        return userRepository.save(user);

    } catch (ResponseStatusException e) {
        
        throw e;
    } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de l'utilisateur", e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erreur lors de l'ajout de l'utilisateur : " + e.getMessage(),
                e
            );
        }}
@Transactional
public void deleteUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User non trouvé"));

    
    for (Entreprise entreprise : user.getEntreprises()) {
        entreprise.getUsers().remove(user);
    }

    
    userRepository.delete(user);
}
@Transactional
public void updateUserById(Long id, UserUpdateDTO dto) {
    System.out.println("Début de updateUserById");
System.out.println("DTO entreprises: " + dto.getEntreprises());

  try{
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
System.out.println("User existant: " + user.getEmail());
    if (dto.getName() != null && !dto.getName().isBlank()) {
        user.setName(dto.getName());
    }

    if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
        user.setEmail(dto.getEmail());
    }

    if (dto.getRole() != null && !dto.getRole().isBlank()) {
        try {
            user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rôle invalide : " + dto.getRole());
        }
    }

    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
        
         String encoded = passwordEncoder.encode(dto.getPassword());
    user.setPassword(encoded);
    }

    if (dto.getEntreprises() != null) {
        List<Long> entrepriseIds = dto.getEntreprises().stream()
            .map(EntrepriseIdDTO::getId)
            .toList();
    System.out.println(entrepriseIds);
        List<Entreprise> entreprises = entrepriseRepository.findAllById(entrepriseIds);

        user.getEntreprises().forEach(e -> e.getUsers().remove(user));
        user.getEntreprises().clear();

        for (Entreprise entreprise : entreprises) {
            user.getEntreprises().add(entreprise);
            entreprise.getUsers().add(user);
        }
        userRepository.save(user); 

    }
    } catch (Exception e) {
    e.printStackTrace(); 
    throw new RuntimeException("Erreur dans updateUserById: " + e.getMessage());
}
}

public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
    throw new RuntimeException("Aucun utilisateur authentifié");
}
String email = authentication.getName();
System.out.println("Email récupéré du token : " + email);
   

     return userRepository.findByEmailWithEntreprises(email)
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
}



public User getUserById(Long id) {
    try {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User non trouvée avec l'id : " + id));
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Erreur lors du fetch du user : " + e.getMessage());
    }
}
    
}

