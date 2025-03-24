import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import exception.InvalidCommandException;
import view.ConsoleView;
import controller.CalendarController;
import view.UserView;

/**
 * This class represents the main entry point of the calendar application.
 * It supports two modes : interactive and headless.
 */

public class CalendarApp {

  /**
   * The main method that starts the calendar application.
   * @param args Command line arguments.
   * @throws InvalidCommandException If an invalid command is encountered.
   */

  public static void main(String[] args) throws InvalidCommandException {
    UserView view = new ConsoleView();
    CalendarController controller = new CalendarController();

    if (args.length < 2) {
      view.displayMessage("Invalid mode, use: --mode interactive " +
              "OR --mode headless <commandFile>");
      System.exit(1);
    }

    String mode = args[1].toLowerCase();

    if (args[0].equalsIgnoreCase("--mode")) {
      if (mode.equals("interactive")) {
        view.displayMessage("Interactive mode on. Type 'exit' to quit");
        try {
          while (true) {
            String command = view.getInput();
            controller.processCommand(command);
          }
        } catch (Exception e) {
          view.displayMessage(e.getMessage());
          controller.processCommand("exit");
        }
      }
      else if (mode.equals("headless")) {
        view.displayMessage("Headless mode on. Provide the absolute path to the command.txt file");
        String filePath = view.getInput();
        List<String> lines = List.of();

        try {
          lines = Files.readAllLines(Paths.get(filePath));
        }
        catch (IOException e) {
          view.displayMessage("Invalid Path: " + e.getMessage());
        }

        if (lines.size() >= 2
                && lines.get(lines.size() - 1).trim().equalsIgnoreCase("exit")) {
          try{
            for (String command : lines) {
              controller.processCommand(command);
            }
          }
          catch (Exception e) {
            view.displayMessage(e.getMessage());
            controller.processCommand("exit");
          }
        }
        else {
          view.displayMessage("File does not contain exit command or less than 2 commands");
        }
      }
      else {
        view.displayMessage("Invalid mode: " + mode);
      }
    }
    else {
      view.displayMessage("First argument must be --mode");
    }
  }
}
