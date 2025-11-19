package com.nanawally.ToDo_microservice.deletedTask.repository;

import com.nanawally.ToDo_microservice.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeletedTaskRepository extends JpaRepository<DeletedTask, UUID> {

    @Query("SELECT d FROM DeletedTask d JOIN d.tags tag WHERE tag = :tag")
    List<DeletedTask> findByTag(@Param("tag") String tag);
}
