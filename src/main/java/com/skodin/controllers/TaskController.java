package com.skodin.controllers;

import com.skodin.DTO.TaskDTO;
import com.skodin.DTO.TaskStateDTO;
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

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/projects/{project_id}/task-states/{task-states_id}/tasks")
public class TaskController extends MainController{

    TaskService taskService;
    ProjectService projectService;
    TaskStateService taskStateService;

    TaskValidator taskValidator;

    public static final String ADD_NEW_TASK = "";
    public static final String GET_ALL_TASKS = "";


    /**
     * в дто надо только название и содержание
     */
    @SneakyThrows
    @PostMapping(ADD_NEW_TASK)
    public ResponseEntity<TaskDTO> createTask(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-states_id") Long taskStateId,
            @RequestBody TaskDTO taskDTO,
            BindingResult bindingResult){


        checkTaskStateInProjectOrThrowEx(projectId, taskStateId);

        taskDTO.setTaskStateId(taskStateId);
        TaskEntity task = ModelMapper.getTask(taskDTO, taskStateService);

        taskValidator.validate(task, bindingResult);
        checkBindingResult(bindingResult);

        TaskEntity taskEntity = taskService.saveAndFlush(task);

        return ResponseEntity
                .created(new URI(String.format("/api/projects/%d/task-states/%d/tasks/%d",
                        projectId, taskStateId, taskEntity.getId())))
                .body(ModelMapper.getTaskDTO(taskEntity));

    }

    @GetMapping(GET_ALL_TASKS)
    public ResponseEntity<List<TaskDTO>> getTasks(
            @PathVariable("project_id") Long projectId,
            @PathVariable("task-states_id") Long taskStateId){


        ProjectTaskStateTuple tuple = checkTaskStateInProjectOrThrowEx(projectId, taskStateId);

        List<TaskEntity> taskEntities = tuple.getTaskState().getTaskEntities();

        return ResponseEntity
                .ok()
                .body(taskEntities.stream()
                        .map(ModelMapper::getTaskDTO).collect(Collectors.toList()));

    }

    private ProjectTaskStateTuple checkTaskStateInProjectOrThrowEx(Long projectId, Long taskStateId) {
        ProjectEntity project = projectService.findById(projectId);
        TaskStateEntity taskState = taskStateService.findById(taskStateId);

        if (!project.getTaskStateEntities().contains(taskState)){
            throw new NotFoundException(
                    String.format("There is no Task State with id %d in Project with id %d",
                            taskStateId, projectId));
        }

        return new ProjectTaskStateTuple(project, taskState);
    }

}
