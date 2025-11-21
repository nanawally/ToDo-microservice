package com.nanawally.ToDo_microservice.task.model;

import com.nanawally.ToDo_microservice.priority.Priority;
import com.nanawally.ToDo_microservice.tag.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
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
    private UUID id =  UUID.randomUUID();


    private String name;
    private String description;
    private boolean completed;


    private Priority priority;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();


}
