package com.skodin.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"taskStateEntities"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "name should not be empty")
    @Size(min = 3, max = 20,
            message = "name`s length should be between 3 and 20 chars")
    String name;

    @Builder.Default
    Instant createdAt = Instant.now();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<TaskStateEntity> taskStateEntities = new ArrayList<>();

    public void addProjectEntities(TaskStateEntity ... entities){
        taskStateEntities.addAll(Arrays.asList(entities));
    }
}
