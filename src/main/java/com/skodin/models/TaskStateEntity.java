package com.skodin.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
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

    @ManyToOne
    @JoinColumn(name = "previous_task_state_id")
    TaskStateEntity previousTaskState;

    @ManyToOne
    @JoinColumn(name = "next_task_state_id")
    TaskStateEntity nextTaskState;

    @Builder.Default
    Instant createdAt = Instant.now();

    @ManyToOne()
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    ProjectEntity project;

    @Builder.Default
    @OneToMany(mappedBy = "taskStateEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskEntity> taskEntities = new ArrayList<>();

    public Optional<TaskStateEntity> getPreviousTaskState() {
        return Optional.ofNullable(previousTaskState);
    }

    public Optional<TaskStateEntity> getNextTaskState() {
        return Optional.ofNullable(nextTaskState);
    }

    @Override
    public String toString() {
        return "TaskStateEntity{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", previousTaskStateId=" + (previousTaskState == null ? null : previousTaskState.getId()) +
               ", nextTaskStateId=" + (nextTaskState == null ? null : nextTaskState.getId()) +
               ", createdAt=" + createdAt +
               ", taskEntities=" + taskEntities +
               '}';
    }
}
