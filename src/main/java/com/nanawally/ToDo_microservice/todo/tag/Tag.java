package com.nanawally.ToDo_microservice.todo.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nanawally.ToDo_microservice.todo.deletedTask.model.DeletedTask;
import com.nanawally.ToDo_microservice.todo.task.model.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "task_tags")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore
    private Task task; // for active tasks

    @ManyToOne
    @JoinColumn(name = "deleted_task_id")
    @JsonIgnore
    private DeletedTask deletedTask; // for deleted tasks

    @Column(name = "tag", nullable = false)
    private String tagName;

    @Column(name = "task_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    public enum TaskType {
        ACTIVE,
        DELETED
    }

    // convenience const.
    public Tag(Task task, String tagName, TaskType type) {
        this.task = task;
        this.tagName = tagName;
        this.taskType = type;
    }
}
