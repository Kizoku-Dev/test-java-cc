package fr.ccomptes.test.domain;

import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class AccountRepositoryTest {

  @Autowired
  TransactionRepository transactionRepository;

  @Autowired
  AccountRepository accountRepository;

  @Test
  void whenTransactionValueIsConsistent_thenDoNotThrow() {
    Account eve = this.accountRepository.findByName("eve");
    Account bob = this.accountRepository.findByName("bob");

    Transaction transaction = new Transaction();
    transaction.setAmount(50L);
    transaction.setFrom(eve);
    transaction.setTo(bob);

    Transaction newTransaction = assertDoesNotThrow(() -> this.transactionRepository.save(transaction));

    this.transactionRepository.delete(newTransaction);
  }

  @Test
  void whenTransactionValueIsInconsistent_thenThrowError() {
    Account eve = this.accountRepository.findByName("eve");
    Account bob = this.accountRepository.findByName("bob");

    Transaction transaction = new Transaction();
    transaction.setAmount(500L);
    transaction.setFrom(eve);
    transaction.setTo(bob);

    assertThrows(
      PSQLException.class,
      () -> this.transactionRepository.save(transaction)
    );
  }

}
