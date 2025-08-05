package com.example.demo.Secuirty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.demo.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.jwt.JwtAuthenticationFilter;

import lombok.AllArgsConstructor;




@EnableMethodSecurity
@AllArgsConstructor
@Configuration
public class SpringSecuirtyConfig {
	
	
	
	
	 @Autowired
		private UserDetailsService userDetailsService;
		
		 @Autowired
		private JwtAuthenticationEntryPoint authenticationEntryPoint;
		 
		 @Autowired
		 private JwtAuthenticationFilter authenticationFilter;
		 
		 
		 @Bean
		    public static PasswordEncoder passwordEncoder(){
		        return new BCryptPasswordEncoder();
		    }
		 
		 
		 @Bean
		    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			 
			 
			 http.csrf(csrf -> csrf.disable())
		        .cors(cors -> cors.configurationSource(request -> {
		            CorsConfiguration config = new CorsConfiguration();
		            config.setAllowCredentials(true);
                    config.addAllowedOriginPattern("*");  
		            config.addAllowedHeader("*"); 
		            config.addAllowedMethod("GET");
		            config.addAllowedMethod("POST");
		            config.addAllowedMethod("PUT");
		            config.addAllowedMethod("DELETE");
		            config.addAllowedMethod("OPTIONS"); 
		            return config;
		        }))
			 
			 
	         .authorizeHttpRequests((authorize) -> {
				   
    authorize.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll(); 

            authorize.requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN","SUPER_ADMIN");
            authorize.requestMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN","SUPER_ADMIN");
            authorize.requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN","SUPER_ADMIN");
          //  authorize.requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "USER","SUPER_ADMIN");
            authorize.requestMatchers(HttpMethod.PATCH, "/api/**").hasAnyRole("ADMIN", "USER","SUPER_ADMIN");
            authorize.requestMatchers(HttpMethod.GET, "/api/**").permitAll();
			            authorize.requestMatchers(HttpMethod.GET, "/api/**/all").hasRole("SUPER_ADMIN");

	             authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
	             
	             authorize.anyRequest().authenticated();
	         }).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

			 
			 http.exceptionHandling( exception -> exception
		                .authenticationEntryPoint(authenticationEntryPoint));

		        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		        return http.build();
			 
		 }
		 
		 
		 
		 @Bean
		    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		        return configuration.getAuthenticationManager();
		
		

	}

}
