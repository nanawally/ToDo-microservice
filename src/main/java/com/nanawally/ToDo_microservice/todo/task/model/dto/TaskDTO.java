package com.nanawally.ToDo_microservice.todo.task.model.dto;

import com.nanawally.ToDo_microservice.todo.priority.Priority;
import com.nanawally.ToDo_microservice.todo.tag.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record TaskDTO(
        UUID id,
        UUID userId,
        @NotBlank
        @Size(min = 1, max = 50)
        String name,
        @Size(min = 1, max = 500)
        String description,
        @NotNull
        Boolean completed,
        List<Tag> tags,
        Priority priority
) {}
