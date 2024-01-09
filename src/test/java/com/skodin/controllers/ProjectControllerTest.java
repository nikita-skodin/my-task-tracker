package com.skodin.controllers;

import com.skodin.DTO.ProjectDTO;
import com.skodin.models.ProjectEntity;
import com.skodin.models.Role;
import com.skodin.models.TaskStateEntity;
import com.skodin.models.UserEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.TaskStateService;
import com.skodin.services.UserService;
import com.skodin.util.ModelMapper;
import com.skodin.validators.ProjectValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest extends MainController {

    private static final Long ID = 1L;
    private static final Instant NOW = Instant.now();

    private static final UserEntity USER = new UserEntity(
            ID, "name", "password",
            "email", null, Role.USER, null
    );

    @Mock
    ProjectService projectService;

    @Mock
    ProjectValidator projectValidator;

    @Mock
    TaskStateService taskStateService;

    @Mock
    BindingResult bindingResult;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    ProjectController projectController;

    @Test
    void getAllProjects_EmptyOptional_ReturnsValidResponseEntity() {
        // setup
        var projects = List.of(
                new ProjectEntity(ID, "name1", NOW, USER, new ArrayList<>()),
                new ProjectEntity(ID, "name2", NOW, USER, new ArrayList<>())
        );

        var projectsDTO = List.of(
                new ProjectDTO(ID, "name1", NOW, USER.getId(), new ArrayList<>()),
                new ProjectDTO(ID, "name2", NOW, USER.getId(), new ArrayList<>())
        );

        when(projectService.findAllByUser(USER)).thenReturn(projects);
        when(modelMapper.getProjectDTO(projects.get(0))).thenReturn(projectsDTO.get(0));
        when(modelMapper.getProjectDTO(projects.get(1))).thenReturn(projectsDTO.get(1));

        // act
        ResponseEntity<List<ProjectDTO>> result;

        try (MockedStatic<UserService> theMock = mockStatic(UserService.class)) {
            theMock.when(UserService::getCurrentUser).thenReturn(USER);
            result = projectController.getAllProjects(Optional.empty());
        }

        //verify
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(result.getHeaders().getContentType(), MediaType.APPLICATION_JSON);

        assertEquals(result.getBody(), projectsDTO);
    }

    @Test
    void getAllProjects_NotEmptyOptional_ReturnsValidResponseEntity() {
        // setup
        Optional<String> prefix = Optional.of("Test");

        var projects = List.of(
                new ProjectEntity(ID, "TestName1", NOW, USER, new ArrayList<>()),
                new ProjectEntity(ID, "TestName2", NOW, USER, new ArrayList<>()));

        var projectsDTO = List.of(
                new ProjectDTO(ID, "TestName1", NOW, USER.getId(), new ArrayList<>()),
                new ProjectDTO(ID, "TestName2", NOW, USER.getId(), new ArrayList<>())
        );

        when(projectService.findAllByNameStartingWithAndUser(prefix.get(), USER)).thenReturn(projects);
        when(modelMapper.getProjectDTO(projects.get(0))).thenReturn(projectsDTO.get(0));
        when(modelMapper.getProjectDTO(projects.get(1))).thenReturn(projectsDTO.get(1));

        // act
        ResponseEntity<List<ProjectDTO>> result;

        try (MockedStatic<UserService> theMock = mockStatic(UserService.class)) {
            theMock.when(UserService::getCurrentUser).thenReturn(USER);
            result = projectController.getAllProjects(prefix);
        }

        //verify
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getBody(), projectsDTO);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(result.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
    }

    @Test
    void getProjectById_ReturnsValidResponseEntity() {
        // setup
        ProjectEntity project = new ProjectEntity(ID, "TestName1", NOW, USER, new ArrayList<>());
        ProjectDTO projectDTO = new ProjectDTO(ID, "TestName1", NOW, USER.getId(), new ArrayList<>());

        when(projectService.findById(ID)).thenReturn(project);
        when(modelMapper.getProjectDTO(project)).thenReturn(projectDTO);

        // act
        ResponseEntity<ProjectDTO> result = projectController.getProjectById(ID);

        //verify
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getBody(), projectDTO);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(result.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
    }


    @Test
    void createProject_ReturnsValidProjectDTO() {
        // setup
        ProjectDTO projectDTO = new ProjectDTO(null, "name", NOW, null, new ArrayList<>());
        ProjectEntity project = new ProjectEntity(null, "name", NOW, null, new ArrayList<>());

        ProjectEntity returnedProject = new ProjectEntity(ID, "name", NOW, USER, new ArrayList<>());
        ProjectDTO returnedProjectDTO = new ProjectDTO(ID, "name", NOW, USER.getId(), new ArrayList<>());

        when(modelMapper.getProject(projectDTO)).thenReturn(project);
        doNothing().when(projectValidator).validate(project, bindingResult);
        when(projectService.saveAndFlush(project)).thenReturn(returnedProject);
        when(modelMapper.getProjectDTO(returnedProject)).thenReturn(returnedProjectDTO);
        when(taskStateService.saveAndFlush(any(TaskStateEntity.class))).thenReturn(new TaskStateEntity());

        // act
        ResponseEntity<ProjectDTO> result;
        try (MockedStatic<UserService> theMock = mockStatic(UserService.class)) {
            theMock.when(UserService::getCurrentUser).thenReturn(USER);

            result = projectController.createProject(projectDTO, bindingResult);

        }

        //verify
        verify(projectValidator).validate(project, bindingResult);

        verify(taskStateService, times(1))
                .saveAndFlush(argThat(argument -> argument.getName().equals("In progress")));
        verify(taskStateService, times(1))
                .saveAndFlush(argThat(argument -> argument.getName().equals("Done")));
        verify(taskStateService, times(1))
                .saveAndFlush(argThat(argument -> argument.getName().equals("To do")));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getBody(), returnedProjectDTO);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(201));
        assertEquals(result.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
    }


    @Test
    void deleteProjectById_ReturnsValidResponseEntity() {
        // setup
        doNothing().when(projectService).deleteById(ID);

        // act
        ResponseEntity<HttpStatus> result = projectController.deleteProjectById(ID);

        //verify
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(200));
        verify(projectService, times(1)).deleteById(ID);
    }


    @Test
    void updateProject_ReturnsValidResponseEntity() {
        // setup
        ProjectDTO projectDTO = new ProjectDTO(null, "name", NOW, null, new ArrayList<>());
        ProjectEntity project = new ProjectEntity(null, "name", NOW, null, new ArrayList<>());

        ProjectEntity updatedProject = new ProjectEntity(ID, "name", NOW, USER, new ArrayList<>());
        ProjectDTO returnedProjectDTO = new ProjectDTO(ID, "name", NOW, USER.getId(), new ArrayList<>());

        when(modelMapper.getProject(projectDTO)).thenReturn(project);
        when(modelMapper.getProjectDTO(updatedProject)).thenReturn(returnedProjectDTO);
        doNothing().when(projectValidator).validate(project, bindingResult);
        when(projectService.update(ID, project)).thenReturn(updatedProject);

        // act
        ResponseEntity<ProjectDTO> result = projectController.updateProject(projectDTO, ID, bindingResult);

        //verify
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getBody(), returnedProjectDTO);

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(result.getHeaders().getContentType(), MediaType.APPLICATION_JSON);

        verify(projectService, times(1)).update(ID, project);
    }


}