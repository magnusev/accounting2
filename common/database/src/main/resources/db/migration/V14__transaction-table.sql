CREATE TABLE transaction
(
    id                 SERIAL PRIMARY KEY,
    external_id        UUID UNIQUE      NOT NULL,
    account_id         INT              NOT NULL REFERENCES account (id),
    raw_transaction_id INT              NOT NULL REFERENCES raw_transaction (id),
    "date"             DATE             NOT NULL,
    description        VARCHAR          NOT NULL,
    category           VARCHAR          NOT NULL,
    sub_category       VARCHAR          NOT NULL,
    amount             DOUBLE PRECISION NOT NULL,
    currency           VARCHAR          NOT NULL,
    details            VARCHAR          NOT NULL,
    tags               VARCHAR[]        NOT NULL,
    notes              VARCHAR,
    ignored            BOOLEAN          NOT NULL,
    status             VARCHAR          NOT NULL,
    type               VARCHAR          NOT NULL,
    source             VARCHAR          NOT NULL
)
