package com.example.demo.dto;

import lombok.Data;

@Data
public class EntrepriseIdDTO {
    private Long id;
    private String name;
    // getter/setter
    public EntrepriseIdDTO(Long id,String name){
        this.id=id;
        this.name=name;
        
    }
}