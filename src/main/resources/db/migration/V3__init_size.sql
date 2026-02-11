-- Create Size Table
CREATE TABLE IF NOT EXISTS size (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    sizes VARCHAR(255) NOT NULL
);

INSERT INTO size (id, name, sizes) VALUES ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1f01', 'Standard', 'XS, S, M, L, XL');
