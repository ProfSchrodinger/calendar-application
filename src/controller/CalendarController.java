package controller;

import exception.EventConflictException;
import exception.InvalidCommandException;
import model.CalendarModel;
import model.ICalendarModel;
import view.ConsoleView;
import view.UserView;

public class CalendarController {
  private ICalendarModel model;
  private UserView view;

  public CalendarController() {
    model = new CalendarModel();
    view = new ConsoleView();
  }

  public void processCommand(String command) {
    if (command.equalsIgnoreCase("exit")) {
      view.displayMessage("Exiting application.");
      System.exit(0);
    }

    try {
      // Example handling for a few commands (to be expanded)
      if (command.toLowerCase().startsWith("create event")) {
        view.displayMessage("Processing create event command: " + command);
        // Command parsing and model invocation goes here.
      }
      else if (command.toLowerCase().startsWith("edit event")) {
        view.displayMessage("Processing edit event command: " + command);
      }
      else if (command.toLowerCase().startsWith("print events")) {
        view.displayMessage("Processing print events command: " + command);
      }
      else if (command.toLowerCase().startsWith("show status")) {
        view.displayMessage("Processing show status command: " + command);
      }
      else if (command.toLowerCase().startsWith("export cal")) {
        view.displayMessage("Processing export calendar command: " + command);
      }
      else {
        throw new InvalidCommandException("Unknown command: " + command);
      }
    }
    catch (Exception e) {
      view.displayError("Unexpected error: " + e.getMessage());
    }
  }
}