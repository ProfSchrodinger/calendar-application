package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import exception.EventConflictException;
import exception.InvalidCommandException;

public class CalendarModel implements ICalendarModel{
  private List<CalendarEvent> events;
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

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

  private void editHelper (String property, String originalValue, String newValue, CalendarEvent event) throws EventConflictException {
    if (property.equals("subject")) {
      if (event.subject.equals(originalValue)) {
        event.subject = newValue;
      }
    }
    else if (property.equals("description")) {
      if (event.description.equals(originalValue)) {
        event.description = newValue;
      }
    }
    else if (property.equals("location")) {
      if (event.location.equals(originalValue)) {
        event.location = newValue;
      }
    }
    else if (property.equals("startDateTime")) {
      if (event.startDateTime.equals(LocalDateTime.parse(originalValue, formatter))
              && LocalDateTime.parse(newValue, formatter).isBefore(event.endDateTime)) {
        event.startDateTime = LocalDateTime.parse(newValue, formatter);
      }
    }
    else if (property.equals("endDateTime")) {
      if (event.endDateTime.equals(LocalDateTime.parse(originalValue, formatter))
              && LocalDateTime.parse(newValue, formatter).isAfter(event.startDateTime)) {
        event.endDateTime = LocalDateTime.parse(newValue, formatter);
      }
    }
    else if (property.equals("isPublic")) {
      if (event.isPublic == Boolean.parseBoolean(originalValue)) {
        event.isPublic = Boolean.parseBoolean(newValue);
      }
    }
    else {
      throw new InvalidCommandException("Invalid property: " + property);
    }
  }

  @Override
  public void editEvents(String property, String originalValue, LocalDateTime startDateTime, LocalDateTime endDateTime, String newValue) throws Exception {
    for (CalendarEvent event : events) {
      if (event.startDateTime.isEqual(startDateTime) && event.endDateTime.isEqual(endDateTime)) {
        editHelper(property, originalValue, newValue, event);
      }
    }
  }

  @Override
  public void editEvents(String property, String originalValue, LocalDateTime startDateTime, String newValue) throws Exception {
    for (CalendarEvent event : events) {
      if (event.startDateTime.isEqual(startDateTime)) {
        editHelper(property, originalValue, newValue, event);
      }
    }
  }

  @Override
  public void editEvents(String property, String originalValue, String newValue) throws Exception {
    for (CalendarEvent event : events) {
      editHelper(property, originalValue, newValue, event);
    }
  }

  @Override
  public List<List> getEventsOn(LocalDate date) {
    List<List> result = new ArrayList<>();
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        if (event.startDateTime.toLocalDate().equals(date)) {
          List eventDetails = new ArrayList();
          eventDetails.add(event.subject);
          eventDetails.add(event.startDateTime);
          eventDetails.add(event.endDateTime);
          eventDetails.add(event.location);
          result.add(eventDetails);
        }
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (event.startDateTime.toLocalDate().equals(date)) {
            List eventDetails = new ArrayList();
            eventDetails.add(singleEvent.subject);
            eventDetails.add(singleEvent.startDateTime);
            eventDetails.add(singleEvent.endDateTime);
            eventDetails.add(singleEvent.location);
            result.add(eventDetails);
          }
        }
      }
    }
    return result;
  }

  @Override
  public List<List> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    List<List> result = new ArrayList<>();
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
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
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (singleEvent.startDateTime.compareTo(start) >= 0
                  && singleEvent.endDateTime.compareTo(end) <= 0) {
            List eventDetails = new ArrayList();
            eventDetails.add(singleEvent.subject);
            eventDetails.add(singleEvent.startDateTime);
            eventDetails.add(singleEvent.endDateTime);
            eventDetails.add(singleEvent.location);
            result.add(eventDetails);
          }
        }
      }
    }
    return result;
  }

  @Override
  public List<List> getEventsAll() {
    List<List> result = new ArrayList<>();
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        List eventDetails = new ArrayList();
        eventDetails.add(event.subject);
        eventDetails.add(event.startDateTime);
        eventDetails.add(event.endDateTime);
        eventDetails.add(event.location);
        result.add(eventDetails);
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          List eventDetails = new ArrayList();
          eventDetails.add(singleEvent.subject);
          eventDetails.add(singleEvent.startDateTime);
          eventDetails.add(singleEvent.endDateTime);
          eventDetails.add(singleEvent.location);
          result.add(eventDetails);
        }
      }
    }
    return result;
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        if (event.startDateTime.compareTo(dateTime) <= 0 &&
                event.endDateTime.compareTo(dateTime) > 0) {
          return true;
        }
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (singleEvent.startDateTime.compareTo(dateTime) <= 0 &&
                  singleEvent.endDateTime.compareTo(dateTime) > 0) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public String exportCalendar(String fileName) throws Exception {
    return "";
  }
}
