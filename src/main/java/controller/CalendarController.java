package controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import exception.EventConflictException;
import exception.InvalidCommandException;
import model.CalendarManager;
import model.RecurringEvent;
import model.SingleEvent;
import utilities.CSVExporter;
import view.ConsoleView;
import view.UserView;

/**
 * CalendarController class manages input from user,
 * processes commands and displays result to user.
 * It manages interaction between calendar model and user view.
 */

public class CalendarController {
  CalendarManager model;
  UserView view;

  /**
   * Formatter for date and time.
   */

  static final DateTimeFormatter DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);

  /**
   * Formatter for date.
   */

  static final DateTimeFormatter DATE_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

  /**
   * Enum representing the properties of the event.
   */

  enum Properties {
    subject,
    startDateTime,
    endDateTime,
    description,
    location,
    isPublic
  }

  /**
   * Constructs a calendar controller with model and view.
   */

  public CalendarController() {
    model = new CalendarManager();
    view = new ConsoleView();
  }

  /**
   * Method to process the user's command and perform the action needed.
   * @param command the user's command.
   */

  public void processCommand(String command) {
    if (command.trim().equalsIgnoreCase("exit")) {
      // view.displayMessage("Exiting application.");
      System.exit(0);
    }

    try {
      if (command.toLowerCase().startsWith("create calendar")) {
        processCreateCalendar(command);
        // view.displayMessage("Command processed: " + command);
      }
      else if (command.toLowerCase().startsWith("edit calendar")) {
        processEditCalendar(command);
        // view.displayMessage("Command processed: " + command);
      }
      else if (command.toLowerCase().startsWith("use calendar")) {
        processUseCalendar(command);
        // view.displayMessage("Command processed: " + command);
      }
      else if (command.toLowerCase().startsWith("create event")) {
        processCreate(command);
        // view.displayMessage("Command processed: " + command);
      }
      else if (command.toLowerCase().startsWith("edit event")) {
        processEdit(command);
        // view.displayMessage("Command processed: " + command);
      }
      else if (command.toLowerCase().startsWith("print events")) {
        processPrint(command);
        // view.displayMessage("Command processed: " + command);
      }
      else if (command.toLowerCase().startsWith("show status")) {
        processShow(command);
        // view.displayMessage("Command processed: " + command);
      }
      else if (command.toLowerCase().startsWith("export cal")) {
        processExport(command);
        // view.displayMessage("Command processed: " + command);
      }
      else if (command.toLowerCase().startsWith("copy event")) {
        processCopyEvents(command);
        // view.displayMessage("Command processed: " + command);
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

  /**
   * Extracts command arguments from the input string.
   * @param command the input command string.
   * @return list of tokens extracted.
   */

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

  /**
   * Function to check if the passed value is a valid ZoneID.
   * @param zoneID the string format of ZoneID passed.
   * @return True is valid, false other cases.
   */

  private boolean checkValidZoneID(String zoneID) {
    try {
      ZoneId zone = ZoneId.of(zoneID);
      return true;
    }
    catch (DateTimeException e) {
      return false;
    }
  }

  /**
   * Function to create a Calendar.
   * @param command the list of commands.
   */

  private void processCreateCalendar(String command) {
    List<String> tokens = extractDataFromCommand(command);

    if (!tokens.get(2).equalsIgnoreCase("--name")
            || !tokens.get(4).equalsIgnoreCase("--timezone")
            || tokens.size() != 6) {
      throw new InvalidCommandException("Invalid create calendar command format.");
    }

    try {
      if (checkValidZoneID(tokens.get(5))) {
        model.createCalendar(tokens.get(3), ZoneId.of(tokens.get(5)));
      }
      else {
        throw new InvalidCommandException("Invalid Zone ID.");
      }
    }
    catch (InvalidCommandException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Error creating calendar");
    }
  }

  /**
   * Function to edit a Calendar.
   * @param command the list of commands.
   */

  private void processEditCalendar(String command) {
    List<String> tokens = extractDataFromCommand(command);

    if (tokens.size() != 7
            || !tokens.get(2).equalsIgnoreCase("--name")
            || !tokens.get(4).equalsIgnoreCase("--property")) {
      throw new InvalidCommandException("Invalid edit calendar command format.");
    }

    String calName = tokens.get(3);
    String property = tokens.get(5).toLowerCase();
    String newValue = tokens.get(6);

    try {
      if (property.equals("name")) {
        model.changeCalendarName(calName, newValue);
      }
      else if (property.equals("timezone")
              && checkValidZoneID(newValue)) {
        model.changeCalendarTimeZone(calName, ZoneId.of(newValue));
      }
      else {
        throw new InvalidCommandException("Invalid ZoneID.");
      }
    }
    catch (InvalidCommandException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Error editing calendar");
    }
  }

  /**
   * Function to set a Calendar.
   * @param command the list of commands.
   */

  private void processUseCalendar(String command) {
    List<String> tokens = extractDataFromCommand(command);

    if (tokens.size() != 4
            || !tokens.get(2).equalsIgnoreCase("--name")) {
      throw new InvalidCommandException("Invalid use calendar command format.");
    }

    try {
      String calName = tokens.get(3);
      model.switchCalendar(calName);
    }
    catch (InvalidCommandException e) {
      throw e;
    }
  }

  private void processCopyEvents(String command) {
    List<String> tokens = extractDataFromCommand(command);

    try {
      if (tokens.size() == 9) {
        String eventName = tokens.get(2);
        String copyDate = tokens.get(4);
        String targetCalendar = tokens.get(6);
        String targetDate = tokens.get(8);

        if (checkDateTimeValidity(copyDate) && checkDateTimeValidity(targetDate)) {
          model.copyEvents(eventName, getDateTime(copyDate),
                  targetCalendar, getDateTime(targetDate));
        }
        else {
          throw new InvalidCommandException("Invalid date formats");
        }
      }
      else if (tokens.size() == 8) {
        String copyDate = tokens.get(3);
        String targetCalendar = tokens.get(5);
        String targetDate = tokens.get(7);

        if (checkDateValidity(copyDate) && checkDateValidity(targetDate)) {
          model.copyEvents(getDate(copyDate), targetCalendar,
                  getDate(targetDate));
        }
        else {
          throw new InvalidCommandException("Invalid date formats");
        }
      }
      else if (tokens.size() == 10) {
        String copyDateStart = tokens.get(3);
        String copyDateEnd = tokens.get(5);
        String targetCalendar = tokens.get(7);
        String targetDate = tokens.get(9);

        if (checkDateValidity(copyDateStart) && checkDateValidity(copyDateEnd)
                && checkDateValidity(targetDate)) {
          model.copyEvents(getDate(copyDateStart), getDate(copyDateEnd),
                  targetCalendar, getDate(targetDate));
        }
        else {
          throw new InvalidCommandException("Invalid date formats");
        }
      }
      else {
        throw new InvalidCommandException("Invalid copy events command format.");
      }
    } catch (InvalidCommandException e) {
      throw e;
    } catch (Exception e) {
      throw new InvalidCommandException("Invalid command");
    }
  }

  /**
   * Checks if given date and time is in valid format.
   * @param date date and time string.
   * @return true if valid, false if not valid.
   */

  boolean checkDateTimeValidity(String date) {
    try {
      LocalDateTime.parse(date, DATE_TIME_FORMATTER);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Checks if date is in valid format.
   * @param date date string.
   * @return true if valid, false if not valid.
   */

  private boolean checkDateValidity(String date) {
    try {
      LocalDate.parse(date, DATE_FORMATTER);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Parses date and time string into LocalDateTime object.
   * @param date date time string.
   * @return LocalDateTime object.
   */

  private LocalDateTime getDateTime(String date) {
    return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
  }

  /**
   * Parses date string into LocalDate object.
   * @param date date string.
   * @return LocalDate object
   */

  private LocalDate getDate(String date) {
    return LocalDate.parse(date, DATE_FORMATTER);
  }

  /**
   * Checks if the property and value is valid.
   * @param property property name.
   * @param newValue new value .
   * @return true if valid, false if not valid.
   */

  private boolean checkValidPropertyValues(String property, String newValue) {
    try {
      Properties prop = Properties.valueOf(property);
      if (prop == Properties.startDateTime || prop == Properties.endDateTime) {
        return checkDateTimeValidity(newValue);
      }
      else if (Properties.valueOf(property) == Properties.isPublic) {
        return (newValue.equalsIgnoreCase("false") || newValue.equalsIgnoreCase("true"));
      }
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Check if weekdays string is valid.
   * @param weekDays the weekdays string.
   * @return true if valid, false if not.
   */

  private boolean checkWeekDays(String weekDays) {
    String allowed = "MTWRFSU";
    for (char c : weekDays.toCharArray()) {
      if (allowed.indexOf(c) == -1) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the Nvalue string is a positive integer.
   * @param nvalue string to check.
   * @return true or false.
   */

  private boolean checkNvalue(String nvalue) {
    boolean isPositiveInteger = false;
    try {
      int value = Integer.parseInt(nvalue);
      isPositiveInteger = (value > 0);
    }
    catch (NumberFormatException e) {
      isPositiveInteger = false;
    }
    return isPositiveInteger;
  }

  /**
   * Method to create a single event based on user input.
   * @param tokens the list of commands.
   */

  private void singleEventCreationHelper(List tokens) {
    try {
      if (tokens.contains("from")) {
        if (checkDateTimeValidity(tokens.get(4).toString())
                && checkDateTimeValidity(tokens.get(6).toString())
                && getDateTime(tokens.get(4).toString())
                .isBefore(getDateTime(tokens.get(6).toString()))) {

          model.createSingleEvent(new SingleEvent(tokens.get(2).toString(),
                  getDateTime(tokens.get(4).toString()),
                  getDateTime(tokens.get(6).toString()),
                  "", "", false));
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
      else if (tokens.contains("on")) {
        if (checkDateTimeValidity(tokens.get(4).toString())) {

          model.createSingleEvent(new SingleEvent(tokens.get(2).toString(),
                  getDateTime(tokens.get(4).toString()),
                  getDateTime(tokens.get(4).toString()).plusDays(1).withHour(0).withMinute(0),
                  "", "", false));
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
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

  /**
   * Method to create a recurring event based on user input.
   * @param tokens the list of command arguments.
   */

  private void recurringEventCreationHelper(List tokens) {
    try {
      if (tokens.contains("times")) {
        if (tokens.contains("to")) {
          if (checkDateTimeValidity(tokens.get(4).toString())
                  && checkDateTimeValidity(tokens.get(6).toString())
                  && getDateTime(tokens.get(4).toString())
                  .isBefore(getDateTime(tokens.get(6).toString()))
                  && checkWeekDays(tokens.get(8).toString())
                  && checkNvalue(tokens.get(10).toString())
                  && getDateTime(tokens.get(4).toString()).toLocalDate()
                  .isEqual(getDateTime(tokens.get(6).toString()).toLocalDate())) {

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
                  && getDateTime(tokens.get(4).toString())
                  .isBefore(getDateTime(tokens.get(6).toString()))
                  && checkWeekDays(tokens.get(8).toString())
                  && checkDateTimeValidity(tokens.get(10).toString())
                  && getDateTime(tokens.get(4).toString()).toLocalDate()
                  .isEqual(getDateTime(tokens.get(6).toString()).toLocalDate())
                  && getDateTime(tokens.get(6).toString())
                  .isBefore(getDateTime(tokens.get(10).toString()))) {

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
                  && checkDateValidity(tokens.get(8).toString())
                  && getDate(tokens.get(4).toString()).plusDays(1)
                  .isBefore(getDate(tokens.get(8).toString()))) {

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

  /**
   * Processes the creation of event based on user input.
   * @param command the create event command.
   */

  private void processCreate(String command) {
    List tokens = extractDataFromCommand(command);
    tokens.remove("--autoDecline");

    boolean recurring = tokens.contains("repeats");

    if (!recurring) {
      singleEventCreationHelper(tokens);
    }
    else {
      recurringEventCreationHelper(tokens);
    }
  }

  /**
   * Processes the command to modify an event.
   * @param command edit event command.
   */

  private void processEdit(String command) {
    List tokens = extractDataFromCommand(command);

    try {
      if (tokens.size() == 10) {
        String property = tokens.get(2).toString();
        String eventName = tokens.get(3).toString();
        String startDateTime = tokens.get(5).toString();
        String endDateTime = tokens.get(7).toString();
        String newValue = tokens.get(9).toString();

        if (checkDateTimeValidity(startDateTime)
                && checkDateTimeValidity(endDateTime)
                && checkValidPropertyValues(property, newValue)) {
          model.editEvents(property, eventName,
                  getDateTime(startDateTime), getDateTime(endDateTime), newValue);
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
      else if (tokens.size() == 8) {
        String property = tokens.get(2).toString();
        String eventName = tokens.get(3).toString();
        String dateTime = tokens.get(5).toString();
        String newValue = tokens.get(7).toString();

        if (checkDateTimeValidity(dateTime) && checkValidPropertyValues(property, newValue)) {
          model.editEvents(property, eventName, getDateTime(dateTime), newValue);
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
      else if (tokens.size() == 5) {
        String property = tokens.get(2).toString();
        String eventName = tokens.get(3).toString();
        String newValue = tokens.get(4).toString();

        if (checkValidPropertyValues(property, newValue)) {
          model.editEvents(property, eventName, newValue);
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
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

  /**
   * Formats and returns a list of events as string.
   * @param result list of events.
   * @return formatted list of event details.
   */

  List returnResult(List<List> result) {
    List<String> printResult = new ArrayList<>();
    for (List event : result) {
      String eventName = (String) event.get(0);
      LocalDateTime startDateTime = (LocalDateTime) event.get(1);
      LocalDateTime endDateTime = (LocalDateTime) event.get(2);
      String location = (String) event.get(3);

      String startFormatted = startDateTime.format(DATE_TIME_FORMATTER);
      String endFormatted = endDateTime.format(DATE_TIME_FORMATTER);

      String locationStr = (location != null && !location.trim()
              .isEmpty()) ? " at " + location : "";

      printResult.add("• " + eventName + " (" + startFormatted + " - "
              + endFormatted + ")"
              + locationStr);
    }
    return printResult;
  }

  /**
   * Processes command to print events on a specific date range.
   * @param command print events command.
   * @throws InvalidCommandException if command is invalid.
   */

  private void processPrint(String command) throws InvalidCommandException {
    List tokens = extractDataFromCommand(command);
    List<List> result;

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
                && getDateTime(tokens.get(3).toString())
                .isBefore(getDateTime(tokens.get(5).toString()))) {
          result = model.getEventsBetween(getDateTime(tokens.get(3).toString()),
                  getDateTime(tokens.get(5).toString()));
        }
        else {
          throw new InvalidCommandException("Invalid datetime or property");
        }
      }
      else {
        throw new InvalidCommandException("Invalid command");
      }

      if (result.size() >= 1) {
        String resultString = String.join("\n", returnResult(result));
        view.displayMessage(resultString);
      }
      // else {
      // view.displayMessage("No events found");
      // }
    }
    catch (InvalidCommandException e) {
      throw e;
    }
    catch (Exception e) {
      throw new InvalidCommandException("Invalid Command");
    }
  }

  /**
   * Processes command to check if user is busy at a specific date and time.
   * @param command the show status command.
   */

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

  /**
   * Processes the export command to save the calendar as a CSV file.
   * @param command export calendar command.
   */

  private void processExport(String command) {
    List tokens = extractDataFromCommand(command);
    List<List> result;

    try {
      if (tokens.get(2).toString().toLowerCase().endsWith(".csv")
              && tokens.get(2).toString().length() > 4) {
        result = model.exportCalendar();
        CSVExporter exporter = new CSVExporter();
        exporter.exportCSV(result, tokens.get(2).toString());
        // String filePath = exporter.exportCSV(result, tokens.get(2).toString());
        // view.displayMessage("File available at: " + filePath);
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