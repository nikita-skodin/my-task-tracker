package com.skodin.config.security;

import com.skodin.DTO.ErrorDTO;
import com.skodin.exceptions.InvalidToken;
import com.skodin.models.UserEntity;
import com.skodin.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        String jwt = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        if (jwt != null && jwt.startsWith("Bearer ")){
            jwt = jwt.substring(7);

            if (jwtService.isTokenValid(jwt, userDetailsService)){
                try{
                    String username = jwtService.extractUsername(jwt);

                    userDetailsService.loadUserByUsername(username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            ((UserEntity) userDetails).getId().toString(),
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                catch (Exception ignored){
                    System.err.println("ERROR IN FILTER");
                    System.err.println(ignored.getMessage());
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
