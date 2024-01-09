package com.skodin.controllers;

import com.skodin.services.AuthenticationService;
import com.skodin.util.auth.AuthenticationRequest;
import com.skodin.util.auth.AuthenticationResponse;
import com.skodin.util.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Authentication operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController extends MainController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Register new user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "empty JSON RegisterRequest",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ) {
        return ResponseEntity.ok(authenticationService.register(request, bindingResult));
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

    @PostMapping("/enable/{code}")
    public ResponseEntity<Boolean> enable(
            @PathVariable String code) {
        // TODO move the code to the header
        return ResponseEntity.ok(authenticationService.enable(code));
    }

}

