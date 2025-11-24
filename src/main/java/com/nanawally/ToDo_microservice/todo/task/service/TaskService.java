package com.nanawally.ToDo_microservice.todo.task.service;

import com.nanawally.ToDo_microservice.utility.advice.exception.TaskNotFoundException;
import com.nanawally.ToDo_microservice.todo.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.todo.deletedTask.repository.DeletedTaskRepository;
import com.nanawally.ToDo_microservice.todo.tag.Tag;
import com.nanawally.ToDo_microservice.todo.task.mapper.TaskMapper;
import com.nanawally.ToDo_microservice.todo.task.model.Task;
import com.nanawally.ToDo_microservice.todo.task.model.dto.TaskDTO;
import com.nanawally.ToDo_microservice.todo.task.repository.TaskRepository;
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

    @Autowired
    public TaskService(TaskRepository taskRepository, DeletedTaskRepository deletedTaskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.deletedTaskRepository = deletedTaskRepository;
        this.taskMapper = taskMapper;
    }

    // get - auto filtered
    public List<TaskDTO> findNotCompleted() {
        return taskRepository.findByCompletedFalse().stream().map(taskMapper::mapToTaskDTO).collect(Collectors.toList());
    }

    // get - all
    public List<TaskDTO> findAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::mapToTaskDTO).collect(Collectors.toList());
    }

    // get - single by name
    public Optional<TaskDTO> findTaskByName(String name) {
        Optional<Task> foundTask = taskRepository.findByName(name);
        return foundTask.map(taskMapper::mapToTaskDTO);
    }

    // get - list of names containing search term
    public List<TaskDTO> findTasksByNamePartial(String name) {
        List<Task> foundTasks = taskRepository.findByNameContainingIgnoreCase(name);
        return foundTasks.stream()
                .map(taskMapper::mapToTaskDTO)
                .collect(Collectors.toList());
    }

    // get - single by id
    public Optional<TaskDTO> findTaskById(UUID id) {
        Optional<Task> foundTask = taskRepository.findById(id);
        if (foundTask.isEmpty()) {
            throw new TaskNotFoundException("Task with ID " + id + " not found");
        }
        return foundTask.map(taskMapper::mapToTaskDTO);
    }

    // get - by tags
    public List<TaskDTO> findTaskByTag(String tag) {
        return taskRepository.findByTag(tag, Tag.TaskType.ACTIVE).stream().map(taskMapper::mapToTaskDTO).collect(Collectors.toList());
    }

    // get - & sort by priority
    public List<TaskDTO> findTasksWithPriority() {
        return taskRepository.findByPriorityIsNotNull()
                .stream()
                .map(taskMapper::mapToTaskDTO)
                .sorted(Comparator.comparing(TaskDTO::priority).reversed())
                .toList();
    }

    // get - without prio
    public List<TaskDTO> findTaskWithoutPriority() {
        return taskRepository.findByPriorityIsNull().stream().map(taskMapper::mapToTaskDTO).toList();
    }

    // post - new task
    public TaskDTO saveNewTask(TaskDTO taskDTO) {
        Task task = taskMapper.mapToTask(taskDTO);
        taskRepository.save(task);
        return taskDTO;
    }

    // TODO - Is there a reason we don't call findById Optional in these patches?
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
        List<Task> completedTasks = taskRepository.findByCompletedTrue();
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
