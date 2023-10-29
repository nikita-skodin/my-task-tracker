package com.skodin.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorDTO {

    @Schema(name = "Error cause", example = "404")
    String error;

    @Schema(name = "Error message", example = "Not found")
    String message;
}
