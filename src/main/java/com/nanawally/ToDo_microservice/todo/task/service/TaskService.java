package com.nanawally.ToDo_microservice.todo.task.service;

import com.nanawally.ToDo_microservice.utility.advice.exception.TaskNotFoundException;
import com.nanawally.ToDo_microservice.todo.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.todo.deletedTask.repository.DeletedTaskRepository;
import com.nanawally.ToDo_microservice.todo.tag.Tag;
import com.nanawally.ToDo_microservice.todo.task.mapper.TaskMapper;
import com.nanawally.ToDo_microservice.todo.task.model.Task;
import com.nanawally.ToDo_microservice.todo.task.model.dto.TaskDTO;
import com.nanawally.ToDo_microservice.todo.task.repository.TaskRepository;
import com.nanawally.ToDo_microservice.utility.authorization.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final DeletedTaskRepository deletedTaskRepository;
    private final TaskMapper taskMapper;
    private final CurrentUser currentUser;

    @Autowired
    public TaskService(TaskRepository taskRepository, DeletedTaskRepository deletedTaskRepository, TaskMapper taskMapper, CurrentUser currentUser) {
        this.taskRepository = taskRepository;
        this.deletedTaskRepository = deletedTaskRepository;
        this.taskMapper = taskMapper;
        this.currentUser = currentUser;
    }

    // get - auto filtered
    public List<TaskDTO> findNotCompleted() {
        UUID userId = currentUser.getUserId();
        return taskRepository.findByCompletedFalseAndUserId(userId).stream().map(taskMapper::mapToTaskDTO).collect(Collectors.toList());
    }

    // get - all
    public List<TaskDTO> findAllTasks() {
        UUID userId = currentUser.getUserId();
        return taskRepository.findAllTaskByUserId(userId).stream().map(taskMapper::mapToTaskDTO).collect(Collectors.toList());
    }

    // get - single by name TODO - remove this one
    public Optional<TaskDTO> findTaskByName(String name) {
        UUID userId = currentUser.getUserId();
        Optional<Task> foundTask = taskRepository.findByNameIgnoreCaseAndUserId(name, userId);
        return foundTask.map(taskMapper::mapToTaskDTO);
    }

    // get - list of names containing search term
    public List<TaskDTO> findTasksByNamePartial(String name) {
        UUID userId = currentUser.getUserId();
        List<Task> foundTasks = taskRepository.findByNameContainingIgnoreCaseAndUserId(name, userId);
        return foundTasks.stream()
                .map(taskMapper::mapToTaskDTO)
                .collect(Collectors.toList());
    }

    // get - single by id
    public Optional<TaskDTO> findTaskById(UUID id) {
        UUID userId = currentUser.getUserId();
        Optional<Task> foundTask = taskRepository.findTaskByIdAndUserId(id, userId);
        if (foundTask.isEmpty()) {
            throw new TaskNotFoundException("Task with ID " + id + " not found");
        }
        return foundTask.map(taskMapper::mapToTaskDTO);
    }

    // get - by tags
    public List<TaskDTO> findTaskByTag(String tag) {
        UUID userId = currentUser.getUserId();
        return taskRepository.findByTagsContainingIgnoreCase(tag, userId, Tag.TaskType.ACTIVE).stream().map(taskMapper::mapToTaskDTO).collect(Collectors.toList());
    }

    // get - & sort by priority
    public List<TaskDTO> findTasksWithPriority() {
        UUID userId = currentUser.getUserId();
        return taskRepository.findByPriorityIsNotNullAndUserId(userId)
                .stream()
                .map(taskMapper::mapToTaskDTO)
                .sorted(Comparator.comparing(TaskDTO::priority).reversed())
                .toList();
    }

    // get - without prio
    public List<TaskDTO> findTaskWithoutPriority() {
        UUID userId = currentUser.getUserId();
        return taskRepository.findByPriorityIsNullAndUserId(userId).stream().map(taskMapper::mapToTaskDTO).toList();
    }

    // post - new task
    public TaskDTO saveNewTask(TaskDTO taskDTO, CurrentUser currentUser) {
        Task task = taskMapper.mapToTask(taskDTO, currentUser);
        taskRepository.save(task);
        log.info("Task with ID {} saved", task.getId());
        return taskDTO;
    }

    // patch - update task
    public TaskDTO updateTask(UUID id, TaskDTO taskDTO) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));

        if (taskDTO.name() != null) {
            existingTask.setName(taskDTO.name());
        }
        if (taskDTO.description() != null) {
            existingTask.setDescription(taskDTO.description());
        }
        if (taskDTO.completed() != null) {
            existingTask.setCompleted(taskDTO.completed());
        }
        if (taskDTO.tags() != null) {
            existingTask.setTags(updateTags(existingTask, taskDTO.tags()));
        }
        if (taskDTO.priority() != null) {
            existingTask.setPriority(taskDTO.priority());
        }

        Task updatedTask = taskRepository.save(existingTask);
        log.info("Updated Task with id {} saved", existingTask.getId());
        return taskMapper.mapToTaskDTO(updatedTask);
    }

    public List<Tag> updateTags(Task task, List<Tag> newTags) {
        // Remove tags that are no longer present
        task.getTags().removeIf(oldTag ->
                newTags.stream()
                        .noneMatch(newTag -> newTag.getTagName().equals(oldTag.getTagName()))
        );

        // Add new tags that don't exist yet
        for (Tag newTag : newTags) {
            boolean exists = task.getTags().stream()
                    .anyMatch(oldTag -> oldTag.getTagName().equals(newTag.getTagName()));
            if (!exists) {
                Tag tag = new Tag();
                tag.setTask(task);
                tag.setTagName(newTag.getTagName());
                tag.setTaskType(Tag.TaskType.ACTIVE);
                task.getTags().add(tag);
            }
        }

        return task.getTags();
    }

    // patch - set task to 'complete'
    public TaskDTO completeTask(UUID id) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));

        if (!existingTask.isCompleted()) {
            existingTask.setCompleted(true);
        }

        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.mapToTaskDTO(updatedTask);
    }

    @Transactional
    public boolean moveTaskToTrash(UUID taskID) {
        UUID userId = currentUser.getUserId();

        return taskRepository.findTaskByIdAndUserId(taskID, userId)
                .map(task -> {
                    DeletedTask deletedTask = new DeletedTask(
                            task.getId(),
                            task.getUserId(),
                            task.getName(),
                            task.getDescription(),
                            task.isCompleted(),
                            task.getPriority(),
                            new ArrayList<>()
                    );

                    List<Tag> deletedTags = task.getTags().stream()
                            .map(tag -> {
                                Tag t = new Tag();
                                t.setTagName(tag.getTagName());
                                t.setDeletedTask(deletedTask);
                                t.setTaskType(Tag.TaskType.DELETED);
                                return t;
                            }).collect(Collectors.toList());

                    deletedTask.setTags(deletedTags);
                    deletedTaskRepository.save(deletedTask);
                    taskRepository.delete(task);
                    log.info("Task with ID {} has been moved to Trashcan", taskID);
                    return true;

                })
                .orElseGet(() -> {
                    log.error("Migration failed - no task found for ID: {}", taskID);
                    return false;
                });
    }

    @Transactional
    public boolean moveAllCompletedToTrash() {
        UUID userId = currentUser.getUserId();
        List<Task> completedTasks = taskRepository.findByCompletedTrueAndUserId(userId);
        if (!completedTasks.isEmpty()) {
            for (Task completedTask : completedTasks) {
                moveTaskToTrash(completedTask.getId());
            }
            log.info("All Completed Tasks were successfully moved to Trashcan");
            return true;
        }
        log.error("Migration of Tasks failed");
        return false;
    }
}
