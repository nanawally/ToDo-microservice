CREATE TABLE tasks
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    completed   BOOLEAN NOT NULL,
    priority    VARCHAR(50)
);

CREATE TABLE deleted_tasks
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    completed   BOOLEAN NOT NULL,
    priority    VARCHAR(50)
);

CREATE TABLE task_tags
(
    task_id UUID NOT NULL,
    tag VARCHAR(255),
    task_type VARCHAR(20) NOT NULL CHECK (task_type IN ('active', 'deleted'))
);

