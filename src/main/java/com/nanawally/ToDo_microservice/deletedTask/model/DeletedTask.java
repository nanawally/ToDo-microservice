package com.nanawally.ToDo_microservice.deletedTask.model;

import com.nanawally.ToDo_microservice.priority.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "deleted_tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeletedTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // TODO - Add @Column annotation?
    private String name;
    private String description;
    private boolean completed;
    private List<String> tags;
    private Priority priority;
}
