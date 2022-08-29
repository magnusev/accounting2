ALTER TABLE raw_transaction
    ADD created TIMESTAMP NOT NULL default current_timestamp;

ALTER TABLE raw_transaction
    ADD modified TIMESTAMP NOT NULL default current_timestamp;
