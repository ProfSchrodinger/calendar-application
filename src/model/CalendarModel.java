package model;

import java.time.LocalDate;
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
  public List<List> getEventsOn(LocalDate date) {
    List<List> result = new ArrayList<>();
    for (CalendarEvent event : events) {
      if (event.startDateTime.toLocalDate().equals(date)) {
        List eventDetails = new ArrayList();
        eventDetails.add(event.subject);
        eventDetails.add(event.startDateTime);
        eventDetails.add(event.endDateTime);
        eventDetails.add(event.location);
        result.add(eventDetails);
      }
    }
    return result;
  }

  @Override
  public List<List> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    List<List> result = new ArrayList<>();
    for (CalendarEvent event : events) {
      if (event.startDateTime.compareTo(start) >= 0
              && event.endDateTime.compareTo(end) <= 0) {
        List eventDetails = new ArrayList();
        eventDetails.add(event.subject);
        eventDetails.add(event.startDateTime);
        eventDetails.add(event.endDateTime);
        eventDetails.add(event.location);
        result.add(eventDetails);
      }
    }
    return result;
  }

  @Override
  public List<List> getAllEvents() {
    List<List> result = new ArrayList<>();
    for (CalendarEvent event : events) {
      List eventDetails = new ArrayList();
      eventDetails.add(event.subject);
      eventDetails.add(event.startDateTime);
      eventDetails.add(event.endDateTime);
      eventDetails.add(event.location);
      result.add(eventDetails);
    }
    return result;
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    for (CalendarEvent event : events) {
      if (event.startDateTime.compareTo(dateTime) <= 0 &&
              event.endDateTime.compareTo(dateTime) > 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String exportCalendar(String fileName) throws Exception {
    return "";
  }
}
