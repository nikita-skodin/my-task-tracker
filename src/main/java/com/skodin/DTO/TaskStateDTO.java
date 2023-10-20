package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateDTO {

    Long id;

    String name;

    Integer order;

    @Builder.Default
    @JsonProperty("created_at")
    Instant createdAt = Instant.now();

    @JsonProperty("project_id")
    Long projectId;

    @Builder.Default
    @JsonProperty("task_entities")
    List<TaskDTO> taskEntities = new ArrayList<>();

}
