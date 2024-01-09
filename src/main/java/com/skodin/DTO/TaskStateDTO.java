package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(name = "id", example = "1")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @Schema(name = "name", example = "This is the name of task state")
    String name;

    @JsonProperty("previous_task_state_id")
    @Schema(name = "previous_task_state_id", example = "1")
    Long previousTaskStateId;

    @JsonProperty(value = "next_task_state_id", access = JsonProperty.Access.READ_ONLY)
    @Schema(name = "next_task_state_id", example = "1")
    Long nextTaskStateId;

    @Builder.Default
    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    @Schema(name = "created_at", example = "timestamp")
    Instant createdAt = Instant.now();

    @JsonProperty(value = "project_id", access = JsonProperty.Access.READ_ONLY)
    @Schema(name = "project_id", example = "1")
    Long projectId;

    @Builder.Default
    @JsonProperty(value = "task_entities", access = JsonProperty.Access.READ_ONLY)
    @Schema(name = "project_id", example = "[\n" +
                                           "    {\n" +
                                           "        \"id\": 27,\n" +
                                           "        \"name\": \"task1\",\n" +
                                           "        \"description\": \"description1\",\n" +
                                           "        \"created_at\": \"2023-10-24T17:26:05.902537Z\",\n" +
                                           "        \"task_state_id\": 146\n" +
                                           "    }\n" +
                                           "]")
    List<TaskDTO> taskEntities = new ArrayList<>();

}
