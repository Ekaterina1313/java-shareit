drop table if exists comments;
drop table if exists booking;
drop table if exists items;
drop table if exists item_request;
drop table if exists users;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_request
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(1000)                           NOT NULL,
    requestor_id BIGINT,
    created      TIMESTAMP,
    CONSTRAINT pk_item_request PRIMARY KEY (id),
    CONSTRAINT fk_item_request_requestor FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(1000),
    available   BOOLEAN,
    owner_id    BIGINT,
    request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_owner FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT fk_request_id FOREIGN KEY (request_id) REFERENCES item_request (id)
);


CREATE TABLE IF NOT EXISTS booking
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP                               NOT NULL,
    end_time   TIMESTAMP                               NOT NULL,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(100)                            NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_user FOREIGN KEY (booker_id) REFERENCES users (id),
    CONSTRAINT fk_booking_item FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(2000)                           NOT NULL,
    item_id   BIGINT,
    author_id BIGINT,
    created   TIMESTAMP                               NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comment_item FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_comment_user FOREIGN KEY (author_id) REFERENCES users (id)
);