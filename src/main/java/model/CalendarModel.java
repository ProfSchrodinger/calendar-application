package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import utilities.CSVExporter;
import exception.EventConflictException;
import exception.InvalidCommandException;

public class CalendarModel implements ICalendarModel{
  List<CalendarEvent> events;
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  public CalendarModel() {
    this.events = new ArrayList<CalendarEvent>();
  }

  @Override
  public void createSingleEvent(CalendarEvent event, boolean autoDecline) throws EventConflictException {
    for (CalendarEvent existing : events) {
      if (existing instanceof SingleEvent) {
        if (event.conflictsWith(existing) && autoDecline) {
          throw new EventConflictException("Event Conflict Occurred");
        }
      }
      else if (existing instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) existing;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (event.conflictsWith(singleEvent) && autoDecline) {
            throw new EventConflictException("Event Conflict Occurred");
          }
        }
      }
    }
    events.add(event);
  }

  @Override
  public void createRecurringEvent(CalendarEvent event) throws EventConflictException {
    RecurringEvent recurringToBeCreatedEvent = (RecurringEvent) event;

    for (CalendarEvent existing : events) {
      if (existing instanceof SingleEvent) {
        for (SingleEvent singleEvent : recurringToBeCreatedEvent.recurringEventList) {
          if (singleEvent.conflictsWith(existing)) {
            throw new EventConflictException("Event Conflict Occurred");
          }
        }
      }
      else if (existing instanceof RecurringEvent) {
        RecurringEvent recurringExistingEvent = (RecurringEvent) existing;
        for (SingleEvent singleExistingEvent : recurringExistingEvent.recurringEventList) {
          for (SingleEvent singleToBeCreatedEvent : recurringToBeCreatedEvent.recurringEventList) {
            if (singleToBeCreatedEvent.conflictsWith(singleExistingEvent)) {
              throw new EventConflictException("Event Conflict Occurred");
            }
          }
        }
      }
    }
    events.add(event);
  }

  private void editHelper (String property, String newValue, CalendarEvent event, String eventType) throws EventConflictException {
    if (property.equals("subject")) {
      event.subject = newValue;
    }
    else if (property.equals("description")) {
      event.description = newValue;
    }
    else if (property.equals("location")) {
      event.location = newValue;
    }
    else if (property.equals("startDateTime")) {
      if (LocalDateTime.parse(newValue, formatter).isBefore(event.endDateTime)) {
        if (eventType.equals("Recurring")
                && LocalDateTime.parse(newValue, formatter).toLocalDate().equals(event.startDateTime.toLocalDate())) {
          event.startDateTime = LocalDateTime.parse(newValue, formatter);
        }
        else if (eventType.equals("Single")){
          event.startDateTime = LocalDateTime.parse(newValue, formatter);
        }
      }
    }
    else if (property.equals("endDateTime")) {
      if (LocalDateTime.parse(newValue, formatter).isAfter(event.startDateTime)) {
        if (eventType.equals("Recurring") && LocalDateTime.parse(newValue, formatter).toLocalDate().equals(event.endDateTime.toLocalDate())) {
          event.endDateTime = LocalDateTime.parse(newValue, formatter);
        }
        else if (eventType.equals("Single")){
          event.endDateTime = LocalDateTime.parse(newValue, formatter);
        }
      }
    }
    else if (property.equals("isPublic")) {
      event.isPublic = Boolean.parseBoolean(newValue);
    }
    else {
      throw new InvalidCommandException("Invalid property: " + property);
    }
  }

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime, String newValue) throws Exception {
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        if (event.startDateTime.isEqual(startDateTime) && event.endDateTime.isEqual(endDateTime)) {
          if (event.subject.equals(eventName)) {
            editHelper(property, newValue, event, "Single");
          }
        }
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (singleEvent.startDateTime.isEqual(startDateTime) && singleEvent.endDateTime.isEqual(endDateTime)) {
            if (singleEvent.subject.equals(eventName)) {
              editHelper(property, newValue, singleEvent, "Recurring");
            }
          }
        }
      }
    }
  }

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime, String newValue) throws Exception {
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        if (event.startDateTime.isEqual(startDateTime)) {
          if (event.subject.equals(eventName)) {
            editHelper(property, newValue, event, "Single");
          }
        }
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (singleEvent.startDateTime.isEqual(startDateTime)) {
            if (singleEvent.subject.equals(eventName)) {
              editHelper(property, newValue, singleEvent, "Recurring");
            }
          }
        }
      }
    }
  }

  @Override
  public void editEvents(String property, String eventName, String newValue) throws Exception {
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        if (event.subject.equals(eventName)) {
          editHelper(property, newValue, event, "Single");
        }
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (singleEvent.subject.equals(eventName)) {
            editHelper(property, newValue, singleEvent, "Recurring");
          }
        }
      }
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
          if (singleEvent.startDateTime.toLocalDate().equals(date)) {
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
  public void exportCalendar(String fileName) throws Exception {
    List<List> exportEvents = new ArrayList<>();

    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        List eventDetails = new ArrayList();

        eventDetails.add(event.subject);
        eventDetails.add(event.startDateTime.toLocalDate());
        eventDetails.add(event.startDateTime.toLocalTime());
        eventDetails.add(event.endDateTime.toLocalDate());
        eventDetails.add(event.endDateTime.toLocalTime());
        eventDetails.add(event.description);
        eventDetails.add(event.location);
        eventDetails.add(!event.isPublic);
        exportEvents.add(eventDetails);
      }
      else {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          List eventDetails = new ArrayList();
          eventDetails.add(singleEvent.subject);
          eventDetails.add(singleEvent.startDateTime.toLocalDate());
          eventDetails.add(singleEvent.startDateTime.toLocalTime());
          eventDetails.add(singleEvent.endDateTime.toLocalDate());
          eventDetails.add(singleEvent.endDateTime.toLocalTime());
          eventDetails.add(singleEvent.description);
          eventDetails.add(singleEvent.location);
          eventDetails.add(!singleEvent.isPublic);
          exportEvents.add(eventDetails);
        }
      }
    }

    CSVExporter exporter = new CSVExporter();
    exporter.exportCSV(exportEvents, fileName);

//    return filePath;
  }
}