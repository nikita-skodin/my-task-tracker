package com.skodin.controllers;

import com.skodin.models.Role;
import com.skodin.models.UserEntity;
import com.skodin.services.AuthenticationService;
import com.skodin.util.auth.AuthenticationRequest;
import com.skodin.util.auth.AuthenticationResponse;
import com.skodin.util.auth.RegisterRequest;
import com.skodin.validators.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController extends MainController {

    private final UserValidator userValidator;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ) {
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(Role.USER)
                .activationCode(UUID.randomUUID().toString())
                .build();

        userValidator.validate(user, bindingResult);
        checkBindingResult(bindingResult);

        return ResponseEntity
                .created(URI.create("/api/users/get/" + user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.register(user));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody String refreshToken
    ) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }

    @GetMapping("/enable/{code}")
    public ResponseEntity<String> enable(
            @PathVariable String code) {
        boolean activationResult = authenticationService.enable(code);
        if (activationResult) {
            return ResponseEntity.ok("Account successfully activated");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation code");
        }
    }

}

