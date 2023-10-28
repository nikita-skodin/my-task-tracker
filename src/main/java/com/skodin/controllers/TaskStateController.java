package com.skodin.controllers;

import com.skodin.DTO.TaskStateDTO;
import com.skodin.exceptions.BadRequestException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.TaskStateService;
import com.skodin.util.ModelMapper;
import com.skodin.validators.TaskStateValidator;
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

    public static final String GET_ALL_TASK_STATES = "";
    public static final String CREATE_TASK_STATE = "";
    public static final String GET_TASK_STATE_BY_ID = "/{task-state_id}";
    public static final String UPDATE_TASK_STATE_BY_ID = "/{task-state_id}";
    public static final String DELETE_TASK_STATE_BY_ID = "/{task-state_id}";

    @GetMapping(GET_ALL_TASK_STATES)
    public ResponseEntity<List<TaskStateDTO>> getAllTaskStates(@PathVariable("project_id") Long id) {

        List<TaskStateEntity> taskStateEntities = projectService.findById(id).getTaskStateEntities();

        return ResponseEntity
                .ok()
                .body(taskStateEntities.stream()
                        .map(ModelMapper::getTaskStateDTO).collect(Collectors.toList()));

    }

    @GetMapping(GET_TASK_STATE_BY_ID)
    public ResponseEntity<TaskStateDTO> getTaskState(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-state_id") Long taskStateId) {

        TaskStateEntity taskStateEntity = taskStateService.findById(taskStateId);

        taskStateInProjectOrThrowEx(taskStateId, projectId, taskStateEntity);

        return ResponseEntity
                .ok()
                .body(ModelMapper.getTaskStateDTO(taskStateEntity));

    }

    /**
     * id назначаются автоматически
     * создается пустая стопка
     */
    @SneakyThrows
    @PostMapping(CREATE_TASK_STATE)
    public ResponseEntity<TaskStateDTO> createTaskState(
            @RequestBody TaskStateDTO taskStateDTO,
            BindingResult bindingResult,
            @PathVariable("project_id") Long id) {

        if (taskStateDTO.getId() != null) {
            throw new BadRequestException("New Task State cannot has an id");
        }

        taskStateDTO.setProjectId(id);

        TaskStateEntity taskState = ModelMapper.getTaskState(taskStateDTO, taskStateService);

        taskStateValidator.validate(taskState, bindingResult);
        checkBindingResult(bindingResult);

        TaskStateEntity taskStateEntity = taskStateService.saveAndFlush(taskState);

        TaskStateDTO taskStateDTO1 = ModelMapper.getTaskStateDTO(taskStateEntity);

        return ResponseEntity
                .created(new URI(String.format("/api/projects/%d/task-states/%d",
                        id, taskStateEntity.getId())))
                .body(taskStateDTO1);

    }

    /**
     * для добавления нового task использовать другой url
     */
    @PatchMapping(UPDATE_TASK_STATE_BY_ID)
    public ResponseEntity<TaskStateDTO> updateProject(
            @RequestBody TaskStateDTO taskStateDTO,
            BindingResult bindingResult,
            @PathVariable("task-state_id") Long taskStateId,
            @PathVariable("project_id") Long projectId){

        if (!Objects.equals(projectId, taskStateDTO.getProjectId())){
            throw new BadRequestException("You can not change project for Task States");
        }

        taskStateDTO.setId(taskStateId);
        TaskStateEntity taskStateFromHttp = ModelMapper.getTaskState(taskStateDTO, taskStateService);

        taskStateInProjectOrThrowEx(taskStateId, projectId, taskStateFromHttp);

        taskStateValidator.validate(taskStateFromHttp, bindingResult);
        checkBindingResult(bindingResult);

        TaskStateEntity update = taskStateService.update(taskStateId, taskStateFromHttp);

        return ResponseEntity
                .ok()
                .body(ModelMapper.getTaskStateDTO(update));
    }

    // TODO: 022 добавить смену порядка при удалении
    @DeleteMapping(DELETE_TASK_STATE_BY_ID)
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

