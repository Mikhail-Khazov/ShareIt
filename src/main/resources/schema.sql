DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR (255) NOT NULL,
    email       VARCHAR (255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description     VARCHAR NOT NULL,
    requestor_id    BIGINT NOT NULL,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_reqst PRIMARY KEY (id),
    CONSTRAINT fk_reqst_requestor FOREIGN KEY (requestor_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name            VARCHAR (255) NOT NULL,
    description     VARCHAR (1024) NOT NULL,
    is_available    BOOLEAN NOT NULL,
    owner_id        BIGINT NOT NULL,
    request_id      BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_requests FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_id     BIGINT NOT NULL,
    booker_id   BIGINT NOT NULL,
    start_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status      VARCHAR NOT NULL,
    CONSTRAINT pk_book PRIMARY KEY (id),
    CONSTRAINT fk_book_item_booker FOREIGN KEY (booker_id) REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_book_item FOREIGN KEY (item_id) REFERENCES items (id)  ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS comments
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text        VARCHAR (1024) NOT NULL,
    item_id     BIGINT NOT NULL,
    author_id   BIGINT NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_comments_item FOREIGN KEY (item_id) REFERENCES items (id)  ON DELETE CASCADE
);
