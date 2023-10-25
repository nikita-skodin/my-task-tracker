package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skodin.models.TaskStateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectDTO {

    Long id;

    String name;

    @Builder.Default
    @JsonProperty("created_at")
    Instant createdAt = Instant.now();

    @Builder.Default
    @JsonProperty("task_state_entities")
    List<TaskStateDTO> taskStateEntities = new ArrayList<>();
}
