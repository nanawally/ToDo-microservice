package com.nanawally.ToDo_microservice.task.model.dto;

import com.nanawally.ToDo_microservice.priority.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record TaskDTO(
        UUID id,
        @NotBlank
        @Size(min = 1, max = 50)
        String name,
        @Size(min = 1, max = 500)
        String description,
        @NotNull
        boolean completed,
        List<String> tags,
        Priority priority
) {}
