package fr.ccomptes.test.interfaces;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ccomptes.test.application.AccountService;
import fr.ccomptes.test.application.AuthService;
import fr.ccomptes.test.domain.Account;
import fr.ccomptes.test.domain.AccountRepository;
import fr.ccomptes.test.domain.TransactionRepository;
import fr.ccomptes.test.interfaces.dto.AccountListResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountControllerTest {

  @LocalServerPort
  private Integer port;

  private String baseUri;

  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  TransactionRepository transactionRepository;

  @Test
  void whenNameHasValue_thenListAccountByNameCaseInsensitive() {
    Account newAccount1 = new Account("newAccountOne");
    newAccount1.setApiKey("toto");
    Account newAccount2 = new Account("newAccountTwo");
    newAccount2.setApiKey("toto");
    this.accountRepository.save(newAccount1);
    this.accountRepository.save(newAccount2);

    List<Account> expected = List.of(newAccount1, newAccount2);
    List<Account> accounts = this.accountService.listAccounts("NeWaCc");
    assertIterableEquals(expected, accounts);
  }

  @Test
  void whenNameHasNullValue_thenListAllAccounts() throws IOException {
    HttpUriRequest request = new HttpGet(this.baseUri + "/accounts");
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    AccountListResponse[] accounts = retrieveResourceFromResponse(
      httpResponse, AccountListResponse[].class);
    assertEquals(4, accounts.length);
  }

  @Test
  void whenNameHasBlankValue_thenListAllAccounts() throws IOException {
    HttpUriRequest request = new HttpGet(this.baseUri + "/accounts?name=");
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    AccountListResponse[] accounts = retrieveResourceFromResponse(
      httpResponse, AccountListResponse[].class);
    assertEquals(4, accounts.length);
  }

  @Test
  void whenApiKey_thenAddTransactionSucceeds() throws IOException {
    String apiKey = "toto";
    Account newAccount1 = new Account("newAccountOne");
    newAccount1.setApiKey(apiKey);
    newAccount1.setBalance(100);
    newAccount1 = this.accountRepository.save(newAccount1);
    Account newAccount2 = new Account("newAccountTwo");
    newAccount2.setBalance(100);
    newAccount2 = this.accountRepository.save(newAccount2);

    String body = "{ \"srcId\": " + newAccount1.getId() + ", \"destId\": " + newAccount2.getId() + ", \"amount\": 25 }";
    HttpPost request = new HttpPost(this.baseUri + "/transactions");
    request.setHeader(AuthService.AUTH_TOKEN_HEADER_NAME, apiKey);
    request.setHeader("Content-Type", "application/json");
    HttpEntity entity = new ByteArrayEntity(body.getBytes(StandardCharsets.UTF_8));
    request.setEntity(entity);
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    assertEquals(
      HttpStatus.SC_OK,
      httpResponse.getStatusLine().getStatusCode()
    );
  }

  @Test
  void whenNoApiKey_thenAddTransactionFails() throws IOException {
    String apiKey = "toto";
    Account newAccount1 = new Account("newAccountOne");
    newAccount1.setApiKey(apiKey);
    this.accountRepository.save(newAccount1);

    String body = "{ \"srcId\": " + newAccount1.getId() + ", \"destId\": 3, \"amount\": 25 }";
    HttpPost request = new HttpPost(this.baseUri + "/transactions");
    request.setHeader("Content-Type", "application/json");
    HttpEntity entity = new ByteArrayEntity(body.getBytes(StandardCharsets.UTF_8));
    request.setEntity(entity);
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    assertEquals(
      HttpStatus.SC_UNAUTHORIZED,
      httpResponse.getStatusLine().getStatusCode()
    );
  }

  @BeforeEach
  void setUp() {
    this.baseUri = "http://localhost:" + this.port;
  }

  public static <T> T retrieveResourceFromResponse(
    final HttpResponse response,
    final Class<T> clazz)
    throws IOException {

    String jsonFromResponse = EntityUtils.toString(response.getEntity());
    ObjectMapper mapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper.readValue(jsonFromResponse, clazz);
  }

}
