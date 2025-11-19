package com.nanawally.ToDo_microservice.task.model;

import com.nanawally.ToDo_microservice.priority.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // TODO - Add @Column annotation?
    private String name;
    private String description;
    private boolean completed;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags;
    private Priority priority;

}
