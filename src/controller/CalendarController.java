package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import exception.InvalidCommandException;
import model.CalendarModel;
import model.ICalendarModel;
import model.SingleEvent;
import view.ConsoleView;
import view.UserView;

public class CalendarController {
  private ICalendarModel model;
  private UserView view;
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);

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
      if (command.toLowerCase().startsWith("create event")) {
        view.displayMessage("Processing create event command: " + command);
        processCreate(command);
      }
      else if (command.toLowerCase().startsWith("edit event")) {
        view.displayMessage("Processing edit event command: " + command);
        processEdit(command);
      }
      else if (command.toLowerCase().startsWith("print events")) {
        view.displayMessage("Processing print events command: " + command);
        processPrint(command);
      }
      else if (command.toLowerCase().startsWith("show status")) {
        view.displayMessage("Processing show status command: " + command);
        processShow(command);
      }
      else if (command.toLowerCase().startsWith("export cal")) {
        view.displayMessage("Processing export calendar command: " + command);
        processExport(command);
      }
      else {
        throw new InvalidCommandException("Unknown command: " + command);
      }
    }
    catch (Exception e) {
      view.displayError("Unexpected error: " + e.getMessage());
    }
  }

  private List extractDataFromCommand(String command) {
    List<String> tokens = new ArrayList<>();
    boolean insideQuotes = false;
    StringBuilder captured = new StringBuilder();

    for (int i = 0; i < command.length(); i++) {
      if (command.charAt(i) == ' ' && !insideQuotes) {
        tokens.add(captured.toString());
        captured = new StringBuilder();
      }
      else if (command.charAt(i) == '"' || command.charAt(i) == '\'') {
        insideQuotes = !insideQuotes;
      }
      else {
        captured.append(command.charAt(i));
      }
    }
    tokens.add(captured.toString());

    return tokens;
  }

  private boolean checkDateValidity(String date) {
    try {
      LocalDateTime.parse(date, DATE_TIME_FORMATTER);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  private void processCreate(String command) {
    List tokens = extractDataFromCommand(command);

    boolean recurring = false;
    if (tokens.contains("repeats")) {
      recurring = true;
    }

    if (!recurring) {
      boolean autoDecline = false;
      if (tokens.get(2).equals("--autoDecline")) {
        autoDecline = true;
      }

      if (tokens.contains("from")) {
        if (autoDecline) {
          if (checkDateValidity(tokens.get(6).toString())
                  && checkDateValidity(tokens.get(8).toString())
                  && LocalDateTime.parse(tokens.get(6).toString(), DATE_TIME_FORMATTER).
                  isBefore(LocalDateTime.parse(tokens.get(8).toString(), DATE_TIME_FORMATTER))) {

            model.createSingleEvent(new SingleEvent(tokens.get(4).toString(),
                    LocalDateTime.parse(tokens.get(6).toString(), DATE_TIME_FORMATTER),
                    LocalDateTime.parse(tokens.get(8).toString(), DATE_TIME_FORMATTER),
                    "", "", false), true);
          }
          else {
            throw new InvalidCommandException("Invalid date format");
          }
        }
        else {
          if (checkDateValidity(tokens.get(5).toString())
                  && checkDateValidity(tokens.get(7).toString())
                  && LocalDateTime.parse(tokens.get(5).toString(), DATE_TIME_FORMATTER).
                  isBefore(LocalDateTime.parse(tokens.get(7).toString(), DATE_TIME_FORMATTER))) {

            model.createSingleEvent(new SingleEvent(tokens.get(3).toString(),
                    LocalDateTime.parse(tokens.get(5).toString(), DATE_TIME_FORMATTER),
                    LocalDateTime.parse(tokens.get(7).toString(), DATE_TIME_FORMATTER),
                    "", "", false), false);
          }
          else {
            throw new InvalidCommandException("Invalid date format");
          }
        }
      }
      else {
        if (autoDecline) {
          if (checkDateValidity(tokens.get(6).toString())) {
            model.createSingleEvent(new SingleEvent(tokens.get(4).toString(),
                    LocalDateTime.parse(tokens.get(6).toString(), DATE_TIME_FORMATTER),
                    LocalDateTime.parse(tokens.get(6).toString(), DATE_TIME_FORMATTER).plusDays(1).withHour(0).withMinute(0),
                    "", "", false), true);
          }
          else {
            throw new InvalidCommandException("Invalid date format");
          }
        }
        else {
          if (checkDateValidity(tokens.get(5).toString())) {
            model.createSingleEvent(new SingleEvent(tokens.get(3).toString(),
                    LocalDateTime.parse(tokens.get(5).toString(), DATE_TIME_FORMATTER),
                    LocalDateTime.parse(tokens.get(5).toString(), DATE_TIME_FORMATTER).plusDays(1).withHour(0).withMinute(0),
                    "", "", false), false);
          }
          else {
            throw new InvalidCommandException("Invalid date format");
          }
        }
      }
    }
    else {
      tokens.remove("--autoDecline");
    }
  }

  private void processEdit(String command) {
    List tokens = extractDataFromCommand(command);
  }

  private void processPrint(String command) {
    List tokens = extractDataFromCommand(command);
  }

  private void processShow(String command) {
    List tokens = extractDataFromCommand(command);
  }

  private void processExport(String command) {
    List tokens = extractDataFromCommand(command);
  }
}