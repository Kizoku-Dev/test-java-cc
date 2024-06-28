CREATE TABLE IF NOT EXISTS accounts
(
    id      int8         NOT NULL,
    balance int8 NULL,
    "name"  varchar(255) NOT NULL,
    CONSTRAINT accounts_pkey PRIMARY KEY (id),
    CONSTRAINT uk_qtv290mh55xhggmpwosf5ag0v UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS transactions
(
    id      int8 NOT NULL,
    amount  int8 NULL,
    from_id int8 NULL,
    to_id   int8 NULL,
    CONSTRAINT transactions_pkey PRIMARY KEY (id),
    CONSTRAINT fk9enm4cc6itv2cvn9khpkc7pqp FOREIGN KEY (to_id) REFERENCES accounts (id),
    CONSTRAINT fks71tvk6f9fuyjyioqbofrdpra FOREIGN KEY (from_id) REFERENCES accounts (id)
);