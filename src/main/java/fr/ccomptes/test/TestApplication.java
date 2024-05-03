package fr.ccomptes.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.ccomptes.test.application.AccountService;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.TransactionRepository;

@SpringBootApplication
public class TestApplication implements CommandLineRunner {

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
	public void run(String... args) throws Exception {
		// Création de comptes
		String[] names = { "banque", "alice", "bob", "eve" };
		for (String name : names) {
			if (accountService.accountExists(name)) {
				continue;
			}
			accountService.createAccount(name);
		}
		// Banque créditée de 1 000 000
		accountService.setBalance("banque", 1_000_000L);
		// Transactions entre comptes
		if (transactionRepository.count() == 0) {
			accountService.addTransaction("banque", "alice", 1000L);
			accountService.addTransaction("alice", "bob", 10L);
			accountService.addTransaction("alice", "bob", 30L);
			accountService.addTransaction("alice", "bob", 50L);
			accountService.addTransaction("alice", "eve", 100L);
			accountService.addTransaction("bob", "eve", 25L);
			accountService.addTransaction("bob", "alice", 40L);
		}

	}

}
