package com.example.demo.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dao.EntrpriseRepository;
import com.example.demo.Entite.Entreprise;
import com.example.demo.Entite.User;
import com.example.demo.Service.EntrepriseService;
import com.example.demo.Service.UserService;
import com.example.demo.dto.EntrepriseDTO;
import com.example.demo.dto.EntrepriseUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/api/ent")
public class EntrepriseController {
        private EntrpriseRepository entrepriseRepository;
        private EntrepriseService entrepriseService;
        private UserService userService;
        private static final Logger logger = LoggerFactory.getLogger(EntrepriseController.class);

public EntrepriseController(EntrepriseService entService,EntrpriseRepository entRepository,UserService userService)
{
    this.entrepriseService=entService;
    this.userService=userService;
    this.entrepriseRepository=entRepository;
}
  
 @PostMapping("/add")
public ResponseEntity<?> addEntreprise(@RequestBody EntrepriseDTO dto) {
    try {
        Entreprise newEnt = entrepriseService.addEntreprise(
            dto.getMatricule(), dto.getName(), dto.getLien(), dto.getAddress()
        );
        System.out.println(newEnt);
        return new ResponseEntity<>(newEnt, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>("Erreur : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
@DeleteMapping("/delete/{id}")

public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
    try {
        entrepriseService.deleteEntrepriseById(id);
        return ResponseEntity.noContent().build();  // 204 No Content, sans corps
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .build();
    }
}
@Transactional
@GetMapping("/all")
public ResponseEntity<List<EntrepriseDTO>> getAll() {
    try {
        System.out.println("get all");
        List<Entreprise> entreprises = entrepriseRepository.findAll();
        System.out.println("Nb entreprises : " + entreprises.size());


        // Convertir vers DTO
        List<EntrepriseDTO> entrepriseDTOs = entreprises.stream()
            .map(EntrepriseDTO::new)
            .toList();
        System.out.println(entrepriseDTOs);
        return ResponseEntity.ok(entrepriseDTOs);
    } catch (Exception e) {
        logger.error("Erreur lors de la récupération des entreprises", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/update/{id}")
public ResponseEntity<?> updateEntreprise(@PathVariable Long id, @RequestBody EntrepriseUpdateDTO dto) {
    try {
        entrepriseService.updateEntrepriseById(id, dto);
        return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
@GetMapping("/getbyid/{id}")
public ResponseEntity<Entreprise> getEntreprise(@PathVariable Long id) {
    Entreprise entreprise = entrepriseService.getEntrepriseById(id);
    return ResponseEntity.ok(entreprise);
}
@GetMapping("/me")

public ResponseEntity<?> getMyEntreprise() {
    User user = userService.getCurrentUser();


    List<EntrepriseDTO> dtos = user.getEntreprises().stream()
        .map(EntrepriseDTO::new)
        .collect(Collectors.toList());

    if (dtos.isEmpty()) {
        return ResponseEntity.noContent().build();
    } else {
        return ResponseEntity.ok(dtos);
    }
}





}
