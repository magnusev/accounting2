CREATE TABLE balance
(
    id             SERIAL PRIMARY KEY,
    account_id     INT              NOT NULL REFERENCES account (id),
    amount         DOUBLE PRECISION NOT NULL,
    currency       VARCHAR          NOT NULL,
    balance_type   VARCHAR          NOT NULL,
    reference_date TIMESTAMP        NOT NULL,
    UNIQUE (account_id, balance_type)
)


