CREATE TABLE enduser_agreement
(
    id                    SERIAL PRIMARY KEY,
    external_id           uuid UNIQUE    NOT NULL,
    nordigen_id           VARCHAR UNIQUE NOT NULL,
    user_id               INT            NOT NULL REFERENCES user_account (id),
    institution_id        INT            NOT NULL REFERENCES institution (id),
    created_at            TIMESTAMP      NOT NULL,
    max_historical_days   INT            NOT NULL,
    access_valid_for_days INT            NOT NULL,
    valid_until           TIMESTAMP      NOT NULL,
    access_scope          VARCHAR[]      NOT NULL,
    accepted              VARCHAR
)
