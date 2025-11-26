package com.nanawally.ToDo_microservice.todo.deletedTask.model;

import com.nanawally.ToDo_microservice.todo.priority.Priority;
import com.nanawally.ToDo_microservice.todo.tag.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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
    private UUID id;
    @Column(nullable = false)
    private UUID userId;
    private String name;
    private String description;
    private boolean completed;
    private Priority priority;
    @OneToMany(mappedBy = "deletedTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

}
