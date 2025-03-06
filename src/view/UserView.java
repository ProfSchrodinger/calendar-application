package view;

public abstract class UserView {
  public abstract void displayMessage(String message);
  public abstract String getInput();
  public abstract void displayError(String error);
}
