package com.skodin.services;

import com.skodin.exceptions.NotFoundException;
import com.skodin.models.TaskEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskEntity findById(Long aLong) {
        return taskRepository.findById(aLong)
                .orElseThrow(() -> new NotFoundException("Task with id: " + aLong + " is not found"));
    }

    public List<TaskEntity> findAll() {
        return taskRepository.findAll();
    }

    public Optional<TaskEntity> findTaskEntityByNameAndTaskStateEntity(String name, TaskStateEntity taskStateEntity) {
        return taskRepository.findTaskEntityByNameAndTaskStateEntity(name, taskStateEntity);
    }

    @Transactional
    public <S extends TaskEntity> S save(S entity) {
        return taskRepository.save(entity);
    }

    @Transactional
    public <S extends TaskEntity> S saveAndFlush(S entity) {
        return taskRepository.saveAndFlush(entity);
    }

    @Transactional
    public void deleteById(Long aLong) {
        TaskEntity entity = findById(aLong);// just check does object exist
        entity.getTaskStateEntity().getTaskEntities().remove(entity);
        // TODO: 022 разобраться почему тут недостаточно простого удаления в то время как в стопке этого с головой
        taskRepository.deleteById(aLong);
    }

    @Transactional
    public TaskEntity update(Long id, TaskEntity source){
        TaskEntity byId = findById(id);
        source.setId(byId.getId());
        return saveAndFlush(source);
    }
}
