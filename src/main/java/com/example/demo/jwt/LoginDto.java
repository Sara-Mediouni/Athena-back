package com.example.demo.jwt;

import lombok.Data;

@Data 
public class LoginDto {

    private String usernameOrEmail;
    private String password;
}