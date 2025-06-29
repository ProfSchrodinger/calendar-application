package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import exception.EventConflictException;
import exception.InvalidCommandException;

/**
 * Calendar model that manages events.
 * It implements the ICalendarModel interface.
 */

public class CalendarModel implements ICalendarModel {

  List<CalendarEvent> events;
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Constructs an empty calendar model.
   */

  public CalendarModel() {
    this.events = new ArrayList<CalendarEvent>();
  }

  /**
   * Creates a single event and adds it to the calendar.
   * in case of conflict.
   * @param event the single event to be created.
   * @throws EventConflictException if event conflicts with existing event.
   */

  @Override
  public void createSingleEvent(CalendarEvent event) throws EventConflictException {
    for (CalendarEvent existing : events) {
      if (existing instanceof SingleEvent) {
        if (event.conflictsWith(existing)) {
          throw new EventConflictException("Event Conflict Occurred");
        }
      }
      else if (existing instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) existing;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (event.conflictsWith(singleEvent)) {
            throw new EventConflictException("Event Conflict Occurred");
          }
        }
      }
    }
    events.add(event);
  }

  /**
   * Creates recurring event and adds it to the calendar.
   * @param event the recurring event to be created.
   * @throws EventConflictException if event conflicts with existing event.
   */

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

  /**
   * Edits an event by modifying its properties.
   * @param property The property to be changed.
   * @param newValue The new value for the property.
   * @param event The event to be modified.
   * @param eventType The type of event.
   * @throws EventConflictException if conflict occurs due to editing.
   */

  private void editHelper(String property, String newValue, CalendarEvent event,
                          String eventType) throws EventConflictException {
    switch (property) {
      case "subject":
        event.subject = newValue;
        break;
      case "description":
        event.description = newValue;
        break;
      case "location":
        event.location = newValue;
        break;
      case "startDateTime":
        if (LocalDateTime.parse(newValue, formatter).isBefore(event.endDateTime)) {
          LocalDateTime originalStartDateTime = event.startDateTime;

          if (eventType.equals("Recurring")
                  && LocalDateTime.parse(newValue, formatter)
                  .toLocalDate().equals(event.startDateTime.toLocalDate())) {
            event.startDateTime = LocalDateTime.parse(newValue, formatter);

            if (checkConflict(event)) {
              event.startDateTime = originalStartDateTime;
            }
          } else if (eventType.equals("Single")) {
            event.startDateTime = LocalDateTime.parse(newValue, formatter);

            if (checkConflict(event)) {
              event.startDateTime = originalStartDateTime;
            }
          }
        }
        break;
      case "endDateTime":
        if (LocalDateTime.parse(newValue, formatter).isAfter(event.startDateTime)) {
          LocalDateTime originalEndDateTime = event.endDateTime;

          if (eventType.equals("Recurring")
                  && LocalDateTime.parse(newValue, formatter)
                  .toLocalDate().equals(event.endDateTime.toLocalDate())) {
            event.endDateTime = LocalDateTime.parse(newValue, formatter);

            if (checkConflict(event)) {
              event.endDateTime = originalEndDateTime;
            }
          } else if (eventType.equals("Single")) {
            event.endDateTime = LocalDateTime.parse(newValue, formatter);

            if (checkConflict(event)) {
              event.endDateTime = originalEndDateTime;
            }
          }
        }
        break;
      case "isPublic":
        event.isPublic = Boolean.parseBoolean(newValue);
        break;
      default:
        throw new InvalidCommandException("Invalid property: " + property);
    }
  }

  /**
   * Helper function to check if the edited event conflicts.
   * @param changedEvent the edited event.
   * @return Boolean true if it conflicts, false in other case.
   */

  private boolean checkConflict(CalendarEvent changedEvent) {
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        if (changedEvent.conflictsWith(event) && event != changedEvent) {
          return true;
        }
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringExistingEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringExistingEvent.recurringEventList) {
          if (changedEvent.conflictsWith(singleEvent) && singleEvent != changedEvent) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Edits an event by modifying a specific property for an event.
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param startDateTime The start date and time of the event.
   * @param endDateTime The end date and time of the event.
   * @param newValue The new value to be set.
   * @throws Exception If an error occurs.
   */

  @Override
  public void editEvents(String property, String eventName, LocalDateTime startDateTime,
                         LocalDateTime endDateTime, String newValue) throws Exception {
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
          if (singleEvent.startDateTime.isEqual(startDateTime)
                  && singleEvent.endDateTime.isEqual(endDateTime)) {
            if (singleEvent.subject.equals(eventName)) {
              editHelper(property, newValue, singleEvent, "Recurring");
            }
          }
        }
      }
    }
  }

  /**
   * Edits an event by modifying a specific property for an event.
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param startDateTime The start date and time of the event.
   * @param newValue The new value to be set.
   * @throws Exception If an error occurs.
   */

  @Override
  public void editEvents(String property, String eventName,
                         LocalDateTime startDateTime, String newValue) throws Exception {
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        if (event.startDateTime.compareTo(startDateTime) >= 0) {
          if (event.subject.equals(eventName)) {
            editHelper(property, newValue, event, "Single");
          }
        }
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (singleEvent.startDateTime.compareTo(startDateTime) >= 0) {
            if (singleEvent.subject.equals(eventName)) {
              editHelper(property, newValue, singleEvent, "Recurring");
            }
          }
        }
      }
    }
  }

  /**
   * Edits an event by modifying a specific property for an event.
   * @param property The property of the event to modify.
   * @param eventName The name of the event to be edited.
   * @param newValue The new value to be set.
   * @throws Exception If an error occurs.
   */

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

  /**
   * Retrieves all events occurring on a given date.
   * @param date date to check for events.
   * @return list of events on the given date.
   */

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

  /**
   * Retrieves all events occurring within a specified time range.
   * @param start The start of the time range.
   * @param end The end of the time range.
   * @return List of events occurring on the specified time range.
   */

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

  /**
   * Checks whether the user is busy at a given date and time.
   * @param dateTime date and time to check.
   * @return true if there is an event at the time, else false.
   */

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        if (event.startDateTime.compareTo(dateTime) <= 0
                && event.endDateTime.compareTo(dateTime) > 0) {
          return true;
        }
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          if (singleEvent.startDateTime.compareTo(dateTime) <= 0
                  && singleEvent.endDateTime.compareTo(dateTime) > 0) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Exports the calendar to a CSV file.
   * @throws Exception If an error occurs during file export.
   */

  @Override
  public List<List> exportCalendar() throws Exception {
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
    return exportEvents;
  }
}