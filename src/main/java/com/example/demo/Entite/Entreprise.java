package com.example.demo.Entite;



import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "entreprise")
@JsonIgnoreProperties({"users"})
public class Entreprise {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;
    @Column(nullable = false, unique = true)
    private String name;
	@Column(nullable = false, unique = true)
	private String matricule;
	
	@Column(nullable = false, unique = true)
	private String address;
	 
	@Column(nullable = false, unique = true)
	    private String lien;
		
	 @ManyToMany(mappedBy = "entreprises")
	 @JsonIgnore
    private Set<User> users;

	
	
}
