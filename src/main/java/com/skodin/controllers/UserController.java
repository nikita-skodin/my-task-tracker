package com.skodin.controllers;

import com.skodin.exceptions.ForbiddenException;
import com.skodin.models.UserEntity;
import com.skodin.services.UserService;
import com.skodin.util.ModelMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.skodin.DTO.UserDTO;
import com.skodin.validators.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController extends MainController{

    public static final String GET_USERS = "/get";
    public static final String GET_USER_BY_ID = "/get/{id}";
    public static final String GET_USER_BY_TOKEN = "/get/me";
    public static final String UPDATE_USER_BY_ID = "/update/{id}";
    public static final String DELETE_USER_BY_ID = "/delete/{id}";

    UserService userService;
    UserValidator userValidator;
    ModelMapper modelMapper;

    @GetMapping(GET_USER_BY_TOKEN)
    public ResponseEntity<UserDTO> getUserByToken() {
        UserEntity user = UserService.getCurrentUser();
        return ResponseEntity
                .ok()
                .body(modelMapper
                        .getUserDTO(userService
                                .findById(user.getId())));
    }

    @GetMapping(GET_USERS)
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // возвращаем без проектов в целях безопасности
        List<UserEntity> users = userService.findAll();
        return ResponseEntity
                .ok()
                .body(users.stream()
                .map(user -> {
                    UserDTO userDTO = modelMapper.getUserDTO(user);
                    userDTO.setProjects(null);
                    return userDTO;
                })
                .collect(Collectors.toList()));
    }

    @GetMapping(GET_USER_BY_ID)
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        // возвращаем без проектов в целях безопасности
        UserEntity user = userService.findById(id);
        user.setProjects(null);
        return ResponseEntity
                .ok()
                .body(modelMapper.getUserDTO(user));
    }


    @PatchMapping(UPDATE_USER_BY_ID)
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO,
            BindingResult bindingResult) {
        // возвращаем без проектов в целях безопасностиc

        // TODO: 016 добавить поментку про токен и его рефреш
        UserEntity user = modelMapper.getUser(userDTO);
        user.setId(id);

        userValidator.validate(user, bindingResult);
        checkBindingResult(bindingResult);

        UserEntity updated = userService.update(id, user);
        UserDTO dto = modelMapper.getUserDTO(updated);
        dto.setProjects(null);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(DELETE_USER_BY_ID)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
