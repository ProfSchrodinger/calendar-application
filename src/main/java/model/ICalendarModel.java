package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import exception.EventConflictException;

/**
 * Interface for calendar model, with methods for managing events.
 */

public interface ICalendarModel {

  /**
   * Creates a single event and adds it to the calendar.
   * If autoDecline is enabled,
   * an EventConflictException is thrown in case of a conflict.
   *
   * @param event The single event to be created.
   * @throws EventConflictException If the event conflicts with an existing event.
   */

  void createSingleEvent(CalendarEvent event) throws EventConflictException;

  /**
   * Creates a recurring event and adds it to the calendar.
   * @param event The recurring event to be created.
   * @throws EventConflictException If any occurrence conflicts with an existing event.
   */

  void createRecurringEvent(CalendarEvent event) throws EventConflictException;

  /**
   * Edits an event by modifying a specific property.
   *
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param startDateTime The start date and time of the event.
   * @param endDateTime The end date and time of the event.
   * @param newValue The new value to set.
   * @throws Exception If an error occurs during event modification.
   */

  void editEvents(String property, String eventName, LocalDateTime startDateTime,
                 LocalDateTime endDateTime, String newValue) throws Exception;

  /**
   * Edits an event by modifying a specific property for an event.
   *
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param startDateTime The start date and time of the event.
   * @param newValue The new value to be set.
   * @throws Exception If an error occurs during event modification.
   */

  void editEvents(String property, String eventName, LocalDateTime startDateTime, String newValue) throws Exception;

  /**
   * Edits all events with the specified name by modifying a specific property.
   *
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param newValue The new value to be set.
   * @throws Exception If an error occurs during event modification.
   */

  void editEvents(String property, String eventName, String newValue) throws Exception;

  /**
   * Retrieves all events occurring on a given date.
   *
   * @param date The date to check for events.
   * @return A list of events scheduled on that date.
   */

  List<List> getEventsOn(LocalDate date);

  /**
   * Retrieves all events within a specified time range.
   *
   * @param start The start of the time range.
   * @param end The end of the time range.
   * @return A list of events occurring within the specified range.
   */

  List<List> getEventsBetween(LocalDateTime start, LocalDateTime end);

  /**
   * Checks whether the user is busy at a given date and time.
   *
   * @param dateTime The date and time to check.
   * @return True if there is an event at the given time, otherwise false.
   */

  boolean isBusy(LocalDateTime dateTime);

  /**
   * Exports the calendar events to a CSV file.
   * @throws Exception If an error occurs during file export.
   */

  List<List> exportCalendar() throws Exception;
}
