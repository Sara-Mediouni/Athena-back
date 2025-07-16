package com.example.demo.jwt;

import com.example.demo.Entite.User;
import com.example.demo.enums.Role;

import lombok.Data;
@Data
public class JwtAuthReponse {
	
	
	
	
	
	private String accessToken;
    private String tokenType = "Bearer";
    private String refreshToken;
    private Role role;
    private User user;
    
    
    
    
    
	 public JwtAuthReponse(String accessToken, String refreshToken, String tokenType, Role role, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.role = role;
        this.user = user;
    }





	
	
	
	
    
    
    
    

}
