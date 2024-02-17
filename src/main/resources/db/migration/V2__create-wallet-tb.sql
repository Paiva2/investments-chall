CREATE TABLE IF NOT EXISTS tb_wallets (
    id UUID DEFAULT gen_random_uuid(),
    amount  NUMERIC(20,20) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    user_id UUID NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES tb_users(id)
);

CREATE EXTENSION IF NOT EXISTS moddatetime;

CREATE TRIGGER handle_wallets_updatedat BEFORE UPDATE ON tb_wallets
FOR EACH ROW EXECUTE PROCEDURE moddatetime(updated_at);