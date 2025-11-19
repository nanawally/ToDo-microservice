CREATE TABLE tasks
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    completed   BOOLEAN NOT NULL,
    priority    VARCHAR(50)
);

-- For the tags list, since it's an ElementCollection, create a separate table
CREATE TABLE task_tags
(
    task_id UUID NOT NULL,
    tag     VARCHAR(255),
    CONSTRAINT fk_task
        FOREIGN KEY (task_id)
            REFERENCES tasks (id)
            ON DELETE CASCADE
);
