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
//@EqualsAndHashCode(exclude = {"taskStateEntities"})
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    String name;

    @Builder.Default
    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    Instant createdAt = Instant.now();

    @JsonProperty(value = "user_id", access = JsonProperty.Access.READ_ONLY)
    Long userId;

    @Builder.Default
    @JsonProperty(value = "task_state_entities", access = JsonProperty.Access.READ_ONLY)
    List<TaskStateDTO> taskStateEntities = new ArrayList<>();
}
