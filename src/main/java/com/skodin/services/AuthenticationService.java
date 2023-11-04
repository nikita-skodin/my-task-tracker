package com.skodin.services;

import com.skodin.util.auth.AuthenticationRequest;
import com.skodin.util.auth.AuthenticationResponse;
import com.skodin.util.auth.RegisterRequest;
import com.skodin.models.Role;
import com.skodin.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    protected final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; //?

    public AuthenticationResponse register(RegisterRequest request) {
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .build();

        UserEntity entity = userService.saveAndFlush(user);

        String token = jwtService.generateToken(entity);

        return new AuthenticationResponse(token);

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        UserEntity user = userService.findByUsername(request.getUsername());

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }
}
