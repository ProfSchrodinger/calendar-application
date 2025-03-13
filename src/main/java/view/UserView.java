package view;

/**
 * This abstract class represents the structure for user interface interactions.
 */

public abstract class UserView {

  /**
   * Displays message to user.
   * @param message the message to be displayed.
   */

  public abstract void displayMessage(String message);

  /**
   * Retrieves user input.
   * @return user input as string.
   */

  public abstract String getInput();
}
