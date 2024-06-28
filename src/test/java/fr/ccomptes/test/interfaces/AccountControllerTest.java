package fr.ccomptes.test.interfaces;

import fr.ccomptes.test.application.AccountService;
import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.AccountRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountControllerTest {

  @LocalServerPort
  private Integer port;

  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Test
  void whenNameHasValue_thenListAccountByNameCaseInsensitive() {
    Account newAccount1 = new Account("newAccountOne");
    Account newAccount2 = new Account("newAccountTwo");
    this.accountRepository.save(newAccount1);
    this.accountRepository.save(newAccount2);

    List<Account> expected = List.of(newAccount1, newAccount2);
    List<Account> accounts = this.accountService.listAccounts("NeWaCc");
    assertIterableEquals(expected, accounts);

    this.accountRepository.delete(newAccount1);
    this.accountRepository.delete(newAccount2);
  }

  @Test
  void whenNameHasNullValue_thenListAllAccounts() {
    List<Account> accounts = this.accountService.listAccounts(null);
    assertEquals(4, accounts.size());
  }

  @Test
  void whenNameHasBlankValue_thenListAllAccounts() {
    List<Account> accounts = this.accountService.listAccounts("");
    assertEquals(4, accounts.size());
  }

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + this.port;
  }

}
