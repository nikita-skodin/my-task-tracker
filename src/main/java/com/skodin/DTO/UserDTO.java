package com.skodin.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode()
@ToString(exclude = "projects")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    List<ProjectDTO> projects;
}
