package com.example.demo.Service;

import com.example.demo.jwt.JwtAuthReponse;
import com.example.demo.jwt.LoginDto;
import com.example.demo.jwt.SignupDTO;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	
	
	
	JwtAuthReponse signup(SignupDTO signupDTO, HttpServletResponse response);
	JwtAuthReponse login(LoginDto loginDto, HttpServletResponse response);
	
	String refreshAccessToken(String refreshToken);

}
