package fr.ccomptes.test.application;

import fr.ccomptes.test.application.exception.AccountException;
import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.Transaction;
import fr.ccomptes.test.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTests {

  @Mock
  AccountRepository accountRepository;

  @Mock
  TransactionRepository transactionRepository;

  @Mock
  AuthService authService;

  @InjectMocks
  AccountService accountService;

  @Test
  void whenSufficientFunds_thenTransactionSucceeds() {

    Account aliceAccount = new Account("alice");
    aliceAccount.setId(1L);
    aliceAccount.setBalance(100L);
    Account bobAccount = new Account("bob");
    bobAccount.setId(2L);
    bobAccount.setBalance(0L);

    when(this.accountRepository.findById(1L)).thenReturn(Optional.of(aliceAccount));
    when(this.accountRepository.findById(2L)).thenReturn(Optional.of(bobAccount));

    // toutes les conditions sont réunies pour que la transaction réussisse
    this.accountService.addTransaction(aliceAccount.getId(), bobAccount.getId(), 100L);

    assertEquals(0L, aliceAccount.getBalance()); // Alice a tout
    assertEquals(100L, bobAccount.getBalance()); // donné à Bob
    verify(this.accountRepository, times(1)).findById(1L); // l'accountRepository a du être appelé
    verify(this.accountRepository, times(1)).findById(2L); // une fois par intervenant dans la transaction
    verify(this.transactionRepository, times(1)).save(any()); // une transaction a du être sauvegardée
  }

  @Test
  void whenInsufficientFunds_thenTransactionFails() {
    Account aliceAccount = new Account("alice");
    aliceAccount.setId(1L);
    aliceAccount.setBalance(100L);
    Account bobAccount = new Account("bob");
    bobAccount.setId(2L);
    bobAccount.setBalance(0L);

    when(this.accountRepository.findById(1L)).thenReturn(Optional.of(aliceAccount));
    when(this.accountRepository.findById(2L)).thenReturn(Optional.of(bobAccount));

    // alice n'a pas assez d'argent à envoyer à bob. Une erreur doit être levée
    assertThrows(
      AccountException.class,
      () -> this.accountService.addTransaction(aliceAccount.getId(), bobAccount.getId(), 200L)
    );
  }

  @Test
  void whenNegativeAmount_thenTransactionFails() {
    assertThrows(
      IllegalArgumentException.class,
      () -> this.accountService.addTransaction(1L, 2L, -100L)
    );
  }

  @Test
  void whenAccountDoesNotExists_thenTransactionFails() {
    when(this.accountRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
      IllegalArgumentException.class,
      () -> this.accountService.addTransaction(1L, 2L, 100L)
    );
  }

  @Test
  void whenAccountNotExist_thenAccountCreationSucceeds() {

    when(this.authService.generateNewToken()).thenReturn("toto");
    this.accountService.createAccount("toto");
    verify(this.accountRepository, times(1)).save(any());
  }

  @Test
  void whenAccountExist_thenAccountCreationFails() {

    Account banqueAccount = new Account("banque");
    when(this.accountRepository.findByName("banque")).thenReturn(banqueAccount);

    assertThrows(IllegalArgumentException.class, () -> this.accountService.createAccount("banque"));
  }

  @Test
  void whenBanqueTransactionAmountEqualsAccountsBalance_thenVerificationOk() {

    Account banqueAccount = new Account("banque");
    banqueAccount.setId(1L);
    banqueAccount.setBalance(1000);
    Account account1 = new Account("account1");
    account1.setId(2L);
    account1.setBalance(50);
    Account account2 = new Account("account2");
    account2.setId(3L);
    account2.setBalance(50);

    Transaction transaction1 = new Transaction();
    transaction1.setFrom(banqueAccount);
    transaction1.setTo(account1);
    transaction1.setAmount(100L);

    List<Transaction> transactions = List.of(transaction1);
    List<Account> accounts = List.of(banqueAccount, account1, account2);
    when(this.accountRepository.findByName("banque")).thenReturn(banqueAccount);
    when(this.accountRepository.findAll()).thenReturn(accounts);
    when(this.transactionRepository.findAll()).thenReturn(transactions);

    assertTrue(this.accountService.verification());
  }

  @Test
  void whenBanqueTransactionAmountNotEqualsAccountsBalance_thenVerificationFails() {

    Account banqueAccount = new Account("banque");
    banqueAccount.setId(1L);
    banqueAccount.setBalance(1000);
    Account account1 = new Account("account1");
    account1.setId(2L);
    account1.setBalance(100);
    Account account2 = new Account("account2");
    account2.setId(3L);
    account2.setBalance(50);

    Transaction transaction1 = new Transaction();
    transaction1.setFrom(banqueAccount);
    transaction1.setTo(account1);
    transaction1.setAmount(100L);

    List<Transaction> transactions = List.of(transaction1);
    List<Account> accounts = List.of(banqueAccount, account1, account2);
    when(this.accountRepository.findByName("banque")).thenReturn(banqueAccount);
    when(this.accountRepository.findAll()).thenReturn(accounts);
    when(this.transactionRepository.findAll()).thenReturn(transactions);

    assertFalse(this.accountService.verification());
  }
}
