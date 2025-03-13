package view;

import java.util.Scanner;

/**
 * This class represents a console base user interface for
 * interacting with calendar app.
 */

public class ConsoleView extends UserView{
  private Scanner scanner;

  /**
   * Constructs a console view and initializes the scanner for user input.
   */

  public ConsoleView() {
    scanner = new Scanner(System.in);
  }

  /**
   * Displays message to user via console.
   * @param message the message to be displayed.
   */

  @Override
  public void displayMessage(String message) {
    System.out.println(message);
  }

  /**
   * Reads and returns user input from console.
   * @return user input as string.
   */

  @Override
  public String getInput() {
    return scanner.nextLine();
  }
}
