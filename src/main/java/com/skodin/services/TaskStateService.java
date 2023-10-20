package com.skodin.services;

import com.skodin.exceptions.NotFoundException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.repositories.TaskStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TaskStateService {

    private final TaskStateRepository taskStateRepository;

    public List<TaskStateEntity> findAll() {
        return taskStateRepository.findAll();
    }

    public TaskStateEntity findById(Long aLong) {
        return taskStateRepository.findById(aLong)
                .orElseThrow(() -> new NotFoundException("Task State with id: " + aLong + " did not found"));
    }

    @Transactional
    public <S extends TaskStateEntity> S saveAndFlush(S entity) {
        return taskStateRepository.saveAndFlush(entity);
    }

    @Transactional
    public <S extends TaskStateEntity> S save(S entity) {
        return taskStateRepository.save(entity);
    }

    @Transactional
    public TaskStateEntity update(Long id, TaskStateEntity entity) {
        TaskStateEntity byId = findById(id);
        entity.setId(byId.getId());
        return saveAndFlush(entity);
    }

    @Transactional
    public void deleteById(Long aLong) {
        findById(aLong);    // just check does object exist
        taskStateRepository.deleteById(aLong);
    }

    public Optional<TaskStateEntity> findTaskStateEntityByNameAndProject(String name, ProjectEntity project) {
        return taskStateRepository.findTaskStateEntityByNameAndProject(name, project);
    }

    public int countAllByProjectId(Long projectId) {
        return taskStateRepository.countAllByProjectId(projectId);
    }

    public Optional<TaskStateEntity> findTaskStateEntityByOrderAndProject(Integer order, ProjectEntity project) {
        return taskStateRepository.findTaskStateEntityByOrderAndProject(order, project);
    }
}
