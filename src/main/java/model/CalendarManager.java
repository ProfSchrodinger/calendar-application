package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.EventConflictException;
import exception.InvalidCommandException;

public class CalendarManager implements ICalendarModel{

  private Map<String, CalendarModel> calendars;
  private CalendarModel currentCalendar;

  public CalendarManager() {
    calendars = new HashMap<>();
    currentCalendar = new CalendarModel();
  }

  public void createCalendar(String calendarName, String timeZone) throws InvalidCommandException {
    if (calendars.containsKey(calendarName)) {
      throw new InvalidCommandException("Calendar already exists with same name.");
    }
    CalendarModel newCalendar = new CalendarModel();
    // Optionally, set the timezone on newCalendar here
    newCalendar.changeCalendarTimeZone(timeZone);
    calendars.put(calendarName, newCalendar);
  }


  @Override
  public void createSingleEvent(CalendarEvent event) throws EventConflictException {

  }

  @Override
  public void createRecurringEvent(CalendarEvent event) throws EventConflictException {

  }

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime, String newValue) throws Exception {

  }

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime, String newValue) throws Exception {

  }

  @Override
  public void editEvents(String property, String eventName, String newValue) throws Exception {

  }

  @Override
  public List<List> getEventsOn(LocalDate date) {
    return List.of();
  }

  @Override
  public List<List> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    return List.of();
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    return false;
  }

  @Override
  public List<List> exportCalendar() throws Exception {
    return List.of();
  }
}
