package view;

import java.util.Scanner;

public class ConsoleView extends UserView{
  private Scanner scanner;

  public ConsoleView() {
    scanner = new Scanner(System.in);
  }

  @Override
  public void displayMessage(String message) {
    System.out.println(message);
  }

  @Override
  public String getInput() {
    return scanner.nextLine();
  }

  @Override
  public void displayError(String error) {
   System.out.println(error);
  }
}
