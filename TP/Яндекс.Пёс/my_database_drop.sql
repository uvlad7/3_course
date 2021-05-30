-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2020-05-30 17:35:17.189

-- foreign keys
ALTER TABLE dog
    DROP FOREIGN KEY dog_owner;

ALTER TABLE dog_task
    DROP FOREIGN KEY dog_task_dog;

ALTER TABLE dog_task
    DROP FOREIGN KEY dog_task_task;

ALTER TABLE owner
    DROP FOREIGN KEY owner_user;

ALTER TABLE performer
    DROP FOREIGN KEY performer_user;

ALTER TABLE response
    DROP FOREIGN KEY request_performer;

ALTER TABLE response
    DROP FOREIGN KEY request_task;

ALTER TABLE review
    DROP FOREIGN KEY review_owner;

ALTER TABLE review
    DROP FOREIGN KEY review_performer;

ALTER TABLE task
    DROP FOREIGN KEY task_owner;

-- tables
DROP TABLE dog;

DROP TABLE dog_task;

DROP TABLE owner;

DROP TABLE performer;

DROP TABLE response;

DROP TABLE review;

DROP TABLE task;

DROP TABLE user;

-- End of file.

