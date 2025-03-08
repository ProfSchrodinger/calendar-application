package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import exception.EventConflictException;

public class CalendarModel implements ICalendarModel{
  private List<CalendarEvent> events;

  public CalendarModel() {
    this.events = new ArrayList<CalendarEvent>();
  }

  @Override
  public CalendarEvent createSingleEvent(CalendarEvent event, boolean autoDecline) throws EventConflictException {
    for (CalendarEvent existing : events) {
      if (event.conflictsWith(existing) && autoDecline) {
        throw new EventConflictException("Event Conflict Occurred");
      }
    }
    events.add(event);
    return event;
  }

  @Override
  public CalendarEvent createRecurringEvent(CalendarEvent event) throws EventConflictException {
    for (CalendarEvent existing : events) {
      if (event.conflictsWith(existing)) {
        throw new EventConflictException("Event Conflict Occurred");
      }
    }
    events.add(event);
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
