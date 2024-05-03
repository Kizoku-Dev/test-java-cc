package fr.ccomptes.test.interfaces;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/accounts")
    /**
     * Liste des comptes
     * @return
     */
    public List<Account> listAccounts() {
        return accountService.listAccounts();
    }

    @PostMapping("/accounts")
    /**
     * Création d'un compte
     * @param AccountCreationRequest
     * @return AccountCreationResponse
     */
    public AccountCreationResponse createAccount(@RequestBody AccountCreationRequest accountRequest) {
        if (accountRequest.name().isEmpty()) 
            throw new IllegalArgumentException("Nom invalide");

        String name = accountRequest.name();

        if (accountService.accountExists(name))
            throw new RuntimeException("Compte existant");

        Account account = accountService.createAccount(name);
        
        AccountCreationResponse response = new AccountCreationResponse(account.getId());
        return response;
    }

    @PutMapping("accounts/{name}/deposit")
    /**
     * Dépôt sur un compte donné
     * @param name
     * @param accountDepositRequest
     * @return
     * @throws Exception
     */
    public AccountDepositResponse depositAccount(@PathVariable String name, @RequestBody AccountDepositRequest accountDepositRequest) throws Exception {
        Long balance = accountService.depositAccount(name, accountDepositRequest.deposit());
        return new AccountDepositResponse(balance);
    }
    

    @GetMapping("/transactions")
    /**
     * Liste des transactions
     * @return
     */
    public List<Transaction> listTransactions() {
        // TODO : filter les transactions inférieures à un montant max_amount
        // et supérieures à un montant min_amount indiqués en paramètres d'URL
        // ne pas filtrer si les montants ne sont pas renseignés 
        return accountService.listTransactions();
    }
    
    @PostMapping("/transactions")
    /**
     * Exécution d'une transaction entre 2 comptes
     * TODO : sécuriser en controlant une clé d'API liée au compte
     * @param transactionRequest
     * @return
     */
    public TransactionResponse addTransaction(@RequestBody TransactionRequest transactionRequest) throws Exception {
        Transaction transaction = accountService.addTransaction(transactionRequest.from(), transactionRequest.to(), transactionRequest.amount());

        TransactionResponse transactionResponse = new TransactionResponse(transaction.getFrom().getName(), transaction.getTo().getName(), transaction.getAmount());
        return transactionResponse;
    }

    @GetMapping("/verification")
    /**
     * Vérification de l'intégrité des comptes et des transactions
     * @return
     */
    public Boolean verification() {
        // TODO : implémenter le contrôle d'intégrité des comptes et des transactions
        // retourner true si tout est en ordre, false sinon
        return null;
    }
}
