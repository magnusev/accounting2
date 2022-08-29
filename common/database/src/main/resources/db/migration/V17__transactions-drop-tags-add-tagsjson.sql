ALTER TABLE transaction
    DROP COLUMN tags;

ALTER TABLE transaction
    ADD COLUMN tags jsonb NOT NULL default '[]'::jsonb;
