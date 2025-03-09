package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import exception.EventConflictException;

public interface ICalendarModel {
  CalendarEvent createSingleEvent(CalendarEvent event, boolean autoDecline) throws EventConflictException;
  CalendarEvent createRecurringEvent(CalendarEvent event) throws EventConflictException;

  void editEvents(String property, String originalValue, LocalDateTime startDateTime,
                 LocalDateTime endDateTime, String newValue) throws Exception;
  void editEvents(String property, String originalValue, LocalDateTime startDateTime, String newValue) throws Exception;
  void editEvents(String property, String originalValue, String newValue) throws Exception;

  List<List> getEventsOn(LocalDate date);
  List<List> getEventsBetween(LocalDateTime start, LocalDateTime end);
  List<List> getEventsAll();

  boolean isBusy(LocalDateTime dateTime);

  String exportCalendar(String fileName) throws Exception;
}
