package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"taskEntities"})
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    String name;

    @JsonProperty("previous_task_state_id")
    Long previousTaskStateId;

    @JsonProperty(value = "next_task_state_id", access = JsonProperty.Access.READ_ONLY)
    Long nextTaskStateId;

    @Builder.Default
    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    Instant createdAt = Instant.now();

    @JsonProperty(value = "project_id", access = JsonProperty.Access.READ_ONLY)
    Long projectId;

    @Builder.Default
    @JsonProperty(value = "task_entities", access = JsonProperty.Access.READ_ONLY)
    List<TaskDTO> taskEntities = new ArrayList<>();

}
