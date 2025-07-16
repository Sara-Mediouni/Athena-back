package com.example.demo.jwt;
import com.example.demo.enums.Role;

import lombok.Data;
@Data
public class SignupDTO {
     private String name;

	 private String usernameOrEmail;
	 private String password;
     private Role role;
	    
	    
	    
}
