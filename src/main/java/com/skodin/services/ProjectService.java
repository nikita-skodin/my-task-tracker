package com.skodin.services;

import com.skodin.exceptions.ForbiddenException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.models.UserEntity;
import com.skodin.repositories.ProjectRepository;
import com.skodin.services.cache.ProjectServiceCache;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    private final ProjectServiceCache projectServiceCache;
    private final CacheManager cacheManager;

    public ProjectEntity findById(Long id) {
        return projectServiceCache.findById(id);
    }

    public List<ProjectEntity> findAllByUser(UserEntity user) {
        return projectRepository.findAllByUser(user);
    }

    @Cacheable(value = "ProjectRepository::findByNameAndUser", key = "#name")
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
    @Caching(
            put = {
                    @CachePut(value = "ProjectService::findById", key = "#id"),
                    @CachePut(value = "ProjectRepository::findByNameAndUser", key = "#source.name"),
            }
    )
    public ProjectEntity update(Long id, ProjectEntity source) {
        ProjectEntity project = findById(id);

        if (!Objects.equals(project.getUser().getId(), UserService.getCurrentUser().getId())) {
            throw new ForbiddenException("FORBIDDEN");
        }

        project.setName(source.getName());
        return projectRepository.saveAndFlush(project);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "ProjectService::findById", key = "#id"),
            }
    )
    public void deleteById(Long id) {
        ProjectEntity project = findById(id);

        if (!Objects.equals(project.getUser().getId(), UserService.getCurrentUser().getId())) {
            throw new ForbiddenException("FORBIDDEN");
        }

        projectRepository.deleteById(id);

        Cache cache = cacheManager.getCache("ProjectRepository::findByNameAndUser");
        if (cache != null) {
            cache.evict(project.getName());
        }
    }
}

