package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import exception.EventConflictException;

public interface ICalendarModel {
  void createSingleEvent(CalendarEvent event, boolean autoDecline) throws EventConflictException;
  void createRecurringEvent(CalendarEvent event) throws EventConflictException;

  void editEvents(String property, String eventName, LocalDateTime startDateTime,
                 LocalDateTime endDateTime, String newValue) throws Exception;
  void editEvents(String property, String eventName, LocalDateTime startDateTime, String newValue) throws Exception;
  void editEvents(String property, String eventName, String newValue) throws Exception;

  List<List> getEventsOn(LocalDate date);
  List<List> getEventsBetween(LocalDateTime start, LocalDateTime end);

  boolean isBusy(LocalDateTime dateTime);

  void exportCalendar(String fileName) throws Exception;
}
