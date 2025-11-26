package com.nanawally.ToDo_microservice.todo.deletedTask.mapper;

import com.nanawally.ToDo_microservice.todo.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.todo.deletedTask.model.dto.DeletedTaskDTO;
import org.springframework.stereotype.Component;

@Component
public class DeletedTaskMapper {

    public DeletedTask mapToDeletedTask(DeletedTaskDTO deletedTaskDTO) {
        DeletedTask deletedTask = new DeletedTask();
        deletedTask.setName(deletedTaskDTO.name());
        deletedTask.setDescription(deletedTaskDTO.description());
        deletedTask.setCompleted(deletedTaskDTO.completed());
        deletedTask.setTags(deletedTaskDTO.tags());
        deletedTask.setPriority(deletedTaskDTO.priority());
        return deletedTask;
    }

    public DeletedTaskDTO mapToDeletedTaskDTO(DeletedTask deletedTask) {
        return new DeletedTaskDTO(
                deletedTask.getId(),
                deletedTask.getUserId(),
                deletedTask.getName(),
                deletedTask.getDescription(),
                deletedTask.isCompleted(),
                deletedTask.getTags(),
                deletedTask.getPriority()
        );
    }
}
