package com.skodin.services;

import com.skodin.exceptions.BadRequestException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.util.auth.AuthenticationRequest;
import com.skodin.util.auth.AuthenticationResponse;
import com.skodin.util.auth.RegisterRequest;
import com.skodin.models.Role;
import com.skodin.models.UserEntity;
import com.skodin.util.mail.MailSandler;
import com.skodin.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserValidator userValidator;
    private final AuthenticationManager authenticationManager;
    private final MailSandler mailSandler;

    @Value("${enable.link}")
    private String enableLink;

    public AuthenticationResponse register(RegisterRequest request, BindingResult bindingResult) {
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .activationCode(UUID.randomUUID().toString())
                .build();

        userValidator.validate(user, bindingResult);

        checkBindingResult(bindingResult);

        UserEntity entity = userService.saveAndFlush(user);

        String accessToken = jwtService.generateAccessToken(entity);
        String refreshToken = jwtService.generateRefreshToken(entity);

        String link = enableLink + user.getActivationCode();

        mailSandler.sendSimpleMessage(request.getEmail(), "Confirmation", link);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword())
            );

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

    private void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (var error : allErrors) {
                if (Objects.equals(error.getCode(), "400")) {
                    throw new BadRequestException(error.getDefaultMessage());
                }
            }
        }
    }

    public Boolean enable(String code) {

        if (code == null){
            throw new BadRequestException("code cannot be null");
        }

        Optional<UserEntity> user = userService.findByActivationCode(code);

        user.ifPresent(userEntity -> {
            userEntity.setActivationCode(null);
            UserEntity user1 = user.get();
            userService.update(user1.getId(), user1);
        });

        return user.isPresent();
    }
}
