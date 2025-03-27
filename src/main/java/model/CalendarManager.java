package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.EventConflictException;
import exception.InvalidCommandException;

/**
 * Class to hold multiple Calendars.
 */

public class CalendarManager implements ICalendarModel, ICalendarManager {

  private Map<String, CalendarModelV2> calendars;
  private CalendarModelV2 currentCalendar;

  /**
   * Constructor.
   */

  public CalendarManager() {
    calendars = new HashMap<>();
    currentCalendar = new CalendarModelV2("Default", ZoneId.of("America/New_York"));
    calendars.put("Default", currentCalendar);
  }

  /**
   * Function to create a calendar.
   * @param calendarName The name of the calendar.
   * @param timeZone The timezone of the new calendar.
   * @throws InvalidCommandException If calendar with same name exits.
   */

  @Override
  public void createCalendar(String calendarName, ZoneId timeZone) throws InvalidCommandException {
    if (calendars.containsKey(calendarName)) {
      throw new InvalidCommandException("Calendar already exists with same name.");
    }
    CalendarModelV2 newCalendar = new CalendarModelV2(calendarName, timeZone);
    calendars.put(calendarName, newCalendar);
  }

  /**
   * Function to switch calendars.
   * @param calendarName The name of the calendar.
   * @throws InvalidCommandException If no such calendar exists.
   */

  @Override
  public void switchCalendar(String calendarName) throws InvalidCommandException {
    if (!calendars.containsKey(calendarName)) {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }
    currentCalendar = calendars.get(calendarName);
  }

  /**
   * Function to change calendar's name.
   * @param calendarName The name of the calendar.
   * @param newName The new name for the calendar.
   * @throws InvalidCommandException If a calendar with the new name already exists.
   */

  @Override
  public void changeCalendarName(String calendarName, String newName) throws InvalidCommandException {
    if (calendars.containsKey(calendarName)) {
      CalendarModelV2 calendar = calendars.get(calendarName);

      if (calendars.containsKey(newName)) {
        throw new InvalidCommandException("Calendar with the given name already exists.");
      }
      else{
        calendars.remove(calendarName);
        calendar.changeCalendarName(newName);
        calendars.put(newName, calendar);
      }
    }
    else {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }
  }

  /**
   * Function to change calendar's time zone.
   * @param calendarName The name of the calendar.
   * @param newTimeZone The new time zone of the calendar.
   */

  @Override
  public void changeCalendarTimeZone(String calendarName, ZoneId newTimeZone) {
    if (calendars.containsKey(calendarName)) {
      CalendarModelV2 calendar = calendars.get(calendarName);
      calendar.changeCalendarTimeZone(newTimeZone);
    }
    else {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }
  }

  /**
   * Function to change the new event's time according to the target calendar's timezone.
   * @param targetZoneID the target calendar's timezone ID.
   * @param modifiedEvent The event to be modified.
   * @param newStartDateTime The start time of the event.
   * @return The modified event.
   */

  private SingleEvent modifyEventHelper(ZoneId targetZoneID, SingleEvent modifiedEvent, LocalDateTime newStartDateTime) {
    LocalDateTime newEndDateTime = newStartDateTime.plusMinutes(ChronoUnit.MINUTES.between(modifiedEvent.startDateTime, modifiedEvent.endDateTime));

    ZonedDateTime sourceZdt = newStartDateTime.atZone(currentCalendar.timeZone);
    ZonedDateTime targetZdt = sourceZdt.withZoneSameInstant(targetZoneID);
    LocalDateTime modifiedStartDateTime = targetZdt.toLocalDateTime();
    modifiedEvent.startDateTime = modifiedStartDateTime;

    sourceZdt = newEndDateTime.atZone(currentCalendar.timeZone);
    targetZdt = sourceZdt.withZoneSameInstant(targetZoneID);
    LocalDateTime modifiedEndDateTime = targetZdt.toLocalDateTime();
    modifiedEvent.endDateTime = modifiedEndDateTime;

    return modifiedEvent;
  }

  /**
   * Function to copy events of a given name and start time.
   * @param eventName The name of the event to be modified.
   * @param copyDate The start date time of the event.
   * @param targetCalendar The target calendar.
   * @param targetDateTime The target datetime.
   */

  @Override
  public void copyEvents(String eventName, LocalDateTime copyDate, String targetCalendar,
                         LocalDateTime targetDateTime) {
    if (!calendars.containsKey(targetCalendar)) {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }

    CalendarModelV2 targetCalendarObject = (CalendarModelV2) calendars.get(targetCalendar);
    List<CalendarEvent> eventsToBeAdded = new ArrayList<>();

    for (CalendarEvent event: currentCalendar.events) {
      if (event instanceof SingleEvent){
        if (event.subject.equals(eventName) && event.startDateTime.equals(copyDate)){
          eventsToBeAdded.add(event);
        }
      }
      if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent: recurringEvent.recurringEventList){
          if (singleEvent.subject.equals(eventName) && singleEvent.startDateTime.equals(copyDate)){
            eventsToBeAdded.add(singleEvent);
          }
        }
      }
    }

    for (CalendarEvent event: eventsToBeAdded){
      SingleEvent modifiedEvent = new SingleEvent((SingleEvent) event);
      modifyEventHelper(targetCalendarObject.timeZone, modifiedEvent, targetDateTime);
      targetCalendarObject.events.add(modifiedEvent);
    }
  }

  /**
   * Function to copy events on a given date.
   * @param copyDate The date on which the events to be copied.
   * @param targetCalendar The target calendar.
   * @param targetDate The target datetime.
   */

  @Override
  public void copyEvents(LocalDate copyDate, String targetCalendar, LocalDate targetDate){
    if (!calendars.containsKey(targetCalendar)) {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }

    CalendarModelV2 targetCalendarObject = calendars.get(targetCalendar);
    List<CalendarEvent> eventsToBeAdded = new ArrayList<>();

    for (CalendarEvent event: currentCalendar.events) {
      if (event instanceof SingleEvent){
        if (event.startDateTime.toLocalDate().equals(copyDate)){
          eventsToBeAdded.add(event);
        }
      }
      if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent: recurringEvent.recurringEventList){
          if (singleEvent.startDateTime.toLocalDate().equals(copyDate)){
            eventsToBeAdded.add(singleEvent);
          }
        }
      }
    }

    for (CalendarEvent event: eventsToBeAdded){
      SingleEvent modifiedEvent = new SingleEvent((SingleEvent) event);
      LocalDateTime newStartDateTime = LocalDateTime.of(targetDate, modifiedEvent.startDateTime.toLocalTime());
      modifyEventHelper(targetCalendarObject.timeZone, modifiedEvent, newStartDateTime);
      targetCalendarObject.events.add(modifiedEvent);
    }
  }

  /**
   * Function to copy events between on a given date.
   * @param copyDateStart The start date of the copying period.
   * @param copyDateEnd The end date of the copying period.
   * @param targetCalendar The target calendar.
   * @param targetDate The target datetime.
   */

  @Override
  public void copyEvents(LocalDate copyDateStart, LocalDate copyDateEnd,
                         String targetCalendar, LocalDate targetDate){
    if (!calendars.containsKey(targetCalendar)) {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }

    CalendarModelV2 targetCalendarObject = calendars.get(targetCalendar);
    List<CalendarEvent> eventsToBeAdded = new ArrayList<>();

    for (CalendarEvent event: currentCalendar.events) {
      if (event instanceof SingleEvent){
        if (event.startDateTime.toLocalDate().isAfter(copyDateStart)
                && event.startDateTime.toLocalDate().isBefore(copyDateEnd)){
          eventsToBeAdded.add(event);
        }
      }
      if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent: recurringEvent.recurringEventList){
          if (singleEvent.startDateTime.toLocalDate().isAfter(copyDateStart)
                  && singleEvent.startDateTime.toLocalDate().isBefore(copyDateEnd)){
            eventsToBeAdded.add(singleEvent);
          }
        }
      }
    }

    for (CalendarEvent event: eventsToBeAdded){
      SingleEvent modifiedEvent = new SingleEvent((SingleEvent) event);
      LocalDateTime newStartDateTime = LocalDateTime.of(targetDate, modifiedEvent.startDateTime.toLocalTime())
              .plusDays(ChronoUnit.DAYS.between(copyDateStart, event.startDateTime.toLocalDate()));
      modifyEventHelper(targetCalendarObject.timeZone, modifiedEvent, newStartDateTime);
      targetCalendarObject.events.add(modifiedEvent);
    }
  }

  /**
   * Function to create single event. Control sent to CalendarModel.
   * @param event The single event to be created.
   * @throws EventConflictException send back from CalendarModel.
   */

  @Override
  public void createSingleEvent(CalendarEvent event) throws EventConflictException {
    currentCalendar.createSingleEvent(event);
  }

  /**
   * Function to create recurring event. Control sent to CalendarModel.
   * @param event The recurring event to be created.
   * @throws EventConflictException sent back from CalendarModel.
   */

  @Override
  public void createRecurringEvent(CalendarEvent event) throws EventConflictException {
    currentCalendar.createRecurringEvent(event);
  }

  /**
   * Function to edit events. Control sent to CalendarModel.
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param startDateTime The start date and time of the event.
   * @param endDateTime The end date and time of the event.
   * @param newValue The new value to set.
   * @throws Exception sent back from CalendarModel.
   */

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime,
                         LocalDateTime endDateTime, String newValue) throws Exception {
    currentCalendar.editEvents(property, eventName, startDateTime, endDateTime, newValue);
  }

  /**
   * Function to edit events. Control sent to CalendarModel.
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param startDateTime The start date and time of the event.
   * @param newValue The new value to be set.
   * @throws Exception sent back from CalendarModel.
   */

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime,
                         String newValue) throws Exception {
    currentCalendar.editEvents(property, eventName, startDateTime, newValue);
  }

  /**
   * Function to edit events. Control sent to CalendarModel.
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param newValue The new value to be set.
   * @throws Exception sent back from CalendarModel.
   */

  @Override
  public void editEvents(String property, String eventName, String newValue) throws Exception {
    currentCalendar.editEvents(property, eventName, newValue);
  }

  /**
   * Function to get events on a particular date. Control sent to CalendarModel.
   * @param date The date to check for events.
   * @return The list of events.
   */

  @Override
  public List<List> getEventsOn(LocalDate date) {
    return currentCalendar.getEventsOn(date);
  }

  /**
   * Function to get events between 2 dates. Control sent to CalendarModel.
   * @param start The start of the time range.
   * @param end The end of the time range.
   * @return The list of events.
   */

  @Override
  public List<List> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    return currentCalendar.getEventsBetween(start, end);
  }

  /**
   * Function to check if the calendar is busy at a datetime. Control sent to CalendarModel.
   * @param dateTime The date and time to check.
   * @return True if busy, else False.
   */

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    return currentCalendar.isBusy(dateTime);
  }

  /**
   * Function to export calendar.
   * @return The list of events to be exported.
   * @throws Exception sent back from CalendarModel.
   */
  @Override
  public List<List> exportCalendar() throws Exception {
    return currentCalendar.exportCalendar();
  }
}
