package com.nanawally.ToDo_microservice.deletedTask.service;

import com.nanawally.ToDo_microservice.deletedTask.mapper.DeletedTaskMapper;
import com.nanawally.ToDo_microservice.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.deletedTask.model.dto.DeletedTaskDTO;
import com.nanawally.ToDo_microservice.deletedTask.repository.DeletedTaskRepository;
import com.nanawally.ToDo_microservice.tag.Tag;
import com.nanawally.ToDo_microservice.task.model.Task;
import com.nanawally.ToDo_microservice.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeletedTaskService {

    private final DeletedTaskRepository deletedTaskRepository;
    private final TaskRepository taskRepository;
    private final DeletedTaskMapper deletedTaskMapper;

    @Autowired
    public DeletedTaskService(DeletedTaskRepository deletedTaskRepository, TaskRepository taskRepository, DeletedTaskMapper deletedTaskMapper) {
        this.deletedTaskRepository = deletedTaskRepository;
        this.taskRepository = taskRepository;
        this.deletedTaskMapper = deletedTaskMapper;
    }

    // GET - all
    public List<DeletedTaskDTO> findAllDeletedTasks() {
        return deletedTaskRepository.findAll().stream().map(deletedTaskMapper::mapToDeletedTaskDTO).collect(Collectors.toList());
    }

    // GET - filtered
    public List<DeletedTaskDTO> findDeletedTaskByTag(String tag) {
        return deletedTaskRepository.findByTag(tag).stream().map(deletedTaskMapper::mapToDeletedTaskDTO).collect(Collectors.toList());
    }

    @Transactional
    public boolean moveFromTrashToTasks(UUID taskID) {

        Optional<DeletedTask> foundTask = deletedTaskRepository.findById(taskID);

        if (foundTask.isEmpty()) {
            return false;
        }

        DeletedTask task = foundTask.get();

        Task restoredTask = new Task(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.isCompleted(),
                task.getPriority(),
                new ArrayList<>()
        );

        List<Tag> restoredTags = task.getTags().stream()
                .map(tag -> {
                    Tag t = new Tag();
                    t.setTagName(tag.getTagName());
                    t.setTask(restoredTask);
                    t.setTaskType(Tag.TaskType.ACTIVE);
                    return t;
                }).collect(Collectors.toList());

        restoredTask.setTags(restoredTags);

        taskRepository.save(restoredTask);

        deletedTaskRepository.delete(task);

        return true;
    }

    // DELETE - by id
    public boolean deleteTaskFromTrash(UUID id) {
        DeletedTask taskToDelete = deletedTaskRepository.findById(id).orElse(null);

        if (taskToDelete != null) {
            deletedTaskRepository.delete(taskToDelete);
            return true;
        } else {
            return false;
        }
    }

    // DELETE - all
    public boolean deleteAllTasks() {
        if (deletedTaskRepository.findAll().isEmpty()) {
            return false;
        } else {
            deletedTaskRepository.deleteAll();
            return true;
        }
    }
}
