package com.skodin.services.cache;

import com.skodin.exceptions.NotFoundException;
import com.skodin.models.ProjectEntity;
import com.skodin.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceCache {
    private final ProjectRepository projectRepository;

    @SneakyThrows
    @Cacheable("ProjectService::findById")
    public ProjectEntity findById(Long id) {
        System.err.println("ProjectService::findById IS WORKING");
        Thread.sleep(3000);
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project with id " + id + " did not found"));
    }

}
