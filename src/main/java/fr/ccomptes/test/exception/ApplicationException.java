package fr.ccomptes.test.exception;

public class ApplicationException extends RuntimeException {
  public ApplicationException(final Type type, final String message, final Throwable cause) {
    super(message);
  }

  public enum Type {
    NOT_IMPLEMENTED,
    ILLEGAL_ARGUMENT,
    INTERNAL_EXCEPTION
  }
}