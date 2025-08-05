package com.example.demo.Secuirty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

import com.example.demo.Entite.Entreprise;
import com.example.demo.Entite.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
public class UserPrincipal implements UserDetails {

    
    private final User user;
    private final Long id;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
    private final List<Entreprise> entreprises;

    public UserPrincipal(User user) {
        this.user = user;
        this.id = user.getId();
        this.username = user.getEmail();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        this.entreprises = user.getEntreprises();
    }
    @Override
public String getUsername() {
    return user.getEmail();  }

 @JsonIgnore
    public List<Entreprise> getEntreprises() {
        return entreprises;
    }

@Override
public String getPassword() {
    return user.getPassword();
}
    public Long getId() {
        return id;
    }
      public User getUser() {
        return user;
    }
   

    public boolean hasRole(String role) {
        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authorities.stream().anyMatch(auth -> auth.getAuthority().equalsIgnoreCase(roleName));
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}