package com.skodin.controllers;

import com.skodin.DTO.ProjectDTO;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.models.UserEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.TaskStateService;
import com.skodin.services.UserService;
import com.skodin.util.ModelMapper;
import com.skodin.validators.ProjectValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController extends MainController {

    ModelMapper modelMapper;
    ProjectService projectService;
    ProjectValidator projectValidator;
    TaskStateService taskStateService;

    public static final String GET_PROJECTS = "/get";
    public static final String CREATE_PROJECT = "/create";
    public static final String GET_PROJECT_BY_ID = "/get/{id}";
    public static final String UPDATE_PROJECT_BY_ID = "/update/{id}";
    public static final String DELETE_PROJECT_BY_ID = "/delete/{id}";

    @GetMapping(GET_PROJECTS)
    public ResponseEntity<List<ProjectDTO>> getAllProjects(
            @RequestParam(required = false) Optional<String> prefix) {

        UserEntity user = UserService.getCurrentUser();
        List<ProjectEntity> projects;

        if (prefix.isPresent() && !prefix.get().isBlank()) {
            projects = projectService.findAllByNameStartingWithAndUser(prefix.get(), user);
        } else {
            projects = projectService.findAllByUser(user);
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(projects.stream().map(modelMapper::getProjectDTO).collect(Collectors.toList()));
    }


    @GetMapping(GET_PROJECT_BY_ID)
    @PreAuthorize("@projectSecurityExpression.checkUserProjectAccess(#id)")
    public ResponseEntity<ProjectDTO> getProjectById(
            @PathVariable Long id) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(modelMapper.getProjectDTO(projectService.findById(id)));
    }

    @PostMapping(CREATE_PROJECT)
    public ResponseEntity<ProjectDTO> createProject(
            @RequestBody ProjectDTO projectDTO,
            BindingResult bindingResult
    ) {

        ProjectEntity project = modelMapper.getProject(projectDTO);
        project.setUser(UserService.getCurrentUser());

        projectValidator.validate(project, bindingResult);
        checkBindingResult(bindingResult);

        ProjectEntity projectEntity = projectService.saveAndFlush(project);

        addStates(project);

        ProjectDTO projectDTO1 = modelMapper.getProjectDTO(projectEntity);

        return ResponseEntity
                .created(URI.create("/api/projects/" + projectDTO1.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(projectDTO1);
    }

    @PatchMapping(UPDATE_PROJECT_BY_ID)
    public ResponseEntity<ProjectDTO> updateProject(
            @RequestBody ProjectDTO projectDTO,
            @PathVariable Long id,
            BindingResult bindingResult) {

        ProjectEntity project = modelMapper.getProject(projectDTO);

        projectValidator.validate(project, bindingResult);
        checkBindingResult(bindingResult);

        ProjectEntity update = projectService.update(id, project);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(modelMapper.getProjectDTO(update));
    }

    @DeleteMapping(DELETE_PROJECT_BY_ID)
    public ResponseEntity<HttpStatus> deleteProjectById(@PathVariable Long id) {
        projectService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addStates(ProjectEntity project) {
        TaskStateEntity toDo = taskStateService.saveAndFlush(TaskStateEntity.builder()
                .project(project).name("To do").build());
        TaskStateEntity inProgress = taskStateService.saveAndFlush(TaskStateEntity.builder()
                .project(project).name("In progress").build());
        TaskStateEntity done = taskStateService.saveAndFlush(TaskStateEntity.builder()
                .project(project).name("Done").build());

        project.addProjectEntities(toDo, inProgress, done);
    }
}

