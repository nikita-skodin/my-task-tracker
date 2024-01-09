package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @Schema(name = "name", example = "This is the name of task")
    String name;

    @Schema(name = "description", example = "This is the description of task")
    String description;

    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    @Schema(name = "created_at", example = "timestamp")
    Instant createdAt = Instant.now();

    @JsonProperty(value = "task_state_id", access = JsonProperty.Access.READ_ONLY)
    @Schema(name = "task_state_id", example = "1")
    Long taskStateId;
}
