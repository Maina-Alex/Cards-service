-- liquidbase formatted sql

-- changeset amaina:20240406-1
-- preconditions onFail:MARK_RAN onError:MARK_RAN
CREATE TABLE IF NOT EXISTS app_user (
    id INTEGER AUTO_INCREMENT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(150),
    email VARCHAR(40),
    phone VARCHAR(40),
    address VARCHAR(40),
    account_active BOOLEAN NOT NULL,
    role VARCHAR(10),
    created_date TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_app_user PRIMARY KEY (id)
    );
