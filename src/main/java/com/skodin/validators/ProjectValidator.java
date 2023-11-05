package com.skodin.validators;

import com.skodin.models.ProjectEntity;
import com.skodin.services.ProjectService;
import com.skodin.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectValidator implements Validator {

    private final ProjectService projectService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(ProjectEntity.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ProjectEntity project = (ProjectEntity) target;

        Optional<ProjectEntity> entity = projectService.findByNameAndUser(
                project.getName(), UserService.getCurrentUser());

        if (entity.isPresent()) {
            errors.rejectValue("name", "400",
                    "Project with name " + project.getName() + " is already exist");
        }


    }
}
