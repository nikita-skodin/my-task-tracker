package com.skodin.validators;

import com.skodin.models.UserEntity;
import com.skodin.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserEntity user = (UserEntity) target;

        Optional<UserEntity> byUsername = userService.findByUsername(user.getUsername());
        if (byUsername.isPresent()
        && !Objects.equals(byUsername.get().getId(), user.getId())) {
            errors.rejectValue("username", "400",
                    "User with username " + user.getUsername() + " is already exist");
        }

        Optional<UserEntity> byEmail = userService.findByEmail(user.getEmail());
        if (byEmail.isPresent()
                && !Objects.equals(byEmail.get().getId(), user.getId())) {
            errors.rejectValue("email", "400",
                    "User with email " + user.getEmail() + " is already exist");
        }

    }
}
