package com.example.demo.Service;



import com.example.demo.Secuirty.UserPrincipal;
import com.example.demo.jwt.JwtAuthReponse;
import com.example.demo.jwt.LoginDto;
import com.example.demo.jwt.SignupDTO;

public interface AuthService {
	
	
	
	JwtAuthReponse signup(SignupDTO signupDTO);
	JwtAuthReponse login(LoginDto loginDto);
	
	String refreshAccessToken(String refreshToken);

}
