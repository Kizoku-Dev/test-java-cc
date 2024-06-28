package fr.ccomptes.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.ccomptes.test.application.AccountService;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.TransactionRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableTransactionManagement
public class TestApplication implements CommandLineRunner {

  private static final String BANQUE = "banque";
  private static final String ALICE = "alice";
  private static final String BOB = "bob";
  private static final String EVE = "eve";

  public static void main(final String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }

  @Autowired
  AccountService accountService;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  TransactionRepository transactionRepository;

  @Override
  public void run(final String... args) {
    // Création de comptes
    String[] names = {BANQUE, ALICE, BOB, EVE};
    Map<String, Long> accountIdsByNames = new HashMap<>();
    for (String name : names) {
      Long id;
      if (this.accountService.accountExists(name)) {
        id = this.accountService.getAccountByName(name).getId();
      } else {
        id = this.accountService.createAccount(name).getId();
      }
      accountIdsByNames.put(name, id);
    }
    // Banque créditée de 1 000 000
    this.accountService.setBalance(BANQUE, 1_000_000L);
    // Transactions entre comptes
    if (this.transactionRepository.count() == 0) {
      this.accountService.addTransaction(accountIdsByNames.get(BANQUE), accountIdsByNames.get(ALICE), 1000L);
      this.accountService.addTransaction(accountIdsByNames.get(ALICE), accountIdsByNames.get(BOB), 10L);
      this.accountService.addTransaction(accountIdsByNames.get(ALICE), accountIdsByNames.get(BOB), 30L);
      this.accountService.addTransaction(accountIdsByNames.get(ALICE), accountIdsByNames.get(BOB), 50L);
      this.accountService.addTransaction(accountIdsByNames.get(ALICE), accountIdsByNames.get(EVE), 100L);
      this.accountService.addTransaction(accountIdsByNames.get(BOB), accountIdsByNames.get(EVE), 25L);
      this.accountService.addTransaction(accountIdsByNames.get(BOB), accountIdsByNames.get(ALICE), 40L);
    }

  }

}
