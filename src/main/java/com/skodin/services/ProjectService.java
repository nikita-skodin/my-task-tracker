package com.skodin.services;

import com.skodin.exceptions.ForbiddenException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.models.UserEntity;
import com.skodin.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    @SneakyThrows
    @Cacheable("findById")
    public ProjectEntity findById(Long id) {
        System.err.println("METHOD IS WORKING");
        Thread.sleep(3000);
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project with id " + id + " did not found"));
    }

    public List<ProjectEntity> findAllByUser(UserEntity user) {
        return projectRepository.findAllByUser(user);
    }

    public Optional<ProjectEntity> findByNameAndUser(String name, UserEntity user) {
        return projectRepository.findByNameAndUser(name, user);
    }

    public List<ProjectEntity> findAllByNameStartingWithAndUser(String name, UserEntity user) {
        return projectRepository.findAllByNameStartingWithAndUser(name, user);
    }

    @Transactional
    public <S extends ProjectEntity> S saveAndFlush(S entity) {

        for (TaskStateEntity el : entity.getTaskStateEntities()) {
            el.setProject(entity);
        }

        return projectRepository.saveAndFlush(entity);
    }

    @Transactional
    @CachePut(value = "findById", key = "#id")
    public ProjectEntity update(Long id, ProjectEntity source) {
        ProjectEntity project = findById(id);

        if (!Objects.equals(project.getUser().getId(), UserService.getCurrentUser().getId())) {
            throw new ForbiddenException("FORBIDDEN");
        }

        project.setCreatedAt(source.getCreatedAt());
        project.setName(source.getName());
        return projectRepository.saveAndFlush(project);
    }

    @Transactional
    @CacheEvict("findById")
    public void deleteById(Long id) {

        ProjectEntity project = findById(id);

        if (!Objects.equals(project.getUser().getId(), UserService.getCurrentUser().getId())) {
            throw new ForbiddenException("FORBIDDEN");
        }

        projectRepository.deleteById(id);
    }
}

