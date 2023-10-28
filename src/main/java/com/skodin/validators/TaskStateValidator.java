package com.skodin.validators;

import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.TaskStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskStateValidator implements Validator {

    private final TaskStateService taskStateService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(TaskStateEntity.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        TaskStateEntity taskStateEntity = (TaskStateEntity) target;

        String name = taskStateEntity.getName();
        ProjectEntity project = taskStateEntity.getProject();

        Optional<TaskStateEntity> entityByNameAndProject = taskStateService
                .findTaskStateEntityByNameAndProject(name, project);

        if (entityByNameAndProject.isPresent() &&
                !Objects.equals(entityByNameAndProject.get().getId(), taskStateEntity.getId())){
            errors.rejectValue("name", "400",
                    String.format("Task State with name %s is already exist in project with id %d",
                            name, project.getId()));
        }

    }
}
