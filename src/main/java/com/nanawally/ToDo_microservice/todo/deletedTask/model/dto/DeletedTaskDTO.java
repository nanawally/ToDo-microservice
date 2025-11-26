package com.nanawally.ToDo_microservice.todo.deletedTask.model.dto;

import com.nanawally.ToDo_microservice.todo.priority.Priority;
import com.nanawally.ToDo_microservice.todo.tag.Tag;

import java.util.List;
import java.util.UUID;

public record DeletedTaskDTO(
        UUID id,
        UUID userId,
        String name,
        String description,
        boolean completed,
        List<Tag> tags,
        Priority priority
) {
}
