package com.nanawally.ToDo_microservice.task.model;

import com.nanawally.ToDo_microservice.priority.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

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


    private String name;
    private String description;
    private boolean completed;


    private Priority priority;
    @ElementCollection
    @CollectionTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id")
    )
    @SQLRestriction("task_type = 'active'")
    @SQLInsert(sql =
            "INSERT INTO task_tags (task_id, tag, task_type) VALUES (?, ?, 'active')"
    )
    @Column(name = "tag")
    private List<String> tags;


}
