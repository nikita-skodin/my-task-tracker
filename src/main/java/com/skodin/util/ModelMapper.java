package com.skodin.util;

import com.skodin.DTO.ProjectDTO;
import com.skodin.DTO.TaskDTO;
import com.skodin.DTO.TaskStateDTO;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.TaskStateService;
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
        map.setProjectId(entity.getProject().getId());
        return map;
    }

    public static TaskStateEntity getTaskState (TaskStateDTO stateDTO, ProjectService projectService){
        TaskStateEntity map = modelMapper.map(stateDTO, TaskStateEntity.class);
        map.setProject(projectService.findById(stateDTO.getProjectId()));
        return map;
    }

    public static TaskDTO getTaskDTO (TaskEntity entity){
        TaskDTO map = modelMapper.map(entity, TaskDTO.class);
        map.setTaskStateId(entity.getTaskStateEntity().getId());
        return map;
    }

    public static TaskEntity getTask (TaskDTO taskDTO, TaskStateService taskStateService){
        TaskEntity map = modelMapper.map(taskDTO, TaskEntity.class);
        map.setTaskStateEntity(taskStateService.findById(taskDTO.getTaskStateId()));
        return map;
    }
}
