package com.example.demo.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Dao.EntrpriseRepository;
import com.example.demo.Dao.UserRepository;
import com.example.demo.Entite.Entreprise;
import com.example.demo.Entite.User;
import com.example.demo.Secuirty.UserPrincipal;
import com.example.demo.Service.UserService;

import com.example.demo.dto.EntrepriseIdDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.dto.userDTO;
import com.example.demo.enums.Role;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
   private UserService userService;
   private UserRepository userRepo;
   private EntrpriseRepository ent;
      @Autowired
private PasswordEncoder passwordEncoder;
         private static final Logger logger = LoggerFactory.getLogger(UserService.class);


   public UserController(UserService userservice , UserRepository userRep,EntrpriseRepository ent,PasswordEncoder passwordEncoder){
    this.userService=userservice;
    this.userRepo=userRep;
      this.passwordEncoder = passwordEncoder;
    this.ent=ent;
   }
    

   
@PostMapping("/add")
public User addUser(@RequestBody @Valid userDTO userRequest) {
    try {
        if (userRepo.existsByEmail(userRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un utilisateur avec cet email existe déjà.");
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        try {
            user.setRole(Role.valueOf(userRequest.getRole()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle invalide : " + userRequest.getRole());
        }
       List<Long> entrepriseIds = userRequest.getEntreprises()
                                  .stream()
                                  .map(EntrepriseIdDTO::getId)
                                  .collect(Collectors.toList());


 List<Entreprise> entreprises = ent.findAllById(entrepriseIds);

 if (entreprises.size() != entrepriseIds.size()) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une ou plusieurs entreprises sont introuvables.");
}

 user.setEntreprises(entreprises);


        return userRepo.save(user);

    } catch (ResponseStatusException e) {
        throw e;
    } catch (Exception e) {
        logger.error("Erreur lors de l'ajout de l'utilisateur", e);
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erreur lors de l'ajout de l'utilisateur : " + e.getMessage(),
            e
        );
    }
}

@GetMapping("/all")
public ResponseEntity<List<userDTO>> getAll() {
    try {
        List<User> users = userRepo.findAll();
        List<userDTO> usersDTOs = users.stream()
            .map(userDTO::new)
            .toList();
        return ResponseEntity.ok(usersDTOs);
    } catch (Exception e) {
       
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
@DeleteMapping("/delete/{id}")
public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    userService.deleteUserById(id);
    return ResponseEntity.noContent().build(); 
}

@PutMapping("/update/{id}")
public ResponseEntity<Void> updateUserById(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto) {
    try {
        userService.updateUserById(id, dto);
        return ResponseEntity.ok().build();
    } catch (ResponseStatusException e) {
        throw e;
    } catch (Exception e) {
        logger.error("Erreur lors de la mise à jour de l'utilisateur", e);
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage(),
            e
        );
    }
}

@GetMapping("/getbyid/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(user);
}
@GetMapping("/by-entreprise")
public ResponseEntity<List<userDTO>> getUsersByMyEntreprise() {
    User currentUser = userService.getCurrentUser();  

    List<User> users = userRepo.findByEntreprisesIn(currentUser.getEntreprises());  
    List<userDTO> userDtos = users.stream()
        .map(userDTO::new)
        .collect(Collectors.toList());

    return ResponseEntity.ok(userDtos);
}




@Transactional
@GetMapping("/users-custom")
public ResponseEntity<List<userDTO>> getUsersByRole(@AuthenticationPrincipal UserPrincipal currentUser) {
    if (currentUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
     

     User userconnected = userRepo.findById(currentUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));

    List<User> users;

    if (currentUser.hasRole("SUPER_ADMIN")) {
        users = userRepo.findAll();
    } else if (currentUser.hasRole("ADMIN")) {
        User currentUser1 = userRepo.findByIdWithEntreprises(currentUser.getId())
                            .orElseThrow(() -> new RuntimeException("User not found"));
        users = userRepo.findByEntreprisesIn(currentUser1.getEntreprises());
    } else {
        users = List.of(userconnected);
        System.out.println(userconnected);
    }

     users.forEach(user -> user.getEntreprises().forEach(e -> e.getId()));  

     List<userDTO> dtos = users.stream()
        .map(userDTO::new)
        .toList();

    return ResponseEntity.ok(dtos);
}

   @GetMapping("/me")
    public Map<String, Object> getCurrentUser() {
         User currentUser = userService.getCurrentUser();  

        return Map.of(
            "username", currentUser.getName(),
            "role", currentUser.getRole()
        );
    }





}

