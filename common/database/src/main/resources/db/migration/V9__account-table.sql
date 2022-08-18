CREATE TABLE account
(
    id                SERIAL PRIMARY KEY,
    external_id       uuid UNIQUE NOT NULL,
    nordigen_id       uuid UNIQUE NOT NULL,
    user_id           INT         NOT NULL REFERENCES user_account (id),
    institution_id    INT         NOT NULL REFERENCES institution (id),
    created           TIMESTAMP   NOT NULL,
    last_accessed     TIMESTAMP   NOT NULL,
    status            VARCHAR     NOT NULL,
    "owner"           VARCHAR     NOT NULL,
    "name"            VARCHAR     NOT NULL,
    resource_id       VARCHAR     NOT NULL,
    iban              VARCHAR     NOT NULL,
    bban              VARCHAR     NOT NULL,
    currency          VARCHAR     NOT NULL,
    product           VARCHAR,
    cash_account_type VARCHAR
)
