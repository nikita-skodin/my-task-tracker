package com.skodin.validators;

import com.skodin.models.TaskEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TaskValidator implements Validator {

    private final TaskService taskService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(TaskEntity.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TaskEntity taskEntity = (TaskEntity) target;

        String name = taskEntity.getName();
        TaskStateEntity taskStateEntity = taskEntity.getTaskStateEntity();

        Optional<TaskEntity> optional = taskService
                .findTaskEntityByNameAndTaskStateEntity(name, taskStateEntity);

        if (optional.isPresent() && !Objects.equals(optional.get().getId(), taskEntity.getId())) {
            errors.rejectValue("name", "400",
                    String.format("Task with name %s is already exist in project with id %d",
                            name, taskStateEntity.getId()));
        }

    }
}
