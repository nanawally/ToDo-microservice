package com.nanawally.ToDo_microservice.todo.task.repository;

import com.nanawally.ToDo_microservice.todo.tag.Tag;
import com.nanawally.ToDo_microservice.todo.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllTaskByUserId(UUID userId);

    Optional<Task> findTaskByIdAndUserId(UUID taskId, UUID userId);

    Optional<Task> findByNameIgnoreCaseAndUserId(String name, UUID userId);

    List<Task> findByNameContainingIgnoreCaseAndUserId(String name,  UUID userId);

    List<Task> findByCompletedFalseAndUserId(UUID userId);

    List<Task> findByCompletedTrueAndUserId(UUID userId);

    @Query("SELECT t FROM Task t " +
            "JOIN t.tags tag " +
            "WHERE LOWER(tag.tagName) LIKE LOWER(CONCAT('%', :tagName, '%')) " +
            "AND t.userId = :userId")
    List<Task> findByTagsContainingIgnoreCase(@Param("tagName") String tagName,  @Param("userId") UUID userId, Tag.TaskType taskType);

    List<Task> findByPriorityIsNotNullAndUserId(UUID userId);

    List<Task> findByPriorityIsNullAndUserId(UUID userId);
}
