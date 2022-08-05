CREATE TABLE access_token
(
    id                       SERIAL PRIMARY KEY,
    created_at               TIMESTAMP NOT NULL,
    access                   VARCHAR   NOT NULL,
    access_expire            INT       NOT NULL,
    access_expire_timestamp  TIMESTAMP NOT NULL,
    refresh                  VARCHAR   NOT NULL,
    refresh_expire           INT       NOT NULL,
    refresh_expire_timestamp TIMESTAMP NOT NULL
)
