package fr.ccomptes.test.interfaces;

import fr.ccomptes.test.application.AccountService;
import fr.ccomptes.test.application.AuthService;
import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.Transaction;
import fr.ccomptes.test.interfaces.dto.AccountCreationRequest;
import fr.ccomptes.test.interfaces.dto.AccountCreationResponse;
import fr.ccomptes.test.interfaces.dto.AccountDepositRequest;
import fr.ccomptes.test.interfaces.dto.AccountDepositResponse;
import fr.ccomptes.test.interfaces.dto.AccountListResponse;
import fr.ccomptes.test.interfaces.dto.TransactionRequest;
import fr.ccomptes.test.interfaces.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.CredentialException;
import java.util.List;


@RestController
public class AccountController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private AuthService authService;

  /**
   * Liste les comptes
   *
   * @return La liste des comptes
   */
  @GetMapping("/accounts")
  public List<AccountListResponse> listAccounts(@RequestParam(name = "name", required = false) final String name) {
    List<Account> accounts = this.accountService.listAccounts(name);
    return AccountMapper.accountsToDto(accounts);
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

    return new AccountCreationResponse(account.getId(), account.getApiKey());
  }

  /**
   * Dépôt sur un compte donné
   *
   * @param id du compte sur lequel faire le dépôt
   * @param accountDepositRequest
   * @return le solde actuel du compte
   */
  @PutMapping("accounts/{id}/deposit")
  public AccountDepositResponse depositAccount(
    @PathVariable final Long id,
    @RequestBody final AccountDepositRequest accountDepositRequest) {

    long balance = this.accountService.depositAccount(id, accountDepositRequest.deposit());
    return new AccountDepositResponse(balance);
  }


  /**
   * Liste les transactions
   *
   * @param minAmount (optionnel) permet de filter les transactions >= minAmount
   * @param maxAmount (optionnel) permet de filter les transactions <= maxAmount
   * @return la liste des transactions
   */
  @GetMapping("/transactions")
  public List<TransactionResponse> listTransactions(
    @RequestParam(name = "min_amount", required = false) final Long minAmount,
    @RequestParam(name = "max_amount", required = false) final Long maxAmount) {

    return TransactionMapper.transactionsToDto(this.accountService.listTransactions(minAmount, maxAmount));
  }

  /**
   * Exécution d'une transaction entre 2 comptes
   *
   * @param apiKey la clé d'api pour s'authentifier
   * @param transactionRequest
   * @return la transaction effectuée
   */
  @PostMapping("/transactions")
  public TransactionResponse addTransaction(
    @RequestHeader(value = AuthService.AUTH_TOKEN_HEADER_NAME, required = false) final String apiKey,
    @RequestBody final TransactionRequest transactionRequest) throws CredentialException {

    this.authService.validateAuth(apiKey, transactionRequest.srcId());
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
   * @return true si la vérification est ok, false si non
   */
  @GetMapping("/verification")
  public boolean verification() {
    return this.accountService.verification();
  }
}
