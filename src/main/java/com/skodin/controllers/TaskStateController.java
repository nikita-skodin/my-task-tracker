package com.skodin.controllers;

import com.skodin.DTO.TaskStateDTO;
import com.skodin.exceptions.BagRequestException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.TaskStateService;
import com.skodin.util.ModelMapper;
import com.skodin.validators.TaskStateValidator;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
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
    public static final String UPDATE_ORDER = "";
    public static final String GET_TASK_STATE_BY_ID = "/{task-state_id}";

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

        if (!Objects.equals(projectId, taskStateEntity.getProject().getId())){
            throw new NotFoundException(String.format(
                    "There is no Task State with id %d in Project with id %d", taskStateId, projectId));
        }

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
            @Valid @RequestBody TaskStateDTO taskStateDTO,
            BindingResult bindingResult,
            @PathVariable("project_id") Long id) {

        if (taskStateDTO.getId() != null) {
            throw new BagRequestException("New Task State cannot has an id");
        }
        if (taskStateDTO.getOrder() == null) {
            throw new BagRequestException("New Task State should has an order");
        }

        taskStateDTO.setProjectId(id);

        TaskStateEntity taskState = ModelMapper.getTaskState(taskStateDTO, projectService);

        taskStateValidator.validate(taskState, bindingResult);
        checkBindingResult(bindingResult);

        TaskStateEntity taskStateEntity = taskStateService.saveAndFlush(taskState);

        TaskStateDTO taskStateDTO1 = ModelMapper.getTaskStateDTO(taskStateEntity);

        return ResponseEntity
                .created(new URI(String.format("/api/projects/%d/task-states/%d",
                        id, taskStateEntity.getId())))
                .body(taskStateDTO1);

    }

    @PatchMapping(UPDATE_ORDER)
    public ResponseEntity<List<TaskStateDTO>> updateOrder(
            @PathVariable("project_id") Long id,
            @RequestBody List<TaskStateDTO> taskStateDTOS){

        if (taskStateDTOS.size() != 2) {
            throw new BagRequestException("List should has 2 objects");
        }

        TaskStateDTO taskStateDTO1 = taskStateDTOS.get(0);
        TaskStateDTO taskStateDTO2 = taskStateDTOS.get(1);

        if (!Objects.equals(taskStateDTO1.getProjectId(), taskStateDTO2.getProjectId())) {
            throw new BagRequestException("Task States should has equals project_id");
        }
        if (Objects.equals(taskStateDTO1.getId(), taskStateDTO2.getId())) {
            throw new BagRequestException("Task States should has different id");
        }
        if (Objects.equals(taskStateDTO1.getId(), id)) {
            throw new BagRequestException("Task States must belong to the project with id " + id);
        }

        TaskStateEntity taskState1 = ModelMapper.getTaskState(taskStateDTO1, projectService);
        TaskStateEntity taskState2 = ModelMapper.getTaskState(taskStateDTO2, projectService);

        setOrder(taskState1, taskState2);

        List<TaskStateEntity> list = List.of(taskStateService.update(taskState1.getId(), taskState1),
                taskStateService.update(taskState2.getId(), taskState2));

        return ResponseEntity
                .ok()
                .body(list.stream()
                        .map(ModelMapper::getTaskStateDTO).collect(Collectors.toList()));
    }

    private void setOrder(TaskStateEntity taskState1, TaskStateEntity taskState2) {
        int order = taskState1.getOrder();
        taskState1.setOrder(taskState2.getOrder());
        taskState2.setOrder(order);
    }
}
