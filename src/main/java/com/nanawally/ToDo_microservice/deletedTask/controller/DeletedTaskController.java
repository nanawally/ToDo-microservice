package com.nanawally.ToDo_microservice.deletedTask.controller;

import com.nanawally.ToDo_microservice.deletedTask.model.dto.DeletedTaskDTO;
import com.nanawally.ToDo_microservice.deletedTask.service.DeletedTaskService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/trashcan")
public class DeletedTaskController {

    private final DeletedTaskService deletedTaskService;

    public DeletedTaskController(DeletedTaskService deletedTaskService) {
        this.deletedTaskService = deletedTaskService;
    }

    // GET - all
    @GetMapping("/")
    @RateLimiter(name = "myRateLimiter")
    public ResponseEntity<List<DeletedTaskDTO>> getAllTrash() {
        List<DeletedTaskDTO> deletedTaskDTOList = deletedTaskService.findAllDeletedTasks();
        if (deletedTaskDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(deletedTaskDTOList);
    }

    // GET - filtered by tags
    @GetMapping("/tag/{tags}")
    @RateLimiter(name = "myRateLimiter")
    public ResponseEntity<List<DeletedTaskDTO>> findByTags(@PathVariable String tags) {
        List<DeletedTaskDTO> deletedTaskByTags = deletedTaskService.findDeletedTaskByTag(tags);
        if (deletedTaskByTags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(deletedTaskByTags);
    }

    // DELETE - by id
    @DeleteMapping("/delete/{id}")
    @RateLimiter(name = "myRateLimiter")
    public ResponseEntity<DeletedTaskDTO> deleteSingleTask(@PathVariable UUID id) {
        if (deletedTaskService.deleteTaskFromTrash(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - all
    @DeleteMapping("/delete/all")
    @RateLimiter(name = "myRateLimiter")
    public ResponseEntity<DeletedTaskDTO> deleteAllTasks() {
        if (deletedTaskService.deleteAllTasks()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
