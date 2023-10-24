package com.skodin.repositories;

import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStateRepository extends JpaRepository<TaskStateEntity, Long> {
    Optional<TaskStateEntity> findTaskStateEntityByNameAndProject(String name, ProjectEntity project);

    Optional<TaskStateEntity> findTaskStateEntityByPreviousTaskStateNullAndProject(ProjectEntity project);
    Optional<TaskStateEntity> findTaskStateEntityByNextTaskStateNullAndProject(ProjectEntity project);
    Optional<TaskStateEntity> findTaskStateEntityByPreviousTaskState(TaskStateEntity previousTaskState);
    Optional<TaskStateEntity> findTaskStateEntityByNextTaskState(TaskStateEntity previousTaskState);

}

