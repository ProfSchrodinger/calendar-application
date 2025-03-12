package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import exception.EventConflictException;
import exception.InvalidCommandException;
import model.CalendarModel;
import model.ICalendarModel;
import model.RecurringEvent;
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

  private enum Properties {
    subject,
    startDateTime,
    endDateTime,
    description,
    location,
    isPublic
  };

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
        throw new InvalidCommandException("Invalid command");
      }
    }
    catch (Exception e) {
      view.displayMessage(e.getMessage());
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

  private boolean checkValidPropertyValues(String property, String originalValue, String newValue) {
    try {
      Properties prop = Properties.valueOf(property);
      if (prop == Properties.startDateTime || prop == Properties.endDateTime) {
        return checkDateTimeValidity(originalValue) && checkDateTimeValidity(newValue);
      }
      else if (Properties.valueOf(property) == Properties.isPublic) {
        return (originalValue.equalsIgnoreCase("false") || originalValue.equalsIgnoreCase("true"))
                && (newValue.equalsIgnoreCase("false") || newValue.equalsIgnoreCase("true"));
      }
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  private boolean checkWeekDays(String weekDays) {
    String allowed = "MTWRFSU";
    for (char c : weekDays.toCharArray()) {
      if (allowed.indexOf(c) == -1) {
        return false;
      }
    }
    return true;
  }

  private boolean checkNvalue(String nvalue) {
    boolean isPositiveInteger = false;
    try {
      int value = Integer.parseInt(nvalue);
      isPositiveInteger = (value > 0);
    }
    catch (NumberFormatException e) {
      isPositiveInteger = false;
    }

    if (isPositiveInteger) {
      return true;
    }
    return false;
  }

  private void singleEventCreationHelper(List tokens, boolean autoDecline) {
    try {
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
            throw new InvalidCommandException("Invalid datetime or property");
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
            throw new InvalidCommandException("Invalid datetime or property");
          }
        }
      }
      else if (tokens.contains("on")) {
        if (autoDecline) {
          if (checkDateTimeValidity(tokens.get(5).toString())) {
            model.createSingleEvent(new SingleEvent(tokens.get(3).toString(),
                    getDateTime(tokens.get(5).toString()),
                    getDateTime(tokens.get(5).toString()).plusDays(1).withHour(0).withMinute(0),
                    "", "", false), true);
          }
          else {
            throw new InvalidCommandException("Invalid datetime or property");
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
            throw new InvalidCommandException("Invalid datetime or property");
          }
        }
      }
      else {
        throw new InvalidCommandException("Invalid command");
      }
    }
    catch (InvalidCommandException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Invalid command");
    }
  }

  private void recurringEventCreationHelper(List tokens) {
    try{
      if (tokens.contains("times")) {
        if (tokens.contains("to")) {
          if (checkDateTimeValidity(tokens.get(4).toString())
                  && checkDateTimeValidity(tokens.get(6).toString())
                  && getDateTime(tokens.get(4).toString()).isBefore(getDateTime(tokens.get(6).toString()))
                  && checkWeekDays(tokens.get(8).toString()) && checkNvalue(tokens.get(10).toString())
                  && getDateTime(tokens.get(4).toString()).toLocalDate().isEqual(getDateTime(tokens.get(6).toString()).toLocalDate())) {
            model.createRecurringEvent(new RecurringEvent(tokens.get(2).toString(),
                    getDateTime(tokens.get(4).toString()), getDateTime(tokens.get(6).toString()),
                    "", "", false, tokens.get(8).toString(),
                    Integer.parseInt(tokens.get(10).toString()),null));
          }
          else {
            throw new InvalidCommandException("Invalid datetime or property");
          }
        }
        else {
          if (checkDateValidity(tokens.get(4).toString())
                  && checkWeekDays(tokens.get(6).toString())
                  && checkNvalue(tokens.get(8).toString())) {
            model.createRecurringEvent(new RecurringEvent(tokens.get(2).toString(),
                    getDate(tokens.get(4).toString()).atStartOfDay(),
                    getDate(tokens.get(4).toString()).plusDays(1).atStartOfDay(),
                    "", "", false, tokens.get(6).toString(),
                    Integer.parseInt(tokens.get(8).toString()),null));
          }
          else {
            throw new InvalidCommandException("Invalid datetime or property");
          }
        }
      }
      else if (tokens.contains("until")) {
        if (tokens.contains("to")) {
          if (checkDateTimeValidity(tokens.get(4).toString())
                  && checkDateTimeValidity(tokens.get(6).toString())
                  && getDateTime(tokens.get(4).toString()).isBefore(getDateTime(tokens.get(6).toString()))
                  && checkWeekDays(tokens.get(8).toString()) && checkDateTimeValidity(tokens.get(10).toString())
                  && getDateTime(tokens.get(4).toString()).toLocalDate().isEqual(getDateTime(tokens.get(6).toString()).toLocalDate())) {
            model.createRecurringEvent(new RecurringEvent(tokens.get(2).toString(),
                    getDateTime(tokens.get(4).toString()), getDateTime(tokens.get(6).toString()),
                    "", "", false, tokens.get(8).toString(),
                    0, getDateTime(tokens.get(10).toString())));
          }
          else {
            throw new InvalidCommandException("Invalid datetime or property");
          }
        }
        else {
          if (checkDateValidity(tokens.get(4).toString())
                  && checkWeekDays(tokens.get(6).toString())
                  && checkDateValidity(tokens.get(8).toString())) {
            model.createRecurringEvent(new RecurringEvent(tokens.get(2).toString(),
                    getDate(tokens.get(4).toString()).atStartOfDay(),
                    getDate(tokens.get(4).toString()).plusDays(1).atStartOfDay(),
                    "", "", false, tokens.get(6).toString(),
                    0,getDate(tokens.get(8).toString()).atStartOfDay()));
          }
          else {
            throw new InvalidCommandException("Invalid datetime or property");
          }
        }
      }
      else {
        throw new InvalidCommandException("Invalid command");
      }
    }
    catch (InvalidCommandException | EventConflictException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Invalid command");
    }
  }

  private void processCreate(String command) {
    List tokens = extractDataFromCommand(command);

    boolean recurring = tokens.contains("repeats");

    if (!recurring) {
      boolean autoDecline = tokens.get(2).equals("--autoDecline");
      singleEventCreationHelper(tokens, autoDecline);
    }
    else {
      tokens.remove("--autoDecline");
      recurringEventCreationHelper(tokens);
    }
  }

  private void processEdit(String command) {
    List tokens = extractDataFromCommand(command);

    try {
      if (tokens.size() == 10) {
        String property = tokens.get(2).toString();
        String originalValue = tokens.get(3).toString();
        String startDateTime = tokens.get(5).toString();
        String endDateTime = tokens.get(7).toString();
        String newValue = tokens.get(9).toString();

        if (checkDateTimeValidity(startDateTime)
                && checkDateTimeValidity(endDateTime)
                && checkValidPropertyValues(property, originalValue, newValue)) {
          model.editEvents(property, originalValue, getDateTime(startDateTime), getDateTime(endDateTime), newValue);
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
      else if (tokens.size() == 8) {
        String property = tokens.get(2).toString();
        String originalValue = tokens.get(3).toString();
        String dateTime = tokens.get(5).toString();
        String newValue = tokens.get(7).toString();

        if (checkDateTimeValidity(dateTime) && checkValidPropertyValues(property, originalValue, newValue)) {
          model.editEvents(property, originalValue, getDateTime(dateTime), newValue);
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
      else if (tokens.size() == 5) {
        String property = tokens.get(2).toString();
        String originalValue = tokens.get(3).toString();
        String newValue = tokens.get(4).toString();

        if (checkValidPropertyValues(property, originalValue, newValue)) {
          model.editEvents(property, originalValue, newValue);
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
    }
    catch (InvalidCommandException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Invalid command");
    }
  }

  private void processPrint(String command) throws InvalidCommandException{
    List tokens = extractDataFromCommand(command);
    List<List> result = new ArrayList<>();

    try {
      if (tokens.contains("on")) {
        if (checkDateValidity(tokens.get(3).toString())) {
          result = model.getEventsOn(getDate(tokens.get(3).toString()));
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
      else if (tokens.contains("from")) {
        if (checkDateTimeValidity(tokens.get(3).toString())
                && checkDateTimeValidity(tokens.get(5).toString())
                && getDateTime(tokens.get(3).toString()).isBefore(getDateTime(tokens.get(5).toString()))) {
          result = model.getEventsBetween(getDateTime(tokens.get(3).toString()),
                  getDateTime(tokens.get(5).toString()));
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
      else if (tokens.contains("all")) {
        result = model.getEventsAll();
      }
      else {
        throw new InvalidCommandException("Invalid command");
      }

      view.displayMessage(result.toString());
    }
    catch (InvalidCommandException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Invalid Command");
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
      else {
        throw new InvalidCommandException("Invalid command");
      }

      view.displayMessage(String.valueOf(isBusy));
    }
    catch (InvalidCommandException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Invalid Command");
    }
  }

  private void processExport(String command) {
    List tokens = extractDataFromCommand(command);

    try {
      if (tokens.get(2).toString().toLowerCase().endsWith(".csv") && tokens.get(2).toString().length() > 4) {
        String filePath = model.exportCalendar(tokens.get(2).toString());

        view.displayMessage("File available at: " + filePath);
      }
      else {
        throw new InvalidCommandException("Invalid filename or extension");
      }
    }
    catch (InvalidCommandException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Invalid command");
    }
  }
}