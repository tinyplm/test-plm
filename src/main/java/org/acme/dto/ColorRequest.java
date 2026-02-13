package org.acme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ColorRequest(
        @NotBlank String name,
        String description,
        @NotBlank String rgb
) {}
