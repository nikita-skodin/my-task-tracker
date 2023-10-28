package com.skodin.controllers;

import com.skodin.DTO.ErrorDTO;
import com.skodin.exceptions.BadRequestException;
import com.skodin.models.TaskStateEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class MainController {
    protected void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (var error: allErrors) {
                if (Objects.equals(error.getCode(), "400")){
                    throw new BadRequestException(error.getDefaultMessage());
                }
            }
        }
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorDTO> handleException(ConstraintViolationException e){

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        StringBuilder response = new StringBuilder();

        for (var el : constraintViolations) {
            response.append(el.getMessage()).append("; ");
        }

        return ResponseEntity
                .status(400)
                .body(new ErrorDTO("BAD_REQUEST", response.toString()));

    }

    public void linksTaskStates(TaskStateEntity state1, TaskStateEntity state2){

        if(state1 == null && state2 == null){
            throw new IllegalArgumentException("Both TaskStates cannot be null");
        } else if (state1 == null){
            state2.setPreviousTaskState(null);
        } else if (state2 == null) {
            state1.setNextTaskState(null);
        } else {
            state1.setNextTaskState(state2);
            state2.setPreviousTaskState(state1);
        }
    }

}
