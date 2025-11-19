package com.nanawally.ToDo_microservice.deletedTask.repository;

import com.nanawally.ToDo_microservice.deletedTask.model.DeletedTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeletedTaskRepository extends JpaRepository<DeletedTask, UUID> {
}
