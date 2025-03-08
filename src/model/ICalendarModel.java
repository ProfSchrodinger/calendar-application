package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import exception.EventConflictException;

public interface ICalendarModel {
  CalendarEvent createSingleEvent(CalendarEvent event, boolean autoDecline) throws EventConflictException;
  CalendarEvent createRecurringEvent(CalendarEvent event) throws EventConflictException;

  void editEvent(String property, String eventName, LocalDateTime startDateTime,
                 LocalDateTime endDateTime, String newValue) throws Exception;
  void editEvents(String property, String eventName, LocalDateTime startDateTime, String newValue) throws Exception;
  void editEvents(String property, String eventName, String newValue) throws Exception;

  List<List> getEventsOn(LocalDate date);
  List<List> getEventsBetween(LocalDateTime start, LocalDateTime end);
  List<List> getAllEvents();

  boolean isBusy(LocalDateTime dateTime);

  String exportCalendar(String fileName) throws Exception;
}
