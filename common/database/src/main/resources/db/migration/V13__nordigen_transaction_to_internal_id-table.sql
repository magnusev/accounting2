CREATE TABLE nordigen_transaction_id_to_internal_id
(
    nordigen_id   VARCHAR PRIMARY KEY,
    accounting_id UUID NOT NULL UNIQUE
)
