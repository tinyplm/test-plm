-- Create Color Table
CREATE TABLE IF NOT EXISTS color (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at timestamptz DEFAULT now(),
    updated_at timestamptz DEFAULT now(),
    rgb VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX color_id_idx ON color(id);

-- Seed Color Data
INSERT INTO color (id, name, description, rgb)
VALUES
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d01', 'Crimson', 'Deep red with rich saturation.', '220,20,60'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d02', 'Amber', 'Warm golden orange tone.', '255,191,0'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d03', 'Emerald', 'Vivid green with a cool sheen.', '80,200,120'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d04', 'Sapphire', 'Bold blue with a slight violet cast.', '15,82,186'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d05', 'Slate', 'Muted blue-gray neutral.', '112,128,144'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d06', 'Coral', 'Soft pink-orange blend.', '255,127,80'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d07', 'Teal', 'Balanced blue-green tone.', '0,128,128'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d08', 'Indigo', 'Deep blue with purple undertones.', '75,0,130'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d09', 'Ivory', 'Off-white with warm tint.', '255,255,240'),
    ('018f2e76-3b10-7c9a-8a2f-4f1b5e2a1d0a', 'Charcoal', 'Dark neutral gray.', '54,69,79');
