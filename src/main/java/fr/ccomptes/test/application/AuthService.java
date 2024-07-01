package fr.ccomptes.test.application;

import fr.ccomptes.test.domain.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AuthService {

  public static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
  private static final SecureRandom secureRandom = new SecureRandom();
  private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

  @Autowired
  AccountRepository accountRepository;

  /**
   * Generate a SecureRandom token on 24 bytes.
   *
   * @return A URL safe string representing the SecureRandom token on base64.
   */
  public String generateNewToken() {
    byte[] randomBytes = new byte[24];
    secureRandom.nextBytes(randomBytes);
    return base64Encoder.encodeToString(randomBytes);
  }


  /**
   * Validate the auth with the pair apiKey/AccountId.
   * Returns nothing if the auth is valid.
   *
   * @param apiKey
   * @param accountId
   * @throws CredentialException if auth is invalid
   */
  public void validateAuth(final String apiKey, final long accountId) throws CredentialException {
    try {
      if (apiKey == null || !this.accountRepository.existsByIdAndApiKey(accountId, apiKey)) {
        throw new CredentialException("La clé API est invalide");
      }
    } catch (Exception e) {
      throw new CredentialException("La clé API est invalide");
    }
  }
}
