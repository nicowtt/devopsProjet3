-- liquibase formatted sql

-- changeset nicolas-Bodelle:002
CREATE TABLE files (
    "fil_id_pk"      BIGSERIAL  UNIQUE NOT NULL,
    "fil_uuid"       UUID       NOT NULL,
    "fil_name"       TEXT       NOT NULL,
    "fil_size"       BIGINT     NOT NULL,
    "fil_mime_type"  TEXT       NOT NULL,
    "fil_created_at" TIMESTAMP  NOT NULL,
    "fil_expired_at" TIMESTAMP  NOT NULL,
    "fil_password"   TEXT       NULL,
    "fil_usr_id_fk"  BIGINT     NOT NULL,
    PRIMARY KEY ("fil_id_pk"),
    CONSTRAINT fk_files_user FOREIGN KEY ("fil_usr_id_fk") REFERENCES users ("usr_id_pk") ON DELETE CASCADE);

CREATE INDEX "file_uuid_idx"  ON "files" ("fil_uuid"      ASC);
CREATE INDEX "file_owner_idx" ON "files" ("fil_usr_id_fk" ASC);