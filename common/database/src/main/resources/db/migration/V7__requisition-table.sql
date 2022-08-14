CREATE TABLE requisistion
(
    id                 SERIAL PRIMARY KEY,
    external_id        uuid UNIQUE NOT NULL,
    nordigen_id        uuid UNIQUE NOT NULL,
    redirect           VARCHAR     NOT NULL,
    status             VARCHAR     NOT NULL,
    institution_id     INT         NOT NULL REFERENCES institution (id),
    created_at         TIMESTAMP   NOT NULL,
    agreement_id       INT         NOT NULL REFERENCES enduser_agreement (id),
    reference          VARCHAR     NOT NULL,
    user_language      VARCHAR     NOT NULL,
    link               VARCHAR     NOT NULL,
    ssn                VARCHAR,
    account_selection  BOOLEAN     NOT NULL,
    redirect_immediate BOOLEAN     NOT NULL

)
