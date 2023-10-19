package com.skodin.controllers;

import com.skodin.DTO.TaskStateDTO;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import com.skodin.util.ModelMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class TaskStateController {

    ProjectService projectService;

    public static final String GET_ALL_TASK_STATES = "";

    @GetMapping(GET_ALL_TASK_STATES)
    public ResponseEntity<List<TaskStateDTO>> getAllTaskStates(@PathVariable("project_id") Long id){

        List<TaskStateEntity> taskStateEntities = projectService.findById(id).getTaskStateEntities();

        return ResponseEntity
                .ok()
                .body(taskStateEntities.stream()
                        .map(ModelMapper::getTaskStateDTO).collect(Collectors.toList()));

    }
}
