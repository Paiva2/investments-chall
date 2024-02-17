CREATE TABLE IF NOT EXISTS tb_users (
    id UUID DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
);

CREATE EXTENSION IF NOT EXISTS moddatetime;

CREATE TRIGGER updatedAt_trigger BEFORE UPDATE ON tb_users
FOR EACH ROW EXECUTE PROCEDURE moddatetime(updated_at);