package com.nanawally.ToDo_microservice.todo.deletedTask.repository;

import com.nanawally.ToDo_microservice.todo.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.todo.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeletedTaskRepository extends JpaRepository<DeletedTask, UUID> {

    Optional<DeletedTask> findDeletedTaskByIdAndUserId(UUID taskId, UUID userId);

    List<DeletedTask> findAllDeletedTaskByUserId(UUID userId);

    @Query("SELECT d FROM DeletedTask d " +
            "JOIN d.tags tag " +
            "WHERE tag.tagName = :tagName " +
            "AND d.userId = :userId")
    List<DeletedTask> findByTag(@Param("tagName") String tagName, @Param("userId") UUID userId, Tag.TaskType taskType);
}
