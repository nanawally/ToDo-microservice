package com.nanawally.ToDo_microservice.todo.deletedTask.service;

import com.nanawally.ToDo_microservice.todo.deletedTask.mapper.DeletedTaskMapper;
import com.nanawally.ToDo_microservice.todo.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.todo.deletedTask.model.dto.DeletedTaskDTO;
import com.nanawally.ToDo_microservice.todo.deletedTask.repository.DeletedTaskRepository;
import com.nanawally.ToDo_microservice.todo.tag.Tag;
import com.nanawally.ToDo_microservice.todo.task.model.Task;
import com.nanawally.ToDo_microservice.todo.task.repository.TaskRepository;
import com.nanawally.ToDo_microservice.utility.authorization.CurrentUser;
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
    private final CurrentUser currentUser;

    @Autowired
    public DeletedTaskService(DeletedTaskRepository deletedTaskRepository, TaskRepository taskRepository, DeletedTaskMapper deletedTaskMapper, CurrentUser currentUser) {
        this.deletedTaskRepository = deletedTaskRepository;
        this.taskRepository = taskRepository;
        this.deletedTaskMapper = deletedTaskMapper;
        this.currentUser = currentUser;
    }

    // GET - all
    public List<DeletedTaskDTO> findAllDeletedTasks() {
        UUID userId = currentUser.getUserId();
        return deletedTaskRepository.findAllDeletedTaskByUserId(userId).stream().map(deletedTaskMapper::mapToDeletedTaskDTO).collect(Collectors.toList());
    }

    // GET - filtered
    public List<DeletedTaskDTO> findDeletedTaskByTag(String tag) {
        UUID userId = currentUser.getUserId();
        return deletedTaskRepository.findByTag(tag, userId, Tag.TaskType.DELETED).stream().map(deletedTaskMapper::mapToDeletedTaskDTO).collect(Collectors.toList());
    }

    @Transactional
    public boolean moveFromTrashToTasks(UUID taskID) {
        UUID userId = currentUser.getUserId();
        Optional<DeletedTask> foundTask = deletedTaskRepository.findDeletedTaskByIdAndUserId(taskID, userId);

        if (foundTask.isEmpty()) {
            return false;
        }

        DeletedTask task = foundTask.get();

        Task restoredTask = new Task(
                task.getId(),
                task.getUserId(),
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
        UUID userId = currentUser.getUserId();
        DeletedTask taskToDelete = deletedTaskRepository.findDeletedTaskByIdAndUserId(id, userId).orElse(null);

        if (taskToDelete != null) {
            deletedTaskRepository.delete(taskToDelete);
            return true;
        } else {
            return false;
        }
    }

    // DELETE - all
    public boolean deleteAllTasks() {
        UUID userId = currentUser.getUserId();
        if (deletedTaskRepository.findAllDeletedTaskByUserId(userId).isEmpty()) {
            return false;
        } else {
            deletedTaskRepository.deleteAll();
            return true;
        }
    }
}
