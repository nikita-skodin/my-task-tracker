package com.skodin.services;

import com.skodin.exceptions.NotFoundException;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskStateEntity;
import com.skodin.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectEntity> findAll() {
        return projectRepository.findAll();
    }

    public List<ProjectEntity> findAllByNameStartingWith(String prefix) {
        return projectRepository.findAllByNameStartingWith(prefix);
    }

    public ProjectEntity findById(Long aLong) {
        return projectRepository.findById(aLong)
                .orElseThrow(() -> new NotFoundException("Project with id: \"" + aLong + "\" is not found"));
    }

    public Optional<ProjectEntity> findByName(String name) {
        return projectRepository.findByName(name);
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
    public void update (Long id, ProjectEntity source){
        ProjectEntity byId = findById(id);
        source.setId(byId.getId());
        projectRepository.save(source);
    }

    @Transactional
    public void deleteById(Long aLong) {

        findById(aLong);    // just check does object exist

        projectRepository.deleteById(aLong);
    }
}
