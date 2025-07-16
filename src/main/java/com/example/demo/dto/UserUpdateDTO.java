
    package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateDTO{

    private String name;

    @Email(message = "L'email n'est pas valide.")
    private String email;

    private String role;

    private String password;

    private List<EntrepriseIdDTO> entreprises;
}


