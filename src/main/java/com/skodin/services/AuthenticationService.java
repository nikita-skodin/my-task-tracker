package com.skodin.services;

import com.skodin.exceptions.AccountDisableException;
import com.skodin.exceptions.BadRequestException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.UserEntity;
import com.skodin.util.auth.AuthenticationRequest;
import com.skodin.util.auth.AuthenticationResponse;
import com.skodin.util.mail.MailSandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailSandler mailSandler;

    @Value("${enable.link}")
    private String enableLink;

    public AuthenticationResponse register(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserEntity entity = userService.saveAndFlush(user);

        String accessToken = jwtService.generateAccessToken(entity);
        String refreshToken = jwtService.generateRefreshToken(entity);

        String link = enableLink + user.getActivationCode();

        mailSandler.sendActivationCodeMessage(user.getEmail(), "Confirm your email", link, user.getUsername());

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())
            );
        } catch (AuthenticationException e) {
            if (e instanceof DisabledException) {
                throw new AccountDisableException("Account is disable");
            }
            throw e;
        }

        UserEntity user = userService
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with username %s not found", request.getUsername())));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse refresh(String refreshToken) {
        return jwtService.refreshUserToken(refreshToken);
    }

    public Boolean enable(String code) {

        if (code == null) {
            throw new BadRequestException("code cannot be null");
        }

        Optional<UserEntity> user = userService.findByActivationCode(code);

        user.ifPresent(userEntity -> userService.updateEnable(user.get()));

        return user.isPresent();
    }
}
