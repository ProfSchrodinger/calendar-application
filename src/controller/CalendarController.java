package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import exception.InvalidCommandException;
import model.CalendarEvent;
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
  private static final DateTimeFormatter DATE_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

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

  private boolean checkDateTimeValidity(String date) {
    try {
      LocalDateTime.parse(date, DATE_TIME_FORMATTER);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  private boolean checkDateValidity(String date) {
    try {
      LocalDate.parse(date, DATE_FORMATTER);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  private LocalDateTime getDateTime(String date) {
    return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
  }

  private LocalDate getDate(String date) {
    return LocalDate.parse(date, DATE_FORMATTER);
  }

  private void processCreate(String command) {
    List tokens = extractDataFromCommand(command);

    System.out.println(tokens);
    boolean recurring = tokens.contains("repeats");

    if (!recurring) {
      boolean autoDecline = tokens.get(2).equals("--autoDecline");

      if (tokens.contains("from")) {
        if (autoDecline) {
          if (checkDateTimeValidity(tokens.get(5).toString())
                  && checkDateTimeValidity(tokens.get(7).toString())
                  && getDateTime(tokens.get(5).toString()).isBefore(getDateTime(tokens.get(7).toString()))) {

            model.createSingleEvent(new SingleEvent(tokens.get(3).toString(),
                    getDateTime(tokens.get(5).toString()),
                    getDateTime(tokens.get(7).toString()),
                    "", "", false), true);
          }
          else {
            throw new InvalidCommandException("Invalid date format");
          }
        }
        else {
          if (checkDateTimeValidity(tokens.get(4).toString())
                  && checkDateTimeValidity(tokens.get(6).toString())
                  && getDateTime(tokens.get(4).toString()).isBefore(getDateTime(tokens.get(6).toString()))) {

            model.createSingleEvent(new SingleEvent(tokens.get(2).toString(),
                    getDateTime(tokens.get(4).toString()),
                    getDateTime(tokens.get(6).toString()),
                    "", "", false), false);
          }
          else {
            throw new InvalidCommandException("Invalid date format");
          }
        }
      }
      else {
        if (autoDecline) {
          if (checkDateTimeValidity(tokens.get(5).toString())) {
            model.createSingleEvent(new SingleEvent(tokens.get(3).toString(),
                    getDateTime(tokens.get(5).toString()),
                    getDateTime(tokens.get(5).toString()).plusDays(1).withHour(0).withMinute(0),
                    "", "", false), true);
          }
          else {
            throw new InvalidCommandException("Invalid date format");
          }
        }
        else {
          if (checkDateTimeValidity(tokens.get(4).toString())) {
            model.createSingleEvent(new SingleEvent(tokens.get(2).toString(),
                    getDateTime(tokens.get(4).toString()),
                    getDateTime(tokens.get(4).toString()).plusDays(1).withHour(0).withMinute(0),
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

  private void processPrint(String command) throws InvalidCommandException{
    List tokens = extractDataFromCommand(command);
    List<List> result = new ArrayList<>();

    try {
      if (tokens.contains("on")) {
        if (checkDateValidity(tokens.get(3).toString())) {
          result = model.getEventsOn(getDate(tokens.get(3).toString()));
        }
      }
      else if (tokens.contains("from")) {
        if (checkDateTimeValidity(tokens.get(3).toString())
                && checkDateTimeValidity(tokens.get(5).toString())
                && getDateTime(tokens.get(3).toString()).isBefore(getDateTime(tokens.get(5).toString()))) {
          result = model.getEventsBetween(getDateTime(tokens.get(3).toString()),
                  getDateTime(tokens.get(5).toString()));
        }
      }
      else if (tokens.contains("all")) {
        result = model.getAllEvents();
      }

      view.displayMessage(result.toString());
    }
    catch (Exception e) {
      throw new InvalidCommandException("Unknown command: " + command);
    }
  }

  private void processShow(String command) {
    List tokens = extractDataFromCommand(command);
    boolean isBusy = false;

    try {
      if (tokens.contains("on")) {
        if (checkDateTimeValidity(tokens.get(3).toString())) {
          isBusy = model.isBusy(getDateTime(tokens.get(3).toString()));
        }
      }

      view.displayMessage(String.valueOf(isBusy));
    }
    catch (Exception e) {
      throw new InvalidCommandException("Unknown command: " + command);
    }
  }

  private void processExport(String command) {
    List tokens = extractDataFromCommand(command);
  }
}