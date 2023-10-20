package com.skodin.controllers;

import com.skodin.DTO.TaskStateDTO;
import com.skodin.exceptions.BagRequestException;
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

    @GetMapping(GET_ALL_TASK_STATES)
    public ResponseEntity<List<TaskStateDTO>> getAllTaskStates(@PathVariable("project_id") Long id) {

        List<TaskStateEntity> taskStateEntities = projectService.findById(id).getTaskStateEntities();

        return ResponseEntity
                .ok()
                .body(taskStateEntities.stream()
                        .map(ModelMapper::getTaskStateDTO).collect(Collectors.toList()));

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
}
