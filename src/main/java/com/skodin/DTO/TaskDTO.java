package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    String name;

    String description;

    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    Instant createdAt = Instant.now();

    @JsonProperty(value = "task_state_id", access = JsonProperty.Access.READ_ONLY)
    Long taskStateId;
}
