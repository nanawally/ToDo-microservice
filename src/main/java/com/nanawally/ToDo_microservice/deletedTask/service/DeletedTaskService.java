package com.nanawally.ToDo_microservice.deletedTask.service;

import com.nanawally.ToDo_microservice.deletedTask.mapper.DeletedTaskMapper;
import com.nanawally.ToDo_microservice.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.deletedTask.model.dto.DeletedTaskDTO;
import com.nanawally.ToDo_microservice.deletedTask.repository.DeletedTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeletedTaskService {

    private final DeletedTaskRepository deletedTaskRepository;
    private final DeletedTaskMapper deletedTaskMapper;

    @Autowired
    public DeletedTaskService(DeletedTaskRepository deletedTaskRepository, DeletedTaskMapper deletedTaskMapper) {
        this.deletedTaskRepository = deletedTaskRepository;
        this.deletedTaskMapper = deletedTaskMapper;
    }

    // GET - all
    public List<DeletedTaskDTO> findAllDeletedTasks(){
        return deletedTaskRepository.findAll().stream().map(deletedTaskMapper::mapToDeletedTaskDTO).collect(Collectors.toList());
    }

    // GET - filtered
    public List<DeletedTaskDTO> findDeletedTaskByTag(String tag){
        return deletedTaskRepository.findByTag(tag).stream().map(deletedTaskMapper::mapToDeletedTaskDTO).collect(Collectors.toList());
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
