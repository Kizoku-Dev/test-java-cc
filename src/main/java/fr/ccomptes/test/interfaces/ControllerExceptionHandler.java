package fr.ccomptes.test.interfaces;

import fr.ccomptes.test.application.exception.AccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.CredentialException;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(final IllegalArgumentException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccountException.class)
  public ResponseEntity<String> handleAccountException(final AccountException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(CredentialException.class)
  public ResponseEntity<String> handleCredentialException(final CredentialException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
  }
}
