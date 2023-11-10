package com.skodin.controllers;

import com.skodin.DTO.TaskStateDTO;
import com.skodin.exceptions.BadRequestException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.TaskStateService;
import com.skodin.util.ModelMapper;
import com.skodin.validators.TaskStateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * проверка на существование проекта с id
 * происходит при конвертации из DTO
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/projects/{project_id}/task-states")
public class TaskStateController extends MainController {

    ProjectService projectService;
    TaskStateService taskStateService;
    TaskStateValidator taskStateValidator;
    ModelMapper modelMapper;

    public static final String GET_ALL_TASK_STATES = "/get";
    public static final String CREATE_TASK_STATE = "/create";
    public static final String GET_TASK_STATE_BY_ID = "/get/{task-state_id}";
    public static final String UPDATE_TASK_STATE_BY_ID = "/update/{task-state_id}";
    public static final String DELETE_TASK_STATE_BY_ID = "/delete/{task-state_id}";

    @GetMapping(GET_ALL_TASK_STATES)
    @Operation(
            summary = "Get all Task States for project by project id",
            description = "Returns all Task States for project",
            parameters = {
                    @Parameter(
                            name = "project_id",
                            description = "project id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation")
            }
    )
    @PreAuthorize("@projectSecurityExpression.checkUserProjectAccess(#id)")
    public ResponseEntity<List<TaskStateDTO>> getAllTaskStates(@PathVariable("project_id") Long id) {

        List<TaskStateEntity> taskStateEntities = projectService.findById(id).getTaskStateEntities();

        return ResponseEntity
                .ok()
                .body(taskStateEntities.stream()
                        .map(modelMapper::getTaskStateDTO).collect(Collectors.toList()));

    }

    @GetMapping(GET_TASK_STATE_BY_ID)
    @Operation(
            summary = "Get Task State for project by project id and task state id",
            description = "Returns Task State for project",
            parameters = {
                    @Parameter(
                            name = "project_id",
                            description = "project id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long")),
                    @Parameter(
                            name = "task-state_id",
                            description = "task state id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PreAuthorize("@projectSecurityExpression.checkUserProjectAccess(#projectId)")
    public ResponseEntity<TaskStateDTO> getTaskState(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-state_id") Long taskStateId) {

        TaskStateEntity taskStateEntity = taskStateService.findById(taskStateId);

        taskStateInProjectOrThrowEx(taskStateId, projectId, taskStateEntity);

        return ResponseEntity
                .ok()
                .body(modelMapper.getTaskStateDTO(taskStateEntity));
    }

    @SneakyThrows
    @PostMapping(CREATE_TASK_STATE)
    @Operation(
            summary = "Create Task State for project by project id and task state id",
            description = "Returns new Task State",
            parameters = {
                    @Parameter(
                            name = "project_id",
                            description = "project id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long")),
                    @Parameter(
                            name = "task-state_id",
                            description = "task state id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long"))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "empty JSON taskStateDTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskStateDTO.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
    @PreAuthorize("@projectSecurityExpression.checkUserProjectAccess(#id)")
    public ResponseEntity<TaskStateDTO> createTaskState(
            @RequestBody TaskStateDTO taskStateDTO,
            BindingResult bindingResult,
            @PathVariable("project_id") Long id) {

        if (taskStateDTO.getId() != null) {
            throw new BadRequestException("New Task State cannot has an id");
        }

        if (!taskStateDTO.getTaskEntities().isEmpty()) {
            throw new BadRequestException("New Task State cannot has any tasks");
        }

        taskStateDTO.setProjectId(id);

        TaskStateEntity taskState = modelMapper.getTaskState(taskStateDTO);

        taskStateValidator.validate(taskState, bindingResult);
        checkBindingResult(bindingResult);

        TaskStateEntity taskStateEntity = taskStateService.saveAndFlush(taskState);

        TaskStateDTO taskStateDTO1 = modelMapper.getTaskStateDTO(taskStateEntity);

        return ResponseEntity
                .created(new URI(String.format("/api/projects/%d/task-states/%d",
                        id, taskStateEntity.getId())))
                .body(taskStateDTO1);

    }

    @PatchMapping(UPDATE_TASK_STATE_BY_ID)
    @Operation(
            summary = "Update Task State for project by project id and task state id",
            description = "Returns updated Task State with Tasks. Updates all fields, but if you " +
                    "want to update order, you should change only previous element" +
                    "For adding new Task see TaskController",
            parameters = {
                    @Parameter(
                            name = "project_id",
                            description = "project id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long")),
                    @Parameter(
                            name = "task-state_id",
                            description = "task state id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long"))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JSON taskStateDTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskStateDTO.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
    @PreAuthorize("@projectSecurityExpression.checkUserProjectAccess(#projectId)")
    public ResponseEntity<TaskStateDTO> updateProject(
            @RequestBody TaskStateDTO taskStateDTO,
            BindingResult bindingResult,
            @PathVariable("task-state_id") Long taskStateId,
            @PathVariable("project_id") Long projectId){
        if (!Objects.equals(projectId, taskStateDTO.getProjectId())){
            throw new BadRequestException("You can not change project for Task States");
        }

        taskStateDTO.setId(taskStateId);
        TaskStateEntity taskStateFromHttp = modelMapper.getTaskState(taskStateDTO);

        taskStateInProjectOrThrowEx(taskStateId, projectId, taskStateFromHttp);

        taskStateValidator.validate(taskStateFromHttp, bindingResult);
        checkBindingResult(bindingResult);

        TaskStateEntity update = taskStateService.update(taskStateId, taskStateFromHttp);

        return ResponseEntity
                .ok()
                .body(modelMapper.getTaskStateDTO(update));
    }

    @DeleteMapping(DELETE_TASK_STATE_BY_ID)
    @Operation(
            summary = "Delete Task State for project by project id and task state id",
            description = "Delete Task State for project by project id and task state id",
            parameters = {
                    @Parameter(
                            name = "project_id",
                            description = "project id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long")),
                    @Parameter(
                            name = "task-state_id",
                            description = "task state id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PreAuthorize("@projectSecurityExpression.checkUserProjectAccess(#projectId)")
    public ResponseEntity<HttpStatus> deleteTasStateById(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-state_id") Long taskStateId){
        TaskStateEntity byId = taskStateService.findById(taskStateId);
        taskStateInProjectOrThrowEx(taskStateId, projectId, byId);
        taskStateService.deleteById(taskStateId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void taskStateInProjectOrThrowEx(Long taskStateId, Long projectId, TaskStateEntity taskState) {
        if (!Objects.equals(projectId, taskState.getProject().getId())){
            throw new NotFoundException(String.format(
                    "There is no Task State with id %d in Project with id %d", taskStateId, projectId));
        }
    }
}

