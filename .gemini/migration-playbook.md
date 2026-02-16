# Flyway Migration Playbook
Quarkus + Hibernate ORM + PostgreSQL

This document defines the safe process for evolving database schema using Flyway.

Flyway owns schema changes.
Hibernate validates schema only.

----------------------------------------------------------------

## 1. Migration Philosophy

Every migration must be:

- forward only
- immutable after merge
- atomic (one logical change)
- backward compatible
- safe for production rollout

A migration must allow:

OLD application + NEW database  → works
NEW application + OLD database  → works during rollout

If this is not true, the migration is unsafe.

----------------------------------------------------------------

## 2. Migration Naming

Format:

VYYYY.MM.DD.NNN__description.sql

Example:

V2026.02.16.001__create_user_table.sql
V2026.02.16.002__add_user_email.sql

Rules:

- never reuse versions
- never rename migrations
- description uses snake_case
- version ordering must be chronological

----------------------------------------------------------------

## 3. Creating a New Table

Template:

CREATE TABLE app_user (
id UUID NOT NULL,
email VARCHAR(255) NOT NULL,
created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
version INTEGER NOT NULL DEFAULT 0,
deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uk_app_user_email UNIQUE (email)
);

CREATE INDEX idx_app_user_deleted
ON app_user(deleted);

Rules:

- include standard columns
- explicit constraints required
- indexes added separately

----------------------------------------------------------------

## 4. Adding a Column (SAFE PATTERN)

Never add NOT NULL immediately.

Incorrect:

ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL;

Correct staged approach:

ALTER TABLE users ADD COLUMN status VARCHAR(20);

UPDATE users
SET status = 'NEW'
WHERE status IS NULL;

ALTER TABLE users
ALTER COLUMN status SET NOT NULL;

----------------------------------------------------------------

## 5. Renaming a Column (Zero Downtime)

Renames must be staged.

Migration A:
- add new column

Migration B:
- copy data

UPDATE users SET new_name = old_name;

Deploy application using new column.

Migration C (later release):
- drop old column

ALTER TABLE users DROP COLUMN old_name;

Never rename directly in production.

----------------------------------------------------------------

## 6. Adding a Foreign Key

Step-by-step:

ALTER TABLE orders ADD COLUMN customer_id UUID;

Backfill data if needed.

ALTER TABLE orders
ALTER COLUMN customer_id SET NOT NULL;

ALTER TABLE orders
ADD CONSTRAINT fk_orders_customer
FOREIGN KEY (customer_id)
REFERENCES customer(id);

CREATE INDEX idx_orders_customer_id
ON orders(customer_id);

Rules:

- FK added only after data is valid
- FK column must be indexed

----------------------------------------------------------------

## 7. Adding Indexes

Indexes must be separate operations.

CREATE INDEX idx_orders_status
ON orders(status);

Avoid combining heavy ALTER and index creation in one migration for large tables.

----------------------------------------------------------------

## 8. Dropping Columns (Staged Removal)

Never drop columns immediately.

Step 1:
Application stops reading/writing column.

Step 2 (later migration):

ALTER TABLE users DROP COLUMN legacy_code;

----------------------------------------------------------------

## 9. Data Migrations

Allowed only for:

- backfilling new columns
- normalization
- enum/value conversion
- reference cleanup

Rules:

- deterministic updates only
- no business logic
- must be rerunnable safely in dev

----------------------------------------------------------------

## 10. Repeatable Migrations

Naming:

R__description.sql

Used for:

- reference data
- lookup tables
- permissions
- static configuration

Must be idempotent.

Example:

INSERT INTO roles(name)
VALUES ('ADMIN')
ON CONFLICT DO NOTHING;

----------------------------------------------------------------

## 11. Large Table Safety Rules

Avoid operations that lock tables:

- immediate NOT NULL
- table rewrite ALTER
- large blocking updates

Preferred approach:

1. add nullable column
2. backfill in batches
3. enforce constraint later

----------------------------------------------------------------

## 12. Rollback Strategy

Flyway does not rely on down migrations.

Recovery strategy:

- create corrective forward migration
- never edit applied migration

----------------------------------------------------------------

## 13. Pre-Commit Checklist

Before merging:

- migration is atomic
- constraint names explicit
- FK indexes created
- NOT NULL staged
- backward compatible
- entity mapping matches schema
- runs successfully on existing database

----------------------------------------------------------------

## 14. Golden Rule

Database migrations must prioritize safety over convenience.

Never optimize for fewer migrations.
Optimize for predictable deployments.

----------------------------------------------------------------
