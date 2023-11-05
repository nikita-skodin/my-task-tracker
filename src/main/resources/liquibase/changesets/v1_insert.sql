-- Заполнение таблицы "user"
INSERT INTO "user" (username, password, email, role)
VALUES
    ('user1', 'password1', 'user1@example.com', 'ROLE_USER'),
    ('user2', 'password2', 'user2@example.com', 'ROLE_USER');

-- Заполнение таблицы "project"
INSERT INTO "project" (name, created_at, user_id)
VALUES
    ('Project 1', current_timestamp, 1),
    ('Project 2', current_timestamp, 2);

-- Заполнение таблицы "task-state"
INSERT INTO "task-state" (name, created_at, project_id, next_task_state_id, previous_task_state_id)
VALUES
    ('State 1', current_timestamp, 1, 2, NULL),
    ('State 2', current_timestamp, 1, NULL, 1),
    ('State 3', current_timestamp, 2, NULL, NULL);

-- Заполнение таблицы "task"
INSERT INTO "task" (name, created_at, description, task_state_id)
VALUES
    ('Task 1', current_timestamp, 'Description 1', 1),
    ('Task 2', current_timestamp, 'Description 2', 2);
