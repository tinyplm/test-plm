-- =================================================================================================
-- 1. FUNCTIONS
-- =================================================================================================

-- Generic Trigger Function to be used by all tables
-- Expects the audit table to be named <table_name>_audit
CREATE OR REPLACE FUNCTION audit_trigger_func() RETURNS TRIGGER AS $$
DECLARE
    audit_table_name TEXT;
    q TEXT;
BEGIN
    audit_table_name := TG_TABLE_NAME || '_audit';
    
    IF (TG_OP = 'DELETE') THEN
        EXECUTE format('INSERT INTO %I (operation, old_data, changed_at) VALUES ($1, $2, LOCALTIMESTAMP)', audit_table_name)
        USING 'DELETE', row_to_json(OLD)::jsonb;
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        EXECUTE format('INSERT INTO %I (operation, old_data, new_data, changed_at) VALUES ($1, $2, $3, LOCALTIMESTAMP)', audit_table_name)
        USING 'UPDATE', row_to_json(OLD)::jsonb, row_to_json(NEW)::jsonb;
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        EXECUTE format('INSERT INTO %I (operation, new_data, changed_at) VALUES ($1, $2, LOCALTIMESTAMP)', audit_table_name)
        USING 'INSERT', row_to_json(NEW)::jsonb;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
