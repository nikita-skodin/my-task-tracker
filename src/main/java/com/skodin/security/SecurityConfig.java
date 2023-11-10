package com.skodin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.DTO.ErrorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> {
                            auth.requestMatchers("/api/auth/**")
                                    .permitAll()
                                    .anyRequest()
                                    .authenticated();
                        }
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(((request, response, authException) ->
                        {
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            ErrorDTO errorResponse = new ErrorDTO("UNAUTHORIZED", "Incorrect data");
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        }
                        ))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            ErrorDTO errorResponse = new ErrorDTO("FORBIDDEN", "Access Denied");
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        }))
                .sessionManagement(manager -> {
                    manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
