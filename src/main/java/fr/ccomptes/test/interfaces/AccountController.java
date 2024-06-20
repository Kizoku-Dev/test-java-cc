package fr.ccomptes.test.interfaces;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.ccomptes.test.application.AccountService;
import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.Transaction;
import fr.ccomptes.test.interfaces.dto.AccountCreationRequest;
import fr.ccomptes.test.interfaces.dto.AccountCreationResponse;
import fr.ccomptes.test.interfaces.dto.AccountDepositRequest;
import fr.ccomptes.test.interfaces.dto.AccountDepositResponse;
import fr.ccomptes.test.interfaces.dto.TransactionRequest;
import fr.ccomptes.test.interfaces.dto.TransactionResponse;


@RestController
/**
 * TODO : gestion des exceptions
 */
public class AccountController {

  @Autowired
  private AccountService accountService;

  /**
   * Liste les comptes
   *
   * @return La liste des comptes
   */
  @GetMapping("/accounts")
  public List<Account> listAccounts() {
    return this.accountService.listAccounts();
  }

  /**
   * Création d'un compte
   *
   * @param accountRequest les informations du compte à créer
   * @return AccountCreationResponse
   */
  @PostMapping("/accounts")
  public AccountCreationResponse createAccount(@RequestBody @Valid final AccountCreationRequest accountRequest) {
    String name = accountRequest.name();

    Account account = this.accountService.createAccount(name);

    return new AccountCreationResponse(account.getId());
  }

  /**
   * Dépôt sur un compte donné
   *
   * @param id                    du compte
   * @param accountDepositRequest
   * @return la solde actuel du compte
   */
  @PutMapping("accounts/{id}/deposit")
  public AccountDepositResponse depositAccount(
    @PathVariable final Long id,
    @RequestBody final AccountDepositRequest accountDepositRequest) {

    long balance = this.accountService.depositAccount(id, accountDepositRequest.deposit());
    return new AccountDepositResponse(balance);
  }


  /**
   * Liste des transactions
   *
   * @return la liste des transactions
   */
  @GetMapping("/transactions")
  public List<TransactionResponse> listTransactions(
    @RequestParam(name = "min_amount", required = false) final Long minAmount,
    @RequestParam(name = "max_amount", required = false) final Long maxAmount) {

    return TransactionMapper.transactionsToDto(this.accountService.listTransactions(minAmount, maxAmount));
  }

  @PostMapping("/transactions")
  /**
   * Exécution d'une transaction entre 2 comptes
   * TODO : sécuriser en controlant une clé d'API liée au compte
   * @param transactionRequest
   * @return
   */
  public TransactionResponse addTransaction(@RequestBody final TransactionRequest transactionRequest) {
    Transaction transaction = this.accountService.addTransaction(
      transactionRequest.srcId(),
      transactionRequest.destId(),
      transactionRequest.amount()
    );

    return new TransactionResponse(
      transaction.getFrom().getName(),
      transaction.getTo().getName(),
      transaction.getAmount()
    );
  }

  /**
   * Vérification de l'intégrité des comptes et des transactions
   *
   * @return
   */
  @GetMapping("/verification")
  public Boolean verification() {
    // TODO : implémenter le contrôle d'intégrité des comptes et des transactions
    // retourner true si tout est en ordre, false sinon
    return null;
  }
}
