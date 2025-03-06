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

    ICalendarModel model = new CalendarModel();
    UserView view = new ConsoleView();
    CalendarController controller = new CalendarController();

    if (args[0].equalsIgnoreCase("--mode")) {
      if (mode.equals("interactive")) {
        view.displayMessage("Interactive mode on. Type 'exit' to quit");
        while (true) {
          String command = view.getInput();
          controller.processCommand(command);
        }
      } else if (mode.equals("headless")) {
        // Yet to implement
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

dfjjksdh
