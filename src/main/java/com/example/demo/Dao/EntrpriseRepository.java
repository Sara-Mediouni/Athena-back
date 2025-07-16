package com.example.demo.Dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entite.Entreprise;
@Repository
public interface EntrpriseRepository extends JpaRepository<Entreprise, Long> {
    boolean existsByMatricule(String matricule);
       Optional<Entreprise> findByName(String name);
}
