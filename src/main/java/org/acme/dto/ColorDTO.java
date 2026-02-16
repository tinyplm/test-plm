package org.acme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

public class ColorDTO {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Create(
        @NotBlank String name,
        String description,
        @NotBlank String rgb
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Update(
        @NotBlank String name,
        String description,
        @NotBlank String rgb,
        long version
    ) {}

    public record Response(
        UUID id,
        long version,
        String name,
        String description,
        String rgb,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
    ) {}
}
