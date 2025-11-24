package com.nanawally.ToDo_microservice.task.repository;

import com.nanawally.ToDo_microservice.tag.Tag;
import com.nanawally.ToDo_microservice.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Optional<Task> findByName(String name);

    List<Task> findByNameContainingIgnoreCase(String name);

    List<Task> findByCompletedFalse();

    List<Task> findByCompletedTrue();

    @Query("SELECT t FROM Task t " +
            "JOIN t.tags tag " +
            "WHERE tag.tagName = :tagName")
    List<Task> findByTag(@Param("tagName") String tagName, Tag.TaskType taskType);

    List<Task> findByPriorityIsNotNull();

    List<Task> findByPriorityIsNull();
}
