CREATE OR REPLACE TRIGGER check_balances_integrity
    BEFORE INSERT
    ON transactions
    FOR EACH ROW
    EXECUTE FUNCTION is_accounts_balance_equals_transactions_amount_except_banque();