package com.skodin.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString(exclude = {"taskEntities", "project"})
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "task-state")
public class TaskStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "name should not be empty")
    @Size(min = 3, max = 20,
            message = "name`s length should be between 3 and 20 chars")
    String name;

    @NotNull(message = "order should not be empty")
    @Column(name = "\"order\"")
    // TODO: 019  добавить проверку на уникальность номера учитывая проект
    Integer order;

    @Builder.Default
    Instant createdAt = Instant.now();

    @ManyToOne()
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    ProjectEntity project;

    @Builder.Default
    @OneToMany(mappedBy = "taskStateEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskEntity> taskEntities = new ArrayList<>();
}
