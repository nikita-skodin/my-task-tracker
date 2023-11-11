package com.skodin.controllers;

import com.skodin.DTO.ProjectDTO;
import com.skodin.models.ProjectEntity;
import com.skodin.models.Role;
import com.skodin.models.UserEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.UserService;
import com.skodin.util.ModelMapper;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest extends MainController {

    private static final Long ID = 1L;
    private static final Instant NOW = Instant.now();

    private static final UserEntity USER = new UserEntity(
            ID, "name", "password",
            "email", null, Role.USER
    );

    @Mock
    ProjectService projectService;

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
        ResponseEntity<?> result;

        try (MockedStatic<UserService> theMock = mockStatic(UserService.class)) {
            theMock.when(UserService::getCurrentUser).thenReturn(USER);

            result = projectController.getAllProjects(Optional.empty());
        }

        List<ProjectDTO> resultBody = (List<ProjectDTO>) result.getBody();

        //verify
        assertNotNull(result);
        assertNotNull(resultBody);
        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(result.getHeaders().getContentType(), MediaType.APPLICATION_JSON);

        assertTrue(resultBody.size() == projectsDTO.size() &&
                resultBody.containsAll(projectsDTO) && projectsDTO.containsAll(resultBody));
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
        ResponseEntity<?> result;

        try (MockedStatic<UserService> theMock = mockStatic(UserService.class)) {
            theMock.when(UserService::getCurrentUser).thenReturn(USER);

            result = projectController.getAllProjects(prefix);
        }

        List<ProjectDTO> resultBody = (List<ProjectDTO>) result.getBody();

        //verify
        assertNotNull(result);
        assertNotNull(resultBody);

        List<ProjectDTO> list = resultBody.stream().filter(el -> !el.getName().startsWith(prefix.get())).toList();
        assertTrue(list.isEmpty());

        assertEquals(result.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(result.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
    }
}