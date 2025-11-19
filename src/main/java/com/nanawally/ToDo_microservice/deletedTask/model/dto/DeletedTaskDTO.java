package com.nanawally.ToDo_microservice.deletedTask.model.dto;

import com.nanawally.ToDo_microservice.priority.Priority;

import java.util.List;
import java.util.UUID;

public record DeletedTaskDTO(
        UUID id,
        String name,
        String description,
        boolean completed,
        List<String> tags,
        Priority priority
) {
}
