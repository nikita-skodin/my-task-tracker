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

        /*можно ли  переопределить метод  map для объекта ModelMapper в java */

        ProjectDTO map = modelMapper.map(project, ProjectDTO.class);

        // мапит встроенной map для dto отсюда и ошибка

//        System.err.println(map);

        return map;
    }

    public static ProjectEntity getProject (ProjectDTO projectDTO){

        ProjectEntity map = modelMapper.map(projectDTO, ProjectEntity.class);

        return map;
    }

    public static TaskStateDTO getTaskStateDTO (TaskStateEntity entity){
        TaskStateDTO map = modelMapper.map(entity, TaskStateDTO.class);
        entity.getPreviousTaskState().ifPresent(s -> map.setPreviousTaskStateId(s.getId()));
        entity.getNextTaskState().ifPresent(s -> map.setNextTaskStateId(s.getId()));

        System.err.println(map);

        return map;
    }

    public static TaskStateEntity getTaskState (TaskStateDTO stateDTO, TaskStateService stateService){
        TaskStateEntity map = modelMapper.map(stateDTO, TaskStateEntity.class);

        Long nextTaskStateId = stateDTO.getNextTaskStateId();
        Long previousTaskStateId = stateDTO.getPreviousTaskStateId();

        map.setNextTaskState(nextTaskStateId == null ? null : stateService.findById(nextTaskStateId));
        map.setPreviousTaskState(previousTaskStateId == null ? null : stateService.findById(previousTaskStateId));
        return map;
    }

    public static TaskDTO getTaskDTO (TaskEntity entity){
        TaskDTO map = modelMapper.map(entity, TaskDTO.class);
        map.setTaskStateId(entity.getTaskStateEntity().getId());
        return map;
    }

    public static TaskEntity getTask (TaskDTO taskDTO, TaskStateService taskStateService){
        TaskEntity map = modelMapper.map(taskDTO, TaskEntity.class);
        System.err.println(taskDTO);
        map.setTaskStateEntity(taskStateService.findById(taskDTO.getTaskStateId()));
        return map;
    }
}
