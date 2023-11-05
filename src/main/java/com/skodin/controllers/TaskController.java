package com.skodin.controllers;

import com.skodin.DTO.TaskDTO;
import com.skodin.exceptions.BadRequestException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.TaskService;
import com.skodin.services.TaskStateService;
import com.skodin.util.ModelMapper;
import com.skodin.util.ProjectTaskStateTuple;
import com.skodin.validators.TaskValidator;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/projects/{project_id}/task-states/{task-states_id}/tasks")
public class TaskController extends MainController {

    TaskService taskService;
    ProjectService projectService;
    TaskStateService taskStateService;
    ModelMapper modelMapper;

    TaskValidator taskValidator;

    public static final String GET_ALL_TASKS = "/get";
    public static final String ADD_NEW_TASK = "/create";
    public static final String GET_TASK_BY_ID = "/get/{task_id}";
    public static final String UPDATE_TASK_BY_ID = "/update/{task_id}";
    public static final String DELETE_TASK_BY_ID = "/delete/{task_id}";

    @GetMapping(GET_ALL_TASKS)
    @Operation(
            summary = "Get Tasks for Task State by project id and task state id",
            description = "Returns all Tasks",
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
                    @ApiResponse(responseCode = "200", description = "Successful operation")
            }
    )
    public ResponseEntity<List<TaskDTO>> getTasks(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-states_id") Long taskStateId) {
        checkUserProjectAccessOrThrow(projectService.findById(projectId));

        ProjectTaskStateTuple tuple = checkTaskStateInProjectOrThrowEx(projectId, taskStateId, null);

        List<TaskEntity> taskEntities = tuple.getTaskState().getTaskEntities();

        return ResponseEntity
                .ok()
                .body(taskEntities.stream()
                        .map(modelMapper::getTaskDTO).collect(Collectors.toList()));

    }

    @GetMapping(GET_TASK_BY_ID)
    @Operation(
            summary = "Get Tasks for Task State by project id, task state id and task id",
            description = "Returns all Tasks",
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
                            schema = @Schema(type = "long")),
                    @Parameter(
                            name = "task_id",
                            description = "task id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    public ResponseEntity<TaskDTO> getTaskById(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-states_id") Long taskStateId,
            @PathVariable("task_id") Long taskId) {
        checkUserProjectAccessOrThrow(projectService.findById(projectId));
        checkTaskStateInProjectOrThrowEx(projectId, taskStateId, taskId);

        TaskEntity taskEntity = taskService.findById(taskId);

        return ResponseEntity
                .ok()
                .body(modelMapper.getTaskDTO(taskEntity));
    }

    @SneakyThrows
    @PostMapping(ADD_NEW_TASK)
    @Operation(
            summary = "Create Task for Task State",
            description = "Create new Task",
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
                    description = "empty JSON taskDTO",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
    public ResponseEntity<TaskDTO> createTask(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-states_id") Long taskStateId,
            @RequestBody TaskDTO taskDTO,
            BindingResult bindingResult) {
        checkUserProjectAccessOrThrow(projectService.findById(projectId));
        checkTaskStateInProjectOrThrowEx(projectId, taskStateId, null);

        taskDTO.setTaskStateId(taskStateId);
        TaskEntity task = modelMapper.getTask(taskDTO);

        taskValidator.validate(task, bindingResult);
        checkBindingResult(bindingResult);

        TaskEntity taskEntity = taskService.saveAndFlush(task);

        return ResponseEntity
                .created(new URI(String.format("/api/projects/%d/task-states/%d/tasks/%d",
                        projectId, taskStateId, taskEntity.getId())))
                .body(modelMapper.getTaskDTO(taskEntity));

    }

    @PatchMapping(UPDATE_TASK_BY_ID)
    @Operation(
            summary = "Update Task",
            description = "Returns updated Task",
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
                            schema = @Schema(type = "long")),
                    @Parameter(
                            name = "task_id",
                            description = "task id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    public ResponseEntity<TaskDTO> updateTaskById(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-states_id") Long taskStateId,
            @PathVariable("task_id") Long taskId,
            @RequestBody TaskDTO taskDTO,
            BindingResult bindingResult) {
        checkUserProjectAccessOrThrow(projectService.findById(projectId));
        if (taskDTO.getId() != null && !taskDTO.getId().equals(taskId)){
           throw new BadRequestException("Id in DTO and in url must be the same");
        }

        taskDTO.setId(taskId);
        taskDTO.setTaskStateId(taskStateId);
        TaskEntity task = modelMapper.getTask(taskDTO);

        checkTaskStateInProjectOrThrowEx(projectId, taskStateId, taskId);
        taskValidator.validate(task, bindingResult);
        checkBindingResult(bindingResult);

        TaskEntity updated = taskService.update(taskId, task);

        return ResponseEntity
                .ok()
                .body(modelMapper.getTaskDTO(updated));
    }

    @DeleteMapping(DELETE_TASK_BY_ID)
    @Operation(
            summary = "Delete Task",
            description = "Delete Task",
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
                            schema = @Schema(type = "long")),
                    @Parameter(
                            name = "task_id",
                            description = "task id",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "long"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    public ResponseEntity<HttpStatus> deleteTaskById(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-states_id") Long taskStateId,
            @PathVariable("task_id") Long taskId) {
        checkUserProjectAccessOrThrow(projectService.findById(projectId));
        checkTaskStateInProjectOrThrowEx(projectId, taskStateId, taskId);

        taskService.deleteById(taskId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ProjectTaskStateTuple checkTaskStateInProjectOrThrowEx(Long projectId, Long taskStateId, Long taskId) {
        ProjectEntity project = projectService.findById(projectId);
        TaskStateEntity taskState = taskStateService.findById(taskStateId);

        if (!project.getTaskStateEntities().contains(taskState)) {
            throw new NotFoundException(
                    String.format("There is no Task State with id %d in Project with id %d",
                            taskStateId, projectId));
        }

        if (taskId != null) {

            TaskEntity taskEntity = taskService.findById(taskId);

            if (!taskState.getTaskEntities().contains(taskEntity)) {
                throw new NotFoundException(
                        String.format("There is no Task with id %d in Task State with id %d",
                                taskId, taskStateId));
            }
        }

        return new ProjectTaskStateTuple(project, taskState);
    }

}
