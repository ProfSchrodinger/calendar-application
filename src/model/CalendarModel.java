package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import exception.EventConflictException;

public class CalendarModel implements ICalendarModel{
  private List<CalendarEvent> calendar;

  public CalendarModel() {
    this.calendar = new ArrayList<CalendarEvent>();
  }

  @Override
  public CalendarEvent createSingleEvent(CalendarEvent event, boolean autoDecline) throws EventConflictException {
    for (CalendarEvent existing : calendar) {
      if (existing.conflictsWith(event) && autoDecline) {
        throw new EventConflictException("Event Conflict Occured");
      }
    }
    calendar.add(event);
    return event;
  }

  @Override
  public CalendarEvent createRecurringEvent(CalendarEvent event) throws EventConflictException {
    for (CalendarEvent existing : calendar) {
      if (existing.conflictsWith(event)) {
        throw new EventConflictException("Event Conflict Occured");
      }
    }
    calendar.add(event);
    return event;
  }

  @Override
  public void editEvent(String property, String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime, String newValue) throws Exception {

  }

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime, String newValue) throws Exception {

  }

  @Override
  public void editEvents(String property, String eventName, String newValue) throws Exception {

  }

  @Override
  public List<CalendarEvent> getEventsOn(LocalDateTime date) {
    return List.of();
  }

  @Override
  public List<CalendarEvent> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    return List.of();
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    return false;
  }

  @Override
  public String exportCalendar(String fileName) throws Exception {
    return "";
  }
}
