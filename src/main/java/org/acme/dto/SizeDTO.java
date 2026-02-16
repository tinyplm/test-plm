package org.acme.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

public class SizeDTO {
    public record Create(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Sizes value is required") String sizes
    ) {}

    public record Update(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Sizes value is required") String sizes,
        long version
    ) {}

    public record Response(
        UUID id,
        long version,
        String name,
        String sizes,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
    ) {}
}
