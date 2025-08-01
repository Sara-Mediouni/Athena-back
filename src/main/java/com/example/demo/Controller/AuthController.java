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
        

        // Créer une réponse d'erreur détaillée
        ErrorResponse errorResponse = new ErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Si la validation passe, continuer avec la logique de login
    JwtAuthReponse jwtAuthReponse = authService.login(loginDto);

    // Retournons un ResponseEntity avec le bon type
    return new ResponseEntity<JwtAuthReponse>(jwtAuthReponse, HttpStatus.OK);  // Typé explicitement
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
        // Récupérer le refresh token depuis la requête
        String refreshToken = body.get("refreshToken");

        // Vérification de la présence du refresh token
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Mauvaise requête si absent
        }

        // Rafraîchir le token via le service
        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            // Créer une nouvelle réponse avec le token
            JwtAuthReponse response = new JwtAuthReponse(newAccessToken, refreshToken, "Bearer", null, null);
            return ResponseEntity.ok(response);  // Retourner le nouveau token
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // Si problème avec le token, retour Unauthorized
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
