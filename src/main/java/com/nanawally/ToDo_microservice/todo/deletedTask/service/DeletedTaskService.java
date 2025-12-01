package com.nanawally.ToDo_microservice.todo.deletedTask.service;

import com.nanawally.ToDo_microservice.todo.deletedTask.mapper.DeletedTaskMapper;
import com.nanawally.ToDo_microservice.todo.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.todo.deletedTask.model.dto.DeletedTaskDTO;
import com.nanawally.ToDo_microservice.todo.deletedTask.repository.DeletedTaskRepository;
import com.nanawally.ToDo_microservice.todo.tag.Tag;
import com.nanawally.ToDo_microservice.todo.task.model.Task;
import com.nanawally.ToDo_microservice.todo.task.repository.TaskRepository;
import com.nanawally.ToDo_microservice.utility.authorization.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(DeletedTaskService.class);

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
        return deletedTaskRepository.findAllDeletedTaskByUserId(userId).stream()
                .map(deletedTaskMapper::mapToDeletedTaskDTO)
                .collect(Collectors.toList());
    }

    // GET - filtered
    public List<DeletedTaskDTO> findDeletedTaskByTag(String tag) {
        UUID userId = currentUser.getUserId();
        return deletedTaskRepository.findByTag(tag, userId, Tag.TaskType.DELETED).stream()
                .map(deletedTaskMapper::mapToDeletedTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean moveFromTrashToTasks(UUID taskID) {
        UUID userId = currentUser.getUserId();
        return deletedTaskRepository
                .findDeletedTaskByIdAndUserId(taskID, userId)
                .map(task -> {
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
                    log.info("Task with id: {} has been moved successfully", task.getId());
                    return true;
                })
                .orElseGet(() -> {
                    log.error("Restore failed - no deleted task found for ID: {}", taskID);
                    return false;
                });
    }

    // DELETE - by id
    public boolean deleteTaskFromTrash(UUID id) {
        UUID userId = currentUser.getUserId();
        Optional<DeletedTask> taskToDelete = deletedTaskRepository.findDeletedTaskByIdAndUserId(id, userId);

        if (taskToDelete.isPresent()) {
            deletedTaskRepository.delete(taskToDelete.get());

            log.info("Task with id: {} has been deleted successfully", id);
            return true;
        }

        return false;
    }

    // DELETE - all
    public boolean deleteAllTasks() {
        UUID userId = currentUser.getUserId();
        if (deletedTaskRepository.findAllDeletedTaskByUserId(userId).isEmpty()) {
            log.warn("No tasks found");
            return false;
        } else {
            deletedTaskRepository.deleteAll();
            log.info("Deleted all tasks");
            return true;
        }
    }
}
