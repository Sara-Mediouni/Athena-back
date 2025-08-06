package com.example.demo.jwt;

import com.example.demo.Entite.User;
import com.example.demo.enums.Role;

import lombok.Data;
@Data

public class JwtAuthReponse {
    private Role role;
    private User user;
    private String accessToken;

    public JwtAuthReponse(Role role, User user) {
        this.role = role;
        this.user = user;
    }
    public JwtAuthReponse() {
       
    }
    public JwtAuthReponse(String accessToken) {
    this.accessToken = accessToken;
}

}
