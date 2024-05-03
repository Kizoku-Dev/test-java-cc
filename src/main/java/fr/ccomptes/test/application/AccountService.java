package fr.ccomptes.test.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.Transaction;
import fr.ccomptes.test.domain.TransactionRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Liste des comptes
     * TODO : filtrer les comptes par un nom donné en paramètre d'URL, insensible à la casse
     * @return
     */
    public List<Account> listAccounts() {
        return accountRepository.findAll();
    }

    public Boolean accountExists(String name) {
        return accountRepository.findByName(name) != null;
    }

    public Account getAccountByName(String name) {
        return accountRepository.findByName(name);
    }

    /**
     * Création d'un compte
     * @param name
     * @param deposit
     * @return AccountCreationResponse
     */
    public Account createAccount(String name) {
        if (accountExists(name))
            throw new IllegalArgumentException("Compte existant");

        Account account = new Account(name);
        account = accountRepository.save(account);

        return account;
    }

    /**
     * Méthode spéciale pour créditer la banque
     * @param accountName
     * @param balance
     */
    public void setBalance(String accountName, Long balance) {
        Account account = accountRepository.findByName(accountName);
        account.setBalance(balance);
        accountRepository.save(account);
    }

    /**
     * Dépôt sur un compte donné
     * @param id
     * @param AccountDepositRequest
     * @return AccountDepositResponse
     */
    public Long depositAccount(String name, Long deposit) throws Exception {
        Account account = accountRepository.findByName(name);
        if (account == null) throw new IllegalArgumentException("Pas de compte trouvé");

        // Un dépôt est en réalité une transaction de la banque vers le compte crédité
        addTransaction("banque", account.getName(), deposit);
        
        return account.getBalance();
    }
    

    /**
     * Liste des transactions
     * @return
     */
    public List<Transaction> listTransactions() {
        // TODO : filter les transactions inférieures à un montant amount_max
        // et supérieures à un montant amount_min
        // ne pas filtrer si les montants ne sont pas renseignés 
        return transactionRepository.findAll();
    }
    
    /**
     * Exécution d'une transaction entre 2 comptes
     * TODO : corriger les problèmes transactionnels
     * @param transactionRequest
     * @return
     */
    public Transaction addTransaction(String from, String to, Long amount) throws Exception {
        // Rejet de la transaction si un compte n'existe pas
        if (from == null || to == null) {
            throw new IllegalArgumentException("Deux comptes doivent être indiqués");
        }
        // Rejet de transactions nulles ou négatives
        if (amount <= 0) {
            throw new IllegalArgumentException("transaction invalide");
        }

        // Récupération des comptes
        Account accountFrom = accountRepository.findByName(from);
        Account accountTo = accountRepository.findByName(to);

        // Rejeter la transaction si le solde du compte débiteur est insufissant !
        if (accountFrom.getBalance() < amount)
            throw new Exception("Solde insuffisant !");

        // Enregistrement de l'opération et rafraîchissement des soldes
        Transaction transaction = new Transaction(accountFrom, accountTo, amount);
        transaction = transactionRepository.save(transaction);
        accountFrom.setBalance(accountFrom.getBalance() - amount);
        accountTo.setBalance(accountTo.getBalance() + amount);
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
        
        return transaction;
    }

    /**
     * Vérification de l'intégrité des comptes et des transactions
     * @return
     */
    public Boolean verification() {
        // TODO : implémenter le contrôle d'intégrité des comptes et des transactions
        // retourner true si tout va bien, false sinon
        return null;
    }
}
