package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import exception.InvalidCommandException;

/**
 * Interface for the CalendarManager.
 */

public interface ICalendarManager {

  /**
   * Function to create calendar.
   * @param calendarName The name for the calendar.
   * @param timeZone The timezone for the calendar.
   * @throws InvalidCommandException If calendar with same name exits.
   */

  void createCalendar(String calendarName, ZoneId timeZone) throws InvalidCommandException;

  /**
   * Function to switch calendars.
   * @param calendarName The name of the calendar.
   * @throws InvalidCommandException If no such calendar exists.
   */

  void switchCalendar(String calendarName) throws InvalidCommandException;

  /**
   * Function to change calendar's name.
   * @param calendarName The name of the calendar.
   * @param newName The new name for the calendar.
   * @throws InvalidCommandException If a calendar with the new name already exists.
   */

  void changeCalendarName(String calendarName, String newName) throws InvalidCommandException;

  /**
   * Function to change calendar's time zone.
   * @param calendarName The name of the calendar.
   * @param newTimeZone The new time zone of the calendar.
   */

  void changeCalendarTimeZone(String calendarName, ZoneId newTimeZone);

  /**
   * Function to copy events of a given name and start time.
   * @param eventName The name of the event to be modified.
   * @param copyDate The start date time of the event.
   * @param targetCalendar The target calendar.
   * @param targetDateTime The target datetime.
   */

  void copyEvents(String eventName, LocalDateTime copyDate, String targetCalendar,
                  LocalDateTime targetDateTime);

  /**
   * Function to copy events on a given date.
   * @param copyDate The date on which the events to be copied.
   * @param targetCalendar The target calendar.
   * @param targetDate The target datetime.
   */

  void copyEvents(LocalDate copyDate, String targetCalendar, LocalDate targetDate);

  /**
   * Function to copy events between on a given date.
   * @param copyDateStart The start date of the copying period.
   * @param copyDateEnd The end date of the copying period.
   * @param targetCalendar The target calendar.
   * @param targetDate The target datetime.
   */

  void copyEvents(LocalDate copyDateStart, LocalDate copyDateEnd,
                  String targetCalendar, LocalDate targetDate);

}
