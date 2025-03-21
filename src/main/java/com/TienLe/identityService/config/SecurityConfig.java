package com.TienLe.identityService.config;

import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import com.TienLe.identityService.enums.Roles;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final String[] PUBPIC_API = {"/users","/auth/token","/auth/introspect","/auth/logout","/auth/refresh"};

	@Autowired
	private CustomJwtDecoder customJwtDecoder;
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

    	httpSecurity.authorizeHttpRequests(request ->
    		request.requestMatchers(HttpMethod.POST,PUBPIC_API).permitAll()
    				.requestMatchers(HttpMethod.GET, "/users").hasRole(Roles.ADMIN.name())
    				.anyRequest().authenticated());
    	
    	httpSecurity.oauth2ResourceServer( oauth2configure -> 
    		oauth2configure.jwt(jwtconfigure -> jwtconfigure.decoder(customJwtDecoder)
    											.jwtAuthenticationConverter(jwtAuthenticationConverter())
    				)
    			);
    	
    	httpSecurity.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:3000"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
            config.setAllowedHeaders(List.of("*"));
            return config;
        }));
    	httpSecurity.csrf(crfsconfigure -> crfsconfigure.disable());
        return httpSecurity.build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
    	
    	JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    	jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
    	
    	JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    	jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    	return jwtAuthenticationConverter;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
}
