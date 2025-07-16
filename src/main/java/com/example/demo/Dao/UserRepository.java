package com.example.demo.Dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entite.Entreprise;
import com.example.demo.Entite.User;

public interface UserRepository extends JpaRepository<User, Long> {	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.entreprises WHERE u.id = :userId")
Optional<User> findByIdWithEntreprises(@Param("userId") Long userId);
	 List<User> findByEntreprisesIn(Collection<Entreprise> entreprises);
    

	@Query("SELECT u FROM User u WHERE u.email = :usernameOrEmail")
	Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
	
	 @Query("SELECT u FROM User u WHERE u.name = :name")
	    User findByName(@Param("name") String name);
	 
	 @Query("SELECT u.name FROM User u WHERE u.email = :email")
	    Optional<String> findNameByEmail(@Param("email") String email);
	  @Query("SELECT u FROM User u WHERE u.email = :email")
	    User findByEmail(@Param("email") String email);
	 
	 @EntityGraph(attributePaths = {"entreprises"})
@Query("SELECT u FROM User u")
List<User> findAllWithEntreprises();

@EntityGraph(attributePaths = "entreprises")
@Query("SELECT u FROM User u WHERE u.email = :email")
Optional<User> findByEmailWithEntreprises(@Param("email") String email);

	 boolean existsByName(String name);
		boolean existsByEmail(String email);

}
