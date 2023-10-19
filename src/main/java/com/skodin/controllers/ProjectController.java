package com.skodin.controllers;

import com.skodin.DTO.ErrorDTO;
import com.skodin.DTO.ProjectDTO;
import com.skodin.exceptions.BagRequestException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import com.skodin.util.ModelMapper;
import com.skodin.validators.ProjectValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    ProjectService projectService;
    ProjectValidator projectValidator;

    public static final String CREATE_PROJECT = "";


    /**
     * Передается пустой project после создания
     */
    @SneakyThrows
    @PostMapping(CREATE_PROJECT)
    public ResponseEntity<ProjectDTO> createProject(
            @Valid @RequestBody ProjectDTO projectDTO,
            BindingResult bindingResult
    ){

        if (projectDTO.getId() != null){
            throw new BagRequestException("New project cannot has an id");
        }

        ProjectEntity project = ModelMapper.getProject(projectDTO);

        projectValidator.validate(project, bindingResult);
        checkBindingResult(bindingResult);
        
        addStates(project);

        ProjectEntity projectEntity = projectService.saveAndFlush(project);

        ProjectDTO projectDTO1 = ModelMapper.getProjectDTO(projectEntity);

        return ResponseEntity
                .created(new URI("/api/projects/" + projectDTO1.getId()))
                .body(projectDTO1);
    }

    private void addStates(ProjectEntity project){
        project.addProjectEntities(
                TaskStateEntity.builder().name("state1").order(0).build(),
                TaskStateEntity.builder().name("state2").order(1).build(),
                TaskStateEntity.builder().name("state3").order(2).build());
    }
    
    private void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (var error: allErrors) {
                if (Objects.equals(error.getCode(), "400")){
                    throw new BagRequestException(error.getDefaultMessage());
                }
            }
        }
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> handleException(ConstraintViolationException e){

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

