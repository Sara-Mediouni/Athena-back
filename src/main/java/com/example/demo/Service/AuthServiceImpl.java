package com.example.demo.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.demo.jwt.SignupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Dao.UserRepository;
import com.example.demo.Entite.RefreshToken;
import com.example.demo.Entite.User;
import com.example.demo.Secuirty.UserPrincipal;
import com.example.demo.enums.Role;
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

public JwtAuthReponse login(LoginDto loginDto) {
    try {
        System.out.println("Tentative de login : " + loginDto.getUsernameOrEmail());

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            System.out.println("User non trouvé");
        }

        User user = userOpt.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        boolean matches = passwordEncoder.matches(loginDto.getPassword(), user.getPassword());
        System.out.println("Mot de passe en base: [" + user.getPassword() + "]");
        System.out.println("Email: " + loginDto.getUsernameOrEmail());
System.out.println("Correspondance des mots de passe ? " + matches);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()
            )
        );
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication,principal);
        RefreshToken refreshToken = createRefreshToken();

        Role role = authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> {
                    String roleName = grantedAuthority.getAuthority().replace("ROLE_", "");
                    return Role.valueOf(roleName);
                })
                .orElse(Role.USER);

        return new JwtAuthReponse(accessToken, refreshToken.getToken(), "Bearer", role, user);

    } catch (Exception e) {
        // Log complet pour trouver l'erreur exacte
        e.printStackTrace();
        throw new RuntimeException("Erreur pendant le login : " + e.getMessage());
    }
}

@Override
public JwtAuthReponse signup(SignupDTO signupDto) {
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
        signupDto.getPassword()
    )
);
UserPrincipal principal = new UserPrincipal(user);
SecurityContextHolder.getContext().setAuthentication(authentication);
    String accessToken = jwtTokenProvider.generateToken(authentication,principal);
    RefreshToken refreshToken = createRefreshToken();
    Role role = signupDto.getRole();

    return new JwtAuthReponse(accessToken, refreshToken.getToken(), "Bearer", role, user);
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

    // Extraire le username ou userId à partir du refreshToken
    String username = getUsernameFromRefreshToken(refreshToken);

    // Recherche de l'utilisateur dans la base de données
    User user = userRepository.findByEmail(username);
    if (user == null) {
        throw new RuntimeException("User not found");
    }

    // Créer un objet UserPrincipal
    UserPrincipal userPrincipal = new UserPrincipal(user);

    // Créer l'objet Authentication pour l'utilisateur
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userPrincipal,
        null, // Le mot de passe est nul ici car ce n'est pas nécessaire pour le refresh token
        userPrincipal.getAuthorities() // Récupérer les autorités de l'utilisateur
    );

    // Générer un nouveau jeton d'accès en utilisant le UserPrincipal
    return jwtTokenProvider.generateToken(authentication, userPrincipal);
}

// Vérification si le token est valide ou expiré
private boolean isValid(String refreshToken) {
    // Exemple de validation avec jwtTokenProvider
    return jwtTokenProvider.validateToken(refreshToken); 
}

// Extraire le username ou userId à partir du refreshToken
private String getUsernameFromRefreshToken(String refreshToken) {
    // Exemple de récupération du username à partir du refreshToken
    return jwtTokenProvider.getName(refreshToken); 
}


}
