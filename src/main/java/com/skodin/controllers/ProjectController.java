package com.skodin.controllers;

import com.skodin.DTO.ProjectDTO;
import com.skodin.exceptions.BagRequestException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.TaskStateService;
import com.skodin.util.ModelMapper;
import com.skodin.validators.ProjectValidator;
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
import java.util.Optional;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/projects")
public class ProjectController extends MainController {

    ProjectService projectService;
    TaskStateService taskStateService;
    ProjectValidator projectValidator;

    public static final String CREATE_PROJECT = "";
    public static final String GET_PROJECTS = "";
    public static final String GET_PROJECT_BY_ID = "/{id}";
    public static final String UPDATE_PROJECT_BY_ID = "/{id}";
    public static final String DELETE_PROJECT_BY_ID = "/{id}";

    @GetMapping(GET_PROJECTS)
    public ResponseEntity<List<ProjectDTO>> getProjects(
            @RequestParam(required = false) Optional<String> prefix) {

        List<ProjectEntity> all;

        if (prefix.isPresent()) {
            all = projectService.findAllByNameStartingWith(prefix.get().trim()); // вместо проверки просто trim
        } else {
            all = projectService.findAll();
        }

        return ResponseEntity
                .ok()
                .body(all.stream().map(ModelMapper::getProjectDTO).collect(Collectors.toList()));
    }

    @GetMapping(GET_PROJECT_BY_ID)
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity
                .ok()
                .body(ModelMapper.getProjectDTO(projectService.findById(id)));
    }


    /**
     * Передается пустой project после создания
     */
    @SneakyThrows
    @PostMapping(CREATE_PROJECT)
    public ResponseEntity<ProjectDTO> createProject(
            @RequestBody ProjectDTO projectDTO,
            BindingResult bindingResult
    ) {
        if (projectDTO.getId() != null) {
            throw new BagRequestException("New Project cannot has an id");
        }

        ProjectEntity project = ModelMapper.getProject(projectDTO);

        projectValidator.validate(project, bindingResult);
        checkBindingResult(bindingResult);


        ProjectEntity projectEntity = projectService.saveAndFlush(project);

        addStates(project);

        ProjectDTO projectDTO1 = ModelMapper.getProjectDTO(projectEntity);

        return ResponseEntity
                .created(new URI("/api/projects/" + projectDTO1.getId()))
                .body(projectDTO1);
    }

    /**
     * берет все поля из DTO,
     * для добавления нового state лучше использовать другой url
     */
    @PatchMapping(UPDATE_PROJECT_BY_ID)
    public ResponseEntity<ProjectDTO> updateProject(
            @RequestBody ProjectDTO projectDTO,
            @PathVariable Long id,
            BindingResult bindingResult) {

        ProjectEntity project = ModelMapper.getProject(projectDTO);

        projectValidator.validate(project, bindingResult);
        checkBindingResult(bindingResult);

        ProjectEntity update = projectService.update(id, project);

        return ResponseEntity
                .ok()
                .body(ModelMapper.getProjectDTO(update));
    }

    @DeleteMapping(DELETE_PROJECT_BY_ID)
    public ResponseEntity<HttpStatus> deleteProjectById(@PathVariable Long id) {

        projectService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addStates(ProjectEntity project) {

        TaskStateEntity toDo = taskStateService.saveAndFlush(TaskStateEntity.builder()
                .project(project).name("To do").build());
        TaskStateEntity inProgress =  taskStateService.saveAndFlush(TaskStateEntity.builder()
                .project(project).name("In progress").build());
        TaskStateEntity done =  taskStateService.saveAndFlush(TaskStateEntity.builder()
                .project(project).name("Done").build());

        // TODO: 023 разобраться почему криво возвращает

        project.addProjectEntities(toDo, inProgress, done);
    }
}

