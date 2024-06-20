package fr.ccomptes.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.ccomptes.test.application.AccountService;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.TransactionRepository;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class TestApplication implements CommandLineRunner {

  private static final String BANQUE = "banque";
  private static final String ALICE = "alice";
  private static final String BOB = "bob";
  private static final String EVE = "eve";

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }

  @Autowired
  AccountService accountService;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  TransactionRepository transactionRepository;

  @Override
  public void run(final String... args) throws Exception {
    // Création de comptes
    String[] names = {BANQUE, ALICE, BOB, EVE};
    Map<String, Long> accountIdsByNames = new HashMap<>();
    for (String name : names) {
      if (this.accountService.accountExists(name)) {
        Long id = this.accountService.getAccountByName(name).getId();
        accountIdsByNames.put(name, id);
      } else {
        this.accountService.createAccount(name);
      }
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
