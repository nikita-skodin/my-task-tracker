package com.skodin.controllers;

import com.skodin.DTO.ErrorDTO;
import com.skodin.exceptions.BadRequestException;
import com.skodin.exceptions.ForbiddenException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.UserEntity;
import com.skodin.services.JwtService;
import com.skodin.services.ProjectService;
import com.skodin.services.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public abstract class MainController {
    protected void checkUsersRules(UserEntity user) {
        if (!user.getId().equals(UserService.getCurrentUser().getId())){
            throw new ForbiddenException("FORBIDDEN");
        }
    }

    protected void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (var error : allErrors) {
                if (Objects.equals(error.getCode(), "400")) {
                    throw new BadRequestException(error.getDefaultMessage());
                }
            }
        }
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorDTO> handleException(ConstraintViolationException e) {

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        StringBuilder response = new StringBuilder();

        for (var el : constraintViolations) {
            response.append(el.getMessage()).append("; ");
        }

        return ResponseEntity
                .status(400)
                .body(new ErrorDTO("BAD_REQUEST", response.toString()));

    }
}
