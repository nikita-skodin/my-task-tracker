package com.skodin.repositories;

import com.skodin.models.ProjectEntity;
import com.skodin.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findByNameAndUser(String name, UserEntity user);

    List<ProjectEntity> findAllByNameStartingWithAndUser(String name, UserEntity user);

    List<ProjectEntity> findAllByUser(UserEntity user);
}
