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

    int countAllByProjectId(Long projectId);

    Optional<TaskStateEntity> findTaskStateEntityByOrderAndProject(Integer order, ProjectEntity project);
}

