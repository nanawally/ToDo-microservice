package com.nanawally.ToDo_microservice.task.repository;

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

    // @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    // List<Task> findByNameRegex(String name);

    List<Task> findByNameContainingIgnoreCase(String name);

    // @Query("{'completed': false}")
    // List<Task> findByNotCompleted();

    List<Task> findByCompletedFalse();

    // @Query("{'completed': true}")
    // List<Task> findByCompleted();

    List<Task> findByCompletedTrue();

    // @Query("{'tags': ?0}")
    // List<Task> findByTags(String tags);

    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE tag = :tag")
    List<Task> findByTag(@Param("tag") String tag);

    // @Query("{ 'priority': { '$exists': true } }")
    // List<Task> findTasksWithPriority();


    List<Task> findByPriorityIsNotNull();

    // @Query("{ 'priority': { '$exists': false } }")
    // List<Task> findTasksWithoutPriority();
    List<Task> findByPriorityIsNull();
}
