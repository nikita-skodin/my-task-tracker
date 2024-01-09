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
//@EqualsAndHashCode(exclude = {"taskStateEntities"})
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectDTO {

    @Schema(name = "id", example = "1")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @Schema(name = "name", example = "This is the name of project")
    String name;

    @Builder.Default
    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    @Schema(name = "created_at", example = "timestamp")
    Instant createdAt = Instant.now();

    @JsonProperty(value = "user_id", access = JsonProperty.Access.READ_ONLY)
    Long userId;

    @Builder.Default
    @JsonProperty(value = "task_state_entities", access = JsonProperty.Access.READ_ONLY)
    @Schema(name = "task_state_entities", example = "[\n" +
                                                    "    {\n" +
                                                    "        \"id\": 145,\n" +
                                                    "        \"name\": \"In progress\",\n" +
                                                    "        \"previous_task_state_id\": 144,\n" +
                                                    "        \"next_task_state_id\": 146,\n" +
                                                    "        \"created_at\": \"2023-10-24T17:16:43.498700Z\",\n" +
                                                    "        \"project_id\": 57,\n" +
                                                    "        \"task_entities\": []\n" +
                                                    "    }]")
    List<TaskStateDTO> taskStateEntities = new ArrayList<>();
}
