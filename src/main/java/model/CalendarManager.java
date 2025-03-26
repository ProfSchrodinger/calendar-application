package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.EventConflictException;
import exception.InvalidCommandException;

public class CalendarManager implements ICalendarModel, ICalendarManager{

  private Map<String, CalendarModel> calendars;
  private CalendarModel currentCalendar;

  public CalendarManager() {
    calendars = new HashMap<>();
    currentCalendar = new CalendarModel("Default", ZoneId.of("America/New_York"));
    calendars.put("Default", currentCalendar);
  }

  @Override
  public void createCalendar(String calendarName, ZoneId timeZone) throws InvalidCommandException {
    if (calendars.containsKey(calendarName)) {
      throw new InvalidCommandException("Calendar already exists with same name.");
    }
    CalendarModel newCalendar = new CalendarModel(calendarName, timeZone);
    calendars.put(calendarName, newCalendar);
  }

  @Override
  public void switchCalendar(String calendarName) throws InvalidCommandException {
    if (!calendars.containsKey(calendarName)) {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }
    currentCalendar = calendars.get(calendarName);
  }

  @Override
  public void changeCalendarName(String calendarName, String newName) throws InvalidCommandException {
    if (calendars.containsKey(calendarName)) {
      CalendarModel calendar = calendars.get(calendarName);

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

  @Override
  public void changeCalendarTimeZone(String calendarName, ZoneId newTimeZone) {
    if (calendars.containsKey(calendarName)) {
      CalendarModel calendar = calendars.get(calendarName);
      calendar.changeCalendarTimeZone(newTimeZone);
    }
    else {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }
  }

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

  @Override
  public void copyEvents(String eventName, LocalDateTime copyDate, String targetCalendar,
                         LocalDateTime targetDateTime) {
    if (!calendars.containsKey(targetCalendar)) {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }

    CalendarModel targetCalendarObject = calendars.get(targetCalendar);
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

  @Override
  public void copyEvents(LocalDate copyDate, String targetCalendar, LocalDate targetDate){
    if (!calendars.containsKey(targetCalendar)) {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }

    CalendarModel targetCalendarObject = calendars.get(targetCalendar);
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

  @Override
  public void copyEvents(LocalDate copyDateStart, LocalDate copyDateEnd,
                         String targetCalendar, LocalDate targetDate){
    if (!calendars.containsKey(targetCalendar)) {
      throw new InvalidCommandException("Calendar with the given name does not exist.");
    }

    CalendarModel targetCalendarObject = calendars.get(targetCalendar);
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

  @Override
  public void createSingleEvent(CalendarEvent event) throws EventConflictException {
    currentCalendar.createSingleEvent(event);
  }

  @Override
  public void createRecurringEvent(CalendarEvent event) throws EventConflictException {
    currentCalendar.createRecurringEvent(event);
  }

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime,
                         LocalDateTime endDateTime, String newValue) throws Exception {
    currentCalendar.editEvents(property, eventName, startDateTime, endDateTime, newValue);
  }

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime,
                         String newValue) throws Exception {
    currentCalendar.editEvents(property, eventName, startDateTime, newValue);
  }

  @Override
  public void editEvents(String property, String eventName, String newValue) throws Exception {
    currentCalendar.editEvents(property, eventName, LocalDateTime.now(), newValue);
  }

  @Override
  public List<List> getEventsOn(LocalDate date) {
    return currentCalendar.getEventsOn(date);
  }

  @Override
  public List<List> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    return currentCalendar.getEventsBetween(start, end);
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    return currentCalendar.isBusy(dateTime);
  }

  @Override
  public List<List> exportCalendar() throws Exception {
    return currentCalendar.exportCalendar();
  }

  public void printCalendars(){
    System.out.println(calendars);
    for (CalendarModel calendar : calendars.values()) {
      System.out.println(calendar.calendarName);
      System.out.println(calendar.timeZone);
      System.out.println(calendar.events);
    }
  }
}
