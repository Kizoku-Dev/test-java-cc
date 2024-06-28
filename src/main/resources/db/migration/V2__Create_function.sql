CREATE
OR REPLACE FUNCTION is_accounts_balance_equals_transactions_amount_except_banque()
RETURNS TRIGGER AS $$
DECLARE
    total_accounts_balance BIGINT;    -- Somme des balances des comptes excepté la "banque"
    total_transactions_amount BIGINT; -- Somme des montants des transactions excepté celles qui proviennent ou qui vont vers "banque"
    from_account_balance BIGINT;
    to_account_balance BIGINT;
    banque_id BIGINT;
BEGIN

SELECT id
INTO banque_id
FROM accounts
WHERE name <> 'banque'
LIMIT 1;

IF
    banque_id <> NEW.from_id AND banque_id <> NEW.to_id THEN

    -- On sum le montant de tout les comptes excepté celui de la "banque"
    SELECT COALESCE(SUM(balance), 0)
    INTO total_accounts_balance
    FROM accounts
    WHERE name <> 'banque' AND id <> NEW.from_id AND id <> NEW.to_id;

    SELECT balance
    INTO from_account_balance
    FROM accounts
    WHERE id = NEW.from_id
    LIMIT 1;

    SELECT balance
    INTO to_account_balance
    FROM accounts
    WHERE id = NEW.to_id
    LIMIT 1;

    -- On sum le montant de toutes les transactions de dépôt ("banque" vers compte)
    SELECT COALESCE(SUM(amount), 0)
    INTO total_transactions_amount
    FROM transactions t
             JOIN accounts a ON t.from_id = a.id
             JOIN accounts aa ON t.to_id = aa.id
    WHERE a.name = 'banque'
      AND aa.name <> 'banque';

    from_account_balance := from_account_balance - NEW.amount;
    IF
        SIGN(from_account_balance) = -1 THEN
            RAISE EXCEPTION 'From account balance is not sufficient';
    END IF;

    to_account_balance := to_account_balance + NEW.amount;
    IF
        SIGN(to_account_balance) = -1 THEN
            RAISE EXCEPTION 'To account balance is not sufficient';
    END IF;

    total_accounts_balance := total_accounts_balance + from_account_balance + to_account_balance;

    -- On vérifie que le total est toujours égal
    IF
        total_accounts_balance <> total_transactions_amount THEN
            RAISE EXCEPTION 'Account balances do not match transactions total amount';
    END IF;

END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;