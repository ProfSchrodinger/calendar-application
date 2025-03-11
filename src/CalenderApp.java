import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import exception.InvalidCommandException;
import model.ICalendarModel;
import model.CalendarModel;
import view.ConsoleView;
import controller.CalendarController;
import view.UserView;

public class CalenderApp {
  public static void main(String[] args) throws InvalidCommandException {
    if (args.length < 2) {
      new InvalidCommandException("Invalid mode, use: --mode interactive " +
              "OR --mode headless <commandFile>");
      System.exit(1);
    }

    String mode = args[1].toLowerCase();

    UserView view = new ConsoleView();
    CalendarController controller = new CalendarController();

    if (args[0].equalsIgnoreCase("--mode")) {
      if (mode.equals("interactive")) {
        view.displayMessage("Interactive mode on. Type 'exit' to quit");
        while (true) {
          String command = view.getInput();
          controller.processCommand(command);
        }
      }
      else if (mode.equals("headless")) {
        view.displayMessage("Headless mode on. Provide the absolute path to the command.txt file");
        String filePath = view.getInput();
        List<String> lines;
        try {
          lines = Files.readAllLines(Paths.get(filePath));
        }
        catch (Exception e) {
          throw new InvalidCommandException("Invalid Path: " + e.getMessage());
        }
        if (lines.size() >= 2
                && lines.get(lines.size() - 1).trim().equalsIgnoreCase("exit")) {
          for (String command : lines) {
            controller.processCommand(command);
          }
        }
        else {
          throw new InvalidCommandException("File does not contain exit command or less than 2 commands");
        }
      }
      else {
        new InvalidCommandException("Invalid mode: " + mode);
      }
    }
    else {
      new InvalidCommandException("First argument must be --mode");
    }
  }
}
