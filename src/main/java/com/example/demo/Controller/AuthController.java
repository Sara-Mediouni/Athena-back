package com.example.demo.Controller;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dao.UserRepository;
import com.example.demo.Entite.User;
import com.example.demo.Service.AuthService;
import com.example.demo.errorshandler.ErrorResponse;
import com.example.demo.errorshandler.UserAlreadyExistsException;
import com.example.demo.jwt.JwtAuthReponse;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.jwt.LoginDto;
import com.example.demo.jwt.SignupDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	
	
	
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	 JwtTokenProvider jwtTokenProvider ;
	
	@Autowired
	private UserRepository userRepository ;
	
	
	
@PostMapping("/login")
    

public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, BindingResult result) {

    if (result.hasErrors()) {
        

       
        ErrorResponse errorResponse = new ErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    
    JwtAuthReponse jwtAuthReponse = authService.login(loginDto);

    return new ResponseEntity<JwtAuthReponse>(jwtAuthReponse, HttpStatus.OK);  
}
@PostMapping("/signup")
public ResponseEntity<?> signup(@RequestBody @Valid SignupDTO signupDto, BindingResult result) {
    System.out.println("signup");
     Optional<User> existingUser=userRepository.findByUsernameOrEmail(signupDto.getUsernameOrEmail());
    if (existingUser.isPresent()) {
        throw new UserAlreadyExistsException("L'utilisateur existe déjà avec cet email ou nom d'utilisateur.");
    }
    if (result.hasErrors()) {
        String errorMessage = result.getAllErrors().stream()
            .map(ObjectError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    JwtAuthReponse jwtAuthReponse = authService.signup(signupDto);
    return new ResponseEntity<>(jwtAuthReponse, HttpStatus.OK);
}


  @PostMapping("/refresh")
    public ResponseEntity<JwtAuthReponse> refreshToken(@RequestBody Map<String, String> body) {
        
        String refreshToken = body.get("refreshToken");

       
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); 
        }

 
        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            
            JwtAuthReponse response = new JwtAuthReponse(newAccessToken, refreshToken, "Bearer", null, null);
            return ResponseEntity.ok(response);   
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);   
        }
    }



@PostMapping("/logout")
public ResponseEntity<String> logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
        session.invalidate();
    }
    return ResponseEntity.ok("Déconnexion réussie");
}
		



	
	

}
