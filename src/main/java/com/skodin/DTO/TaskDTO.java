package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skodin.models.TaskStateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskDTO {

    Long id;

    String name;

    @JsonProperty("created_at")
    @Builder.Default
    Instant createdAt = Instant.now();

    @Builder.Default
    @JsonProperty("is_done")
    Boolean isDone = false;

    String description;

    @JsonProperty("task_state_id")
    Long taskStateId;
}
