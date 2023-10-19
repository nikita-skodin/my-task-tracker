package com.skodin.repositories;

import com.skodin.models.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    List<ProjectEntity> findAllByNameStartingWith (String prefix);

    Optional<ProjectEntity> findByName(String name);

}
