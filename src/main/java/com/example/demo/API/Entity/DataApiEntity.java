package com.example.demo.API.Entity;


    import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@Entity
@Table(name = "factures") // le nom de ta table réelle ici
public class DataApiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String piece;
    private String pieceBC;
    private String pieceBL;
    @JsonProperty("Date")
 @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    @JsonProperty("DateBL")
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateBL;
    private LocalDate dateBC;
    

    private String refArticle;
    private String designation;

    private Double qte;
    private Double remise;
    private Double pu;

    private Double taxe1;
    private Double taxe2;
    private Double taxe3;
    @JsonProperty("HT")
    private Double ht;
    @JsonProperty("TTC")
    private Double ttc;

    private String ville;
    private String pays;

    private String gamme1;
    private String gamme2;
    @JsonProperty("CO_Nom")
    private String coNom;
    private String ctNum;
    private String ctIntitule;
    @JsonProperty("DE_Intitule")
    private String deIntitule;
    private String faIntitule;

    private Integer doType;

    // Getters & setters
     public LocalDate getDate() {
        return date != null ? date.toLocalDate() : null;
    }

    public LocalDate getDateBL() {
        return dateBL != null ? dateBL.toLocalDate() : null;
    }
    public Double getTtc() {
        return ttc;
    }
    public Double getHt() {
        return ht;
    }
    public String getDepot() {
        return deIntitule;
    }
    public String getCoNom() {
        return coNom;
    }
}

