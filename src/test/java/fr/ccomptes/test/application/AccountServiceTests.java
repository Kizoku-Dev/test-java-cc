package fr.ccomptes.test.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.ccomptes.test.application.exception.AccountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.TransactionRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTests {

  @Mock
  AccountRepository accountRepository;

  @Mock
  TransactionRepository transactionRepository;

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

    this.accountService.createAccount("toto");
    verify(this.accountRepository, times(1)).save(any());
  }

  @Test
  void whenAccountExist_thenAccountCreationFails() {

    Account banqueAccount = new Account("banque");
    when(this.accountRepository.findByName("banque")).thenReturn(banqueAccount);

    assertThrows(IllegalArgumentException.class, () -> this.accountService.createAccount("banque"));
  }

}
