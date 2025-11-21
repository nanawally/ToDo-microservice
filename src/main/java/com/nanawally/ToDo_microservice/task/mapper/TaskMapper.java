package com.nanawally.ToDo_microservice.task.mapper;

import com.nanawally.ToDo_microservice.tag.Tag;
import com.nanawally.ToDo_microservice.task.model.Task;
import com.nanawally.ToDo_microservice.task.model.dto.TaskDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {
    public Task mapToTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setName(taskDTO.name());
        task.setDescription(taskDTO.description());
        task.setCompleted(taskDTO.completed());
        // task.setTags(taskDTO.tags());

        List<Tag> tagEntities = taskDTO.tags().stream()
                .map(tagDTO -> new Tag(task, tagDTO.getTag(), Tag.TaskType.ACTIVE))
                .collect(Collectors.toList());

        task.setTags(tagEntities);

        task.setPriority(taskDTO.priority());
        return task;
    }

    public TaskDTO mapToTaskDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.isCompleted(),
                task.getTags(),
                task.getPriority()
        );
    }
}
