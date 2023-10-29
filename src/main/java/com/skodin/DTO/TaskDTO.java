package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skodin.models.TaskStateEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
@ToString
@EqualsAndHashCode()
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskDTO {

    @Schema(name = "id", example = "1")
    Long id;

    @Schema(name = "name", example = "This is the name of task")
    String name;

    @Schema(name = "description", example = "This is the description of task")
    String description;

    @JsonProperty("created_at")
    @Builder.Default
    @Schema(name = "created_at", example = "timestamp")
    Instant createdAt = Instant.now();

    @JsonProperty("task_state_id")
    @Schema(name = "task_state_id", example = "1")
    Long taskStateId;
}
