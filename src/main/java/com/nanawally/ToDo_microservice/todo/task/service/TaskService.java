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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

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
    /*
    public List<TaskDTO> findAllTasks() {
        UUID userId = currentUser.getUserId();
        return taskRepository.findByUserId(userId)
                .stream()
                .map(taskMapper::mapToTaskDTO)
                .toList();
    }*/

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

    // get - single by name
    public Optional<TaskDTO> findTaskByName(String name) {
        UUID userId = currentUser.getUserId();
        Optional<Task> foundTask = taskRepository.findByNameAndUserId(name, userId);
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
        return taskRepository.findByTag(tag, userId, Tag.TaskType.ACTIVE).stream().map(taskMapper::mapToTaskDTO).collect(Collectors.toList());
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
    public TaskDTO saveNewTask(TaskDTO taskDTO) {
        Task task = taskMapper.mapToTask(taskDTO);
        taskRepository.save(task);
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
        if (taskDTO.completed()) {
            existingTask.setCompleted(taskDTO.completed());
        }
        if (taskDTO.tags() != null) {
            existingTask.setTags(taskDTO.tags());
        }
        if (taskDTO.priority() != null) {
            existingTask.setPriority(taskDTO.priority());
        }

        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.mapToTaskDTO(updatedTask);
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

        Optional<Task> foundTask = taskRepository.findById(taskID);
        if (foundTask.isEmpty()) {
            return false;
        }

        Task task = foundTask.get();

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

        return true;
    }

    @Transactional
    public boolean moveAllCompletedToTrash() {
        UUID userId = currentUser.getUserId();
        List<Task> completedTasks = taskRepository.findByCompletedTrueAndUserId(userId);
        if (!completedTasks.isEmpty()) {
            for (Task completedTask : completedTasks) {
                moveTaskToTrash(completedTask.getId());
            }
            return true;
        }
        return false;
    }

    // TODO - getMostUsedTags() implementation
}
