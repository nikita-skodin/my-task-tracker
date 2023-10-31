package com.skodin.util;

import com.skodin.DTO.ProjectDTO;
import com.skodin.DTO.TaskDTO;
import com.skodin.DTO.TaskStateDTO;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.TaskService;
import com.skodin.services.TaskStateService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ModelMapper {
    private final org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();
    private final TaskStateService taskStateService;

    {
        configureModelMapper(modelMapper);
    }

    public ProjectDTO getProjectDTO(ProjectEntity project) {
        return modelMapper.map(project, ProjectDTO.class);
    }

    public ProjectEntity getProject(ProjectDTO projectDTO) {
        return modelMapper.map(projectDTO, ProjectEntity.class);
    }

    public TaskStateDTO getTaskStateDTO(TaskStateEntity entity) {
        return modelMapper.map(entity, TaskStateDTO.class);
    }

    public TaskStateEntity getTaskState(TaskStateDTO stateDTO) {
        return modelMapper.map(stateDTO, TaskStateEntity.class);
    }

    public TaskDTO getTaskDTO(TaskEntity entity) {
        return modelMapper.map(entity, TaskDTO.class);
    }

    public TaskEntity getTask(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, TaskEntity.class);
    }

    private void configureModelMapper(org.modelmapper.ModelMapper modelMapper) {
        Converter<Optional<TaskStateEntity>, Long> TaskStateToDtoConverter = new AbstractConverter<>() {
            @Override
            protected Long convert(Optional<TaskStateEntity> taskStateEntity) {
                return taskStateEntity.map(TaskStateEntity::getId).orElse(null);
            }
        };

        Converter<Long, TaskStateEntity> DTOtoTaskStateConverter = new AbstractConverter<>() {
            @Override
            protected TaskStateEntity convert(Long aLong) {
                if (aLong != null) {
                    return taskStateService.findById(aLong);
                }
                return null;    // never
            }
        };
        Converter<TaskStateEntity, Long> TaskToDtoConverter = new AbstractConverter<>() {
            @Override
            protected Long convert(TaskStateEntity taskStateEntity) {
                return taskStateEntity.getId();
            }
        };

        // Task States
        modelMapper.typeMap(TaskStateEntity.class, TaskStateDTO.class)
                .addMappings(mapper -> {
                    mapper.using(TaskStateToDtoConverter).map(TaskStateEntity::getPreviousTaskState, TaskStateDTO::setPreviousTaskStateId);
                });

        modelMapper.typeMap(TaskStateEntity.class, TaskStateDTO.class)
                .addMappings(mapper -> {
                    mapper.using(TaskStateToDtoConverter).map(TaskStateEntity::getNextTaskState, TaskStateDTO::setNextTaskStateId);
                });

        modelMapper.typeMap(TaskStateDTO.class, TaskStateEntity.class)
                .addMappings(mapper -> {
                    mapper.using(DTOtoTaskStateConverter).map(TaskStateDTO::getPreviousTaskStateId, TaskStateEntity::setPreviousTaskState);
                });

        modelMapper.typeMap(TaskStateDTO.class, TaskStateEntity.class)
                .addMappings(mapper -> {
                    mapper.using(DTOtoTaskStateConverter).map(TaskStateDTO::getNextTaskStateId, TaskStateEntity::setNextTaskState);
                });

        // Tasks
        modelMapper.typeMap(TaskEntity.class, TaskDTO.class)
                .addMappings(mapper -> {
                    mapper.using(TaskToDtoConverter).map(TaskEntity::getTaskStateEntity, TaskDTO::setTaskStateId);
                });

        modelMapper.typeMap(TaskDTO.class, TaskEntity.class)
                .addMappings(mapper -> {
                    mapper.using(DTOtoTaskStateConverter).map(TaskDTO::getTaskStateId, TaskEntity::setTaskStateEntity);
                });
    }
}
