CREATE TABLE IF NOT EXISTS tb_investments (
    id UUID DEFAULT gen_random_uuid(),
    initial_amount  NUMERIC(20,20) NOT NULL,
    current_profit  NUMERIC(20,20) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    withdrawn_date TIMESTAMP WITH TIME ZONE,
    already_withdrawn BOOLEAN NOT NULL DEFAULT 'false',
    wallet_id UUID NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (wallet_id) REFERENCES tb_wallets(id)
);

CREATE EXTENSION IF NOT EXISTS moddatetime;

CREATE TRIGGER handle_wallets_updatedat BEFORE UPDATE ON tb_investments
FOR EACH ROW EXECUTE PROCEDURE moddatetime(updated_at);