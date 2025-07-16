package com.example.demo.Entite;

import java.util.List;
import java.util.Set;

import com.example.demo.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;




@Entity
@Data
@Table (name = "user")
public class User {
	
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;
	 @Column(nullable = false, unique = true)
	private String name;
	@Enumerated(EnumType.STRING)
	private Role role;
	
	
	 @Column(nullable = false, unique = true)
	    private String email;
	 
	 @Column(nullable = false)
	    private String password;

   @ManyToMany(fetch = FetchType.LAZY)
   
   @JoinTable(
        name = "user_entreprise",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "entreprise_id")
    )
	
    public  List<Entreprise> entreprises;
	
    public void setRole(Role role) {
        this.role = role;
    }
	
		

}
