-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2020-05-30 17:35:17.189

-- tables
-- Table: dog
CREATE TABLE dog (
    id int unsigned NOT NULL AUTO_INCREMENT,
    owner_id int unsigned NOT NULL,
    category char(32) NOT NULL,
    name char(25) NOT NULL,
    about varchar(512) NULL,
    photo blob NULL,
    CONSTRAINT dog_pk PRIMARY KEY (id)
);

-- Table: dog_task
CREATE TABLE dog_task (
    dog_id int unsigned NOT NULL,
    task_id int unsigned NOT NULL,
    id int unsigned NOT NULL AUTO_INCREMENT,
    CONSTRAINT dog_task_pk PRIMARY KEY (id)
);

-- Table: owner
CREATE TABLE owner (
    id int unsigned NOT NULL AUTO_INCREMENT,
    user_id int unsigned NOT NULL,
    sum_rating int unsigned NOT NULL DEFAULT 0,
    review_number int unsigned NOT NULL DEFAULT 0,
    UNIQUE INDEX unique_owner (user_id),
    CONSTRAINT owner_pk PRIMARY KEY (id)
);

-- Table: performer
CREATE TABLE performer (
    id int unsigned NOT NULL AUTO_INCREMENT,
    user_id int unsigned NOT NULL,
    sum_rating int unsigned NOT NULL DEFAULT 0,
    review_number int unsigned NOT NULL DEFAULT 0,
    UNIQUE INDEX unique_performer (user_id),
    CONSTRAINT performer_pk PRIMARY KEY (id)
);

-- Table: response
CREATE TABLE response (
    id int unsigned NOT NULL AUTO_INCREMENT,
    performer_id int unsigned NULL,
    task_id int unsigned NULL,
    preferred_cost double NULL,
    CONSTRAINT response_pk PRIMARY KEY (id)
);

-- Table: review
CREATE TABLE review (
    id int unsigned NOT NULL AUTO_INCREMENT,
    owner_id int unsigned NULL,
    performer_id int unsigned NULL,
    rate real(1,0) NOT NULL,
    explanation varchar(512) NULL,
    type binary(1) NOT NULL COMMENT '0 means review from owner
1 - from performer',
    CONSTRAINT review_pk PRIMARY KEY (id)
);

-- Table: task
CREATE TABLE task (
    id int unsigned NOT NULL AUTO_INCREMENT,
    owner_id int unsigned NULL,
    start_time date NOT NULL,
    duration time NOT NULL,
    preferred_cost double NULL,
    CONSTRAINT task_pk PRIMARY KEY (id)
);

-- Table: user
CREATE TABLE user (
    id int unsigned NOT NULL AUTO_INCREMENT,
    login char(50) NOT NULL,
    pass_bcrypt int NOT NULL,
    name char(50) NOT NULL,
    surname char(50) NOT NULL,
    email char(255) NOT NULL,
    phone char(25) NOT NULL,
    about varchar(1024) NULL DEFAULT '',
    photo blob NULL,
    geo json NOT NULL,
    role int NOT NULL DEFAULT 0 COMMENT 'owner, performer, both, admin',
    UNIQUE INDEX login_unique (login),
    UNIQUE INDEX email_unique (email),
    UNIQUE INDEX phone_unique (phone),
    CONSTRAINT user_pk PRIMARY KEY (id)
);

-- foreign keys
-- Reference: dog_owner (table: dog)
ALTER TABLE dog ADD CONSTRAINT dog_owner FOREIGN KEY dog_owner (owner_id)
    REFERENCES owner (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: dog_task_dog (table: dog_task)
ALTER TABLE dog_task ADD CONSTRAINT dog_task_dog FOREIGN KEY dog_task_dog (dog_id)
    REFERENCES dog (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: dog_task_task (table: dog_task)
ALTER TABLE dog_task ADD CONSTRAINT dog_task_task FOREIGN KEY dog_task_task (task_id)
    REFERENCES task (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: owner_user (table: owner)
ALTER TABLE owner ADD CONSTRAINT owner_user FOREIGN KEY owner_user (user_id)
    REFERENCES user (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: performer_user (table: performer)
ALTER TABLE performer ADD CONSTRAINT performer_user FOREIGN KEY performer_user (user_id)
    REFERENCES user (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: request_performer (table: response)
ALTER TABLE response ADD CONSTRAINT request_performer FOREIGN KEY request_performer (performer_id)
    REFERENCES performer (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Reference: request_task (table: response)
ALTER TABLE response ADD CONSTRAINT request_task FOREIGN KEY request_task (task_id)
    REFERENCES task (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Reference: review_owner (table: review)
ALTER TABLE review ADD CONSTRAINT review_owner FOREIGN KEY review_owner (owner_id)
    REFERENCES owner (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Reference: review_performer (table: review)
ALTER TABLE review ADD CONSTRAINT review_performer FOREIGN KEY review_performer (performer_id)
    REFERENCES performer (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- Reference: task_owner (table: task)
ALTER TABLE task ADD CONSTRAINT task_owner FOREIGN KEY task_owner (owner_id)
    REFERENCES owner (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- End of file.

