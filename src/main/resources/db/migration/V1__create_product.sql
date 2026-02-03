CREATE TABLE IF NOT EXISTS product (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(19, 2),
    quantity INTEGER
);

CREATE UNIQUE INDEX product_id_idx ON product(id);
