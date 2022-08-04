CREATE TABLE country
(
    id          SERIAL PRIMARY KEY,
    external_id uuid UNIQUE    NOT NULL,
    name        VARCHAR        NOT NULL,
    region      VARCHAR        NOT NULL,
    alpha_2     VARCHAR UNIQUE NOT NULL,
    alpha_3     VARCHAR UNIQUE NOT NULL
)
