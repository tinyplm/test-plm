CREATE TABLE IF NOT EXISTS line (
    line_id UUID PRIMARY KEY,
    line_code VARCHAR(255) NOT NULL,
    season_code VARCHAR(255) NOT NULL,
    year INTEGER,
    brand_id UUID NOT NULL,
    market_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    start_date timestamptz,
    end_date timestamptz,
    planned_style_count INTEGER,
    planned_units INTEGER,
    planned_revenue NUMERIC(19, 2),
    created_by VARCHAR(255),
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX line_id_idx ON line(line_id);
