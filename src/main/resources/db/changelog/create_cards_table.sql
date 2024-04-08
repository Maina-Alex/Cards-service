-- liquidbase formatted sql

-- changeset amaina:20240406-2
-- preconditions onFail:MARK_RAN onError:MARK_RAN
CREATE TABLE IF NOT EXISTS cards (
     id INTEGER AUTO_INCREMENT,
     description VARCHAR(400),
     name VARCHAR(255),
     color VARCHAR(7),
     created_date TIMESTAMP(6),
     status VARCHAR(15),
     app_user_id INTEGER,
     CONSTRAINT pk_card PRIMARY KEY (id),
     FOREIGN KEY (app_user_id) REFERENCES app_user(id)
);
