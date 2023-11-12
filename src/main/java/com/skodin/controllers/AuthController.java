package com.skodin.controllers;

import com.skodin.util.auth.AuthenticationRequest;
import com.skodin.util.auth.AuthenticationResponse;
import com.skodin.util.auth.RegisterRequest;
import com.skodin.services.AuthenticationService;
import com.skodin.util.mail.MailSandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController extends MainController {

    private final AuthenticationService authenticationService;
    private final MailSandler mailSandler;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ){

        String link = "localhost:8080/api/auth/refresh";

        mailSandler.sendSimpleMessage(request.getEmail(), "Confirmation", link);

        return ResponseEntity.ok(authenticationService.register(request, bindingResult));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody String refreshToken
    ){
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }

}

