CREATE TABLE tasks
(
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,
    name        VARCHAR(255),
    description TEXT,
    completed   BOOLEAN NOT NULL,
    priority    VARCHAR(50)
);

CREATE TABLE deleted_tasks
(
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,
    name        VARCHAR(255),
    description TEXT,
    completed   BOOLEAN NOT NULL,
    priority    VARCHAR(50)
);

CREATE TABLE task_tags
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    task_id UUID,
    deleted_task_id UUID,

    tag VARCHAR(255) NOT NULL,

    task_type VARCHAR(20) NOT NULL CHECK (task_type IN ('ACTIVE', 'DELETED')),

    CONSTRAINT fk_task
        FOREIGN KEY (task_id)
            REFERENCES tasks(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_deleted_task
        FOREIGN KEY (deleted_task_id)
            REFERENCES deleted_tasks(id)
            ON DELETE CASCADE
);
