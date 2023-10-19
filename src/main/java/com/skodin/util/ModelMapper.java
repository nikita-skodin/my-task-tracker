package com.skodin.util;

import com.skodin.DTO.ProjectDTO;
import com.skodin.DTO.TaskStateDTO;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import lombok.RequiredArgsConstructor;

public class ModelMapper {
    private static final org.modelmapper.ModelMapper modelMapper;

    static {
        modelMapper = new org.modelmapper.ModelMapper();
    }

    public static ProjectDTO getProjectDTO (ProjectEntity project){
        return modelMapper.map(project, ProjectDTO.class);
    }

    public static ProjectEntity getProject (ProjectDTO projectDTO){
        return modelMapper.map(projectDTO, ProjectEntity.class);
    }

    public static TaskStateDTO getTaskStateDTO (TaskStateEntity entity){
        TaskStateDTO map = modelMapper.map(entity, TaskStateDTO.class);
        map.setProjectId(entity.getId());
        return map;
    }

    public static TaskStateEntity getTaskState (TaskStateDTO stateDTO, ProjectService projectService){
        TaskStateEntity map = modelMapper.map(stateDTO, TaskStateEntity.class);
        // TODO: 019 на notFound надо проверять раньше
        map.setProject(projectService.findById(stateDTO.getProjectId()));
        return map;
    }
}
