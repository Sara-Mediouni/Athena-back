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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
     public ResponseEntity<JwtAuthReponse> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        JwtAuthReponse authResponse = authService.login(loginDto, response);
        return ResponseEntity.ok(authResponse);
    }
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthReponse> signup(@RequestBody SignupDTO signupDTO, HttpServletResponse response) {
        JwtAuthReponse authResponse = authService.signup(signupDTO, response);
        return ResponseEntity.ok(authResponse);
    } 
   @PostMapping("/refresh")
public ResponseEntity<JwtAuthReponse> refreshToken(@RequestBody Map<String, String> body) {
    String refreshToken = body.get("refreshToken");

    if (refreshToken == null || refreshToken.trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
    }

    try {
        String newAccessToken = authService.refreshAccessToken(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("token", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Strict")
                .build();

        JwtAuthReponse response = new JwtAuthReponse(); // avec constructeur vide
        response.setAccessToken(newAccessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        // Supprime le cookie en le renvoyant avec maxAge=0
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        // Invalide session si existante
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Déconnexion réussie");
    }
}
