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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Projects", description = "Operations related to projects")
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
    @Operation(
            summary = "Get all projects",
            description = "Returns all projects, possibly with Task States",
            parameters = {
                    @Parameter(
                            name = "prefix",
                            description = "project`s name prefix",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "String"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation")
            }
    )
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

    @Operation(
            summary = "Get project by id",
            description = "Returns project by id",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "project`s id",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "Long"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping(GET_PROJECT_BY_ID)
    @PreAuthorize("@projectSecurityExpression.checkUserProjectAccess(#id)")
    public ResponseEntity<ProjectDTO> getProjectById(
            @PathVariable Long id) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(modelMapper.getProjectDTO(projectService.findById(id)));
    }

    @Operation(
            summary = "Create new project",
            description = "Returns new project DTO with 3 empty Task States DTO",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "empty JSON projectDTO with name only",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDTO.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
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

    @Operation(
            summary = "Update project by id",
            description = """
                    Returns updated project with Task States. Updates only name.
                    For adding new Task States see TaskStateController.
                    """,
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "project`s id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "Long"))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "empty JSON projectDTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDTO.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
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

    @Operation(
            summary = "Delete project by id",
            description = "Delete project by id",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "project`s id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "Long"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
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

