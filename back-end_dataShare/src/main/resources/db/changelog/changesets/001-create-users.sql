-- liquibase formatted sql

-- changeset nicolas-Bodelle:001
CREATE TABLE users (
    "usr_id_pk" BIGSERIAL UNIQUE NOT NULL,
    "usr_uuid" UUID NOT NULL,
    "usr_email" TEXT UNIQUE NOT NULL,
    "usr_password" VARCHAR(60) NOT NULL,
    "usr_created_at" TIMESTAMP NOT NULL,
    "usr_updated_at" TIMESTAMP NULL,
    PRIMARY KEY ("usr_id_pk"));

CREATE INDEX "user_uuid_idx" ON "users" ("usr_uuid" asc);
CREATE INDEX "user_email_idx" ON "users" ("usr_email" asc);