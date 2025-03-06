package exception;

public class EventConflictException extends RuntimeException {
  public EventConflictException(String message) {
    super(message);
    System.out.println(message);
  }
}
