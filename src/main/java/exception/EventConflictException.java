package exception;

/**
 * Exception thrown when an event conflict occurs with an existing event in calendar.
 */

public class EventConflictException extends RuntimeException {

  /**
   * Constructs a new event conflict exception with a detailed message.
   * @param message detailed message explaining the conflict.
   */

  public EventConflictException(String message) {
    super(message);
  }
}
