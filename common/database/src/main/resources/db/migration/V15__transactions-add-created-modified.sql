ALTER TABLE transaction
    ADD created TIMESTAMP NOT NULL default current_timestamp;

ALTER TABLE transaction
    ADD modified TIMESTAMP NOT NULL default current_timestamp;
