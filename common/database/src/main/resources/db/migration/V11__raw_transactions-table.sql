CREATE TABLE raw_transaction
(
    id                                  SERIAL PRIMARY KEY,
    external_id                         uuid UNIQUE      NOT NULL,
    account_id                          INT              NOT NULL REFERENCES account (id),
    status                              VARCHAR          NOT NULL,
    additional_information              VARCHAR          NOT NULL,
    booking_date                        DATE             NOT NULL,
    creditor_name                       VARCHAR,
    debitor_name                        VARCHAR,
    creditor_account                    JSONB,
    debitor_account                     JSONB,
    exchange_rate                       DOUBLE PRECISION,
    instructed_amount                   DOUBLE PRECISION,
    instructed_currency                 VARCHAR,
    source_currency                     VARCHAR,
    target_currency                     VARCHAR,
    unit_currency                       VARCHAR,
    entry_reference                     VARCHAR          NOT NULL,
    remittance_information_unstructured VARCHAR          NOT NULL,
    transaction_amount                  DOUBLE PRECISION NOT NULL,
    transaction_currency                VARCHAR          NOT NULL,
    transaction_id                      VARCHAR          NOT NULL UNIQUE,
    value_date                          DATE             NOT NULL
)
