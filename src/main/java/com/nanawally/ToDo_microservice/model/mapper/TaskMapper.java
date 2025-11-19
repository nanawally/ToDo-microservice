package com.nanawally.ToDo_microservice.model.mapper;

import com.nanawally.ToDo_microservice.model.Task;
import com.nanawally.ToDo_microservice.model.dto.TaskDTO;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task mapToTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setName(taskDTO.name());
        task.setDescription(taskDTO.description());
        task.setCompleted(taskDTO.completed());
        task.setTags(taskDTO.tags());
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
