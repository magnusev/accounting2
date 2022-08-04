CREATE TABLE user_account
(
    id          SERIAL PRIMARY KEY,
    external_id uuid UNIQUE    NOT NULL,
    email       VARCHAR UNIQUE NOT NULL
)
