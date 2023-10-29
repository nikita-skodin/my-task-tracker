package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skodin.models.TaskStateEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
@EqualsAndHashCode(exclude = {"taskStateEntities"})
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectDTO {

    @Schema(name = "id", example = "1")
    Long id;

    @Schema(name = "name", example = "This is the name of project")
    String name;

    @Builder.Default
    @JsonProperty("created_at")
    @Schema(name = "created_at", example = "timestamp")
    Instant createdAt = Instant.now();

    @Builder.Default
    @JsonProperty("task_state_entities")
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
