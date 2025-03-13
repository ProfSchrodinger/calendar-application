package exception;

/**
 * Exception thrown when user inputs an invalid command.
 */

public class InvalidCommandException extends RuntimeException {

  /**
   * Constructs a new invalid command exception with a detailed message.
   * @param message message explaining the invalid command.
   */

  public InvalidCommandException(String message) {
    super(message);
  }
}
