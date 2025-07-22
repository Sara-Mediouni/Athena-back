package com.example.demo.API.dto;

import java.time.LocalDate;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.Data;

@Data
public class CAglobalDTO {
   
    private LocalDate Datedebut;
    private LocalDate Datefin; 
    private Double cattc;
    private Double caht;
    private Integer document;
    
    public CAglobalDTO(LocalDate datedebut, LocalDate datefin, Double cattc, Double caht,Integer document) {
        this.Datedebut = datedebut;
        this.Datefin = datefin;
        this.cattc = cattc;
        this.caht = caht;
        this.document = document;
    }
   

    // Getters & setters

}
