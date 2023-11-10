package com.skodin.services;

import com.skodin.exceptions.ForbiddenException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.models.UserEntity;
import com.skodin.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectEntity> findAllByNameStartingWithAndUser(String name, UserEntity user) {
        return projectRepository.findAllByNameStartingWithAndUser(name, user);
    }

    public List<ProjectEntity> findAllByUser(UserEntity user) {
        return projectRepository.findAllByUser(user);
    }

    public ProjectEntity findById(Long aLong) {
        return projectRepository.findById(aLong)
                .orElseThrow(() -> new NotFoundException("Project with id " + aLong + " did not found"));
    }

    public Optional<ProjectEntity> findByNameAndUser(String name, UserEntity user) {
        return projectRepository.findByNameAndUser(name, user);
    }

    @Transactional
    public <S extends ProjectEntity> S saveAndFlush(S entity) {

        for (TaskStateEntity el : entity.getTaskStateEntities()) {
            el.setProject(entity);
        }

        return projectRepository.saveAndFlush(entity);
    }

    @Transactional
    public <S extends ProjectEntity> S save(S entity) {
        return projectRepository.save(entity);
    }

    @Transactional
    public ProjectEntity update (Long id, ProjectEntity source){
        ProjectEntity project = findById(id);

        if (!Objects.equals(project.getUser().getId(), UserService.getCurrentUser().getId())){
            throw new ForbiddenException("FORBIDDEN");
        }

        project.setCreatedAt(source.getCreatedAt());
        project.setName(source.getName());
        return projectRepository.saveAndFlush(project);
    }

    @Transactional
    public void deleteById(Long aLong) {

        ProjectEntity project = findById(aLong);

        if (!Objects.equals(project.getUser().getId(), UserService.getCurrentUser().getId())){
            throw new ForbiddenException("FORBIDDEN");
        }

        projectRepository.deleteById(aLong);
    }
}
