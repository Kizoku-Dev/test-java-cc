package fr.ccomptes.test.application;

import fr.ccomptes.test.application.exception.AccountException;
import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.Transaction;
import fr.ccomptes.test.domain.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountService {

  private static final String BANQUE_ACCOUNT_NAME = "banque";

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  /**
   * Liste des comptes
   * TODO : filtrer les comptes par un nom donné en paramètre d'URL, insensible à la casse
   *
   * @return
   */
  public List<Account> listAccounts() {
    return this.accountRepository.findAll();
  }

  public boolean accountExists(final String name) {
    return this.accountRepository.findByName(name) != null;
  }

  public Account getAccountByName(final String name) {
    return this.accountRepository.findByName(name);
  }

  /**
   * Création d'un compte
   *
   * @param name
   * @return AccountCreationResponse
   */
  public Account createAccount(final String name) {
    if (this.accountExists(name)) {
      throw new IllegalArgumentException("Compte existant");
    }

    Account account = new Account(name);
    account = this.accountRepository.save(account);

    return account;
  }

  /**
   * Méthode spéciale pour créditer la banque
   *
   * @param accountName
   * @param balance
   */
  public void setBalance(final String accountName, final Long balance) {
    Account account = this.accountRepository.findByName(accountName);
    account.setBalance(balance);
    this.accountRepository.save(account);
  }

  /**
   * Dépôt sur un compte donné
   *
   * @param id      du compte
   * @param deposit montant à déposer
   * @return AccountDepositResponse
   */
  public long depositAccount(final long id, final long deposit) {
    if (deposit <= 0) {
      throw new IllegalArgumentException("Le montant du dépôt doit être non nul et positif.");
    }
    Account account = this.accountRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Pas de compte trouvé"));

    // Un dépôt est en réalité une transaction de la banque vers le compte crédité
    this.addTransactionFromBanque(account, deposit);

    return account.getBalance();
  }

  /**
   * Débite un compte au profit d'un autre à travers une transaction
   *
   * @param srcId  du compte à débiter
   * @param destId du compte à créditer
   * @param amount montant du virement
   */
  public Transaction addTransaction(final long srcId, final long destId, final long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Le montant du virement doit être non nul et positif.");
    }
    Account srcAccount = this.accountRepository.findById(srcId)
      .orElseThrow(() -> new IllegalArgumentException("Pas de compte débiteur trouvé"));
    Account destAccount = this.accountRepository.findById(destId)
      .orElseThrow(() -> new IllegalArgumentException("Pas de compte créditeur trouvé"));

    return this.processTransaction(srcAccount, destAccount, amount);
  }


  /**
   * Liste des transactions
   *
   * @return
   */
  public List<Transaction> listTransactions(final Long minAmount, final Long maxAmount) {
    return this.transactionRepository.findByAmountBetween(minAmount, maxAmount);
  }

  /**
   * Exécution d'une transaction entre 2 comptes
   *
   * @param srcAccount  compte à débiter
   * @param destAccount compte à créditer
   * @param amount      montant à déplacer
   */
  private Transaction processTransaction(final Account srcAccount, final Account destAccount, final long amount) {

    // Rejeter la transaction si le solde du compte débiteur est insufissant !
    if (srcAccount.getBalance() < amount) {
      throw new AccountException("Solde insuffisant !");
    }

    // Enregistrement de l'opération et rafraîchissement des soldes
    Transaction transaction = new Transaction(srcAccount, destAccount, amount);
    transaction = this.transactionRepository.save(transaction);
    srcAccount.setBalance(srcAccount.getBalance() - amount);
    destAccount.setBalance(destAccount.getBalance() + amount);
    this.accountRepository.save(srcAccount);
    this.accountRepository.save(destAccount);

    return transaction;
  }

  private void addTransactionFromBanque(final Account account, final long amount) {
    Account banqueAccount = this.accountRepository.findByName(BANQUE_ACCOUNT_NAME);
    this.processTransaction(banqueAccount, account, amount);
  }

  /**
   * Vérification de l'intégrité des comptes et des transactions
   *
   * @return
   */
  public Boolean verification() {
    // TODO : implémenter le contrôle d'intégrité des comptes et des transactions
    // retourner true si tout va bien, false sinon
    return null;
  }
}
