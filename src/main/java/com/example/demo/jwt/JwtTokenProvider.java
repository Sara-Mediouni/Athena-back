package com.example.demo.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.demo.Secuirty.UserPrincipal;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${app.jwt-secret}")
	private String jwtSecret;

	@Value("${app.jwt-expiration-milliseconds}")
	private long jwtExpirationDate;

	public String generateToken(Authentication authentication, UserPrincipal userPrincipal) {
		System.out.println("generate token");
		String username = authentication.getName();

		Set<String> roles = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());

		Date currentDate = new Date();

		Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

		String token = Jwts.builder()
				.subject(username)
				.claim("roles", roles)
				.claim("userId", userPrincipal.getId())
				.issuedAt(new Date())
				.expiration(expireDate)
				.signWith(key())
				.compact();

		return token;

	}

	public String generateRefreshToken(String username) {
		Date currentDate = new Date();
		long refreshTokenExpiration = jwtExpirationDate * 2; // Exemple : une expiration plus longue pour le
																// refreshToken
		Date expireDate = new Date(currentDate.getTime() + refreshTokenExpiration);

		return Jwts.builder()
				.subject(username)
				.issuedAt(currentDate)
				.expiration(expireDate)
				.signWith(key())
				.compact();
	}

	private Key key() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String getName(String token) {

		return Jwts.parser()
				.verifyWith((SecretKey) key())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			System.out.println("Validating token...");
			Jwts.parser()
					.verifyWith((SecretKey) key())
					.build()
					.parse(token);
			return true;
		} catch (Exception e) {
			System.out.println("Token validation failed: " + e.getMessage());
			return false;
		}
	}

}
