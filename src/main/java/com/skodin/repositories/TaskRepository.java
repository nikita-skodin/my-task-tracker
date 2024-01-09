package com.skodin.repositories;

import com.skodin.models.TaskEntity;
import com.skodin.models.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findTaskEntityByNameAndTaskStateEntity(String name, TaskStateEntity taskStateEntity);
}
