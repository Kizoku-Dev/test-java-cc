package fr.ccomptes.test.application.exception;

public class AccountException extends RuntimeException {
  public AccountException(final String message) {
    super(message);
  }
}