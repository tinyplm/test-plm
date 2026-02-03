CREATE TABLE IF NOT EXISTS color (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at timestamptz DEFAULT now(),
    updated_at timestamptz DEFAULT now(),
    rgb VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX color_id_idx ON color(id);
