CREATE TABLE institution
(
    id                     SERIAL PRIMARY KEY,
    external_id            uuid UNIQUE NOT NULL,
    nordigen_id            VARCHAR UNIQUE NOT NULL ,
    bic                    VARCHAR,
    transaction_total_days VARCHAR     NOT NULL,
    logo                   VARCHAR     NOT NULL
);

CREATE TABLE institution_country
(
    country_id     INT NOT NULL REFERENCES country (id) ON UPDATE CASCADE ON DELETE CASCADE,
    institution_id INT NOT NULL REFERENCES institution (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT institution_country_pkey PRIMARY KEY (country_id, institution_id)
)
