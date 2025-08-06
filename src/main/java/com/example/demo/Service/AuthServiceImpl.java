package com.example.demo.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import com.example.demo.jwt.SignupDTO;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Dao.UserRepository;
import com.example.demo.Entite.RefreshToken;
import com.example.demo.Entite.User;
import com.example.demo.Secuirty.UserPrincipal;
import com.example.demo.enums.Role;
import com.example.demo.errorshandler.InvalidCredentialsException;
import com.example.demo.jwt.JwtAuthReponse;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.jwt.LoginDto;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    AuthServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override

    public JwtAuthReponse login(LoginDto loginDto, HttpServletResponse response) {
        try {

            Optional<User> userOpt = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail());
            if (userOpt.isEmpty()) {
                System.out.println("User non trouvé");
            }

            User user = userOpt.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
            boolean matches = passwordEncoder.matches(loginDto.getPassword(), user.getPassword());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsernameOrEmail(),
                            loginDto.getPassword()));
            UserPrincipal principal = new UserPrincipal(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtTokenProvider.generateToken(authentication, principal);
            RefreshToken refreshToken = createRefreshToken();

            Role role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> {
                        String roleName = grantedAuthority.getAuthority().replace("ROLE_", "");
                        return Role.valueOf(roleName);
                    })
                    .orElse(Role.USER);
            ResponseCookie accessTokenCookie = ResponseCookie.from("token", accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .sameSite("Lax") 
                    .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(604800)
                    .sameSite("Lax") 
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            return new JwtAuthReponse(role, user);

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Identifiants invalides");
        } catch (UsernameNotFoundException e) {
            throw new InvalidCredentialsException("Utilisateur non trouvé");
        } catch (Exception e) {
            throw new RuntimeException("Erreur interne : " + e.getMessage());
        }
    }

    @Override
    public JwtAuthReponse signup(SignupDTO signupDto, HttpServletResponse response) {
        User user = new User();

        user.setName(signupDto.getName());
        user.setEmail(signupDto.getUsernameOrEmail());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        System.out.println("Mot de passe encodé : " + user.getPassword());

        user.setRole(signupDto.getRole());

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        signupDto.getPassword()));
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.generateToken(authentication, principal);
        RefreshToken refreshToken = createRefreshToken();
        ResponseCookie accessTokenCookie = ResponseCookie.from("token", accessToken)
                .httpOnly(true)
               .secure(false)
                .path("/")
                .maxAge(3600)
                .sameSite("Lax") 
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
              .secure(false)
                .path("/")
                .maxAge(604800)
                .sameSite("Lax") 
                .build();
        Role role = signupDto.getRole();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return new JwtAuthReponse(role, user);

    }

    private RefreshToken createRefreshToken() {
        String token = UUID.randomUUID().toString();
        long expirationDate = System.currentTimeMillis() + 86400000;
        return new RefreshToken(token, expirationDate);
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !isValid(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

     
        String username = getUsernameFromRefreshToken(refreshToken);

      
        User user = userRepository.findByEmail(username);
        if (user == null) {

            throw new InvalidCredentialsException("Utilisateur non trouvé");
        }

        UserPrincipal userPrincipal = new UserPrincipal(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities());

        return jwtTokenProvider.generateToken(authentication, userPrincipal);
    }

    private boolean isValid(String refreshToken) {
        return jwtTokenProvider.validateToken(refreshToken);
    }

    private String getUsernameFromRefreshToken(String refreshToken) {
        return jwtTokenProvider.getName(refreshToken);
    }

}
