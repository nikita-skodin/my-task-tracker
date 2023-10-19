package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skodin.models.ProjectEntity;
import com.skodin.models.TaskEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateDTO {

    Long id;

    @NotBlank(message = "name should not be empty")
    @Size(min = 3, max = 20,
            message = "name`s length should be between 3 and 20 chars")
    String name;

    @NotNull(message = "order should not be empty")
    // TODO: 019  добавить проверку на уникальность номера
    Integer order;

    @Builder.Default
    Instant createdAt = Instant.now();

    @JsonProperty("project_id")
    Long projectId;

    // TODO: 019 доделать по аналогии
//    @Builder.Default
//    @OneToMany(mappedBy = "taskStateEntity")
//    List<TaskEntity> taskEntities = new ArrayList<>();

}
