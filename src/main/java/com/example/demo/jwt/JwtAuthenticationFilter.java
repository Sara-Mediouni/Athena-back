package com.example.demo.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	
	private JwtTokenProvider jwtTokenProvider;
    private CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

private String getTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
            System.out.println("[JWT FILTER] Cookie trouvé: " + cookie.getName() + "=" + cookie.getValue());
            if ("accessToken".equals(cookie.getName())) {
                System.out.println("[JWT FILTER] ✅ Token extrait du cookie");
                return cookie.getValue();
            }
        }
    }
    System.out.println("[JWT FILTER] ❌ Aucun token trouvé dans les cookies");
    return null;
}

@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

    
    String token = getTokenFromCookie(request);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
        String username = jwtTokenProvider.getName(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    filterChain.doFilter(request, response);
}


	public String getJwtFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
    }
    return null;
}
	 
	 private String getTokenFromRequest(HttpServletRequest request){
	        String bearerToken = request.getHeader("Authorization");

	        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
	            return bearerToken.substring(7, bearerToken.length());
	        }

	        return null;
	    }

}
