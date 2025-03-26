package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import exception.InvalidCommandException;

public interface ICalendarManager {
  void createCalendar(String calendarName, ZoneId timeZone) throws InvalidCommandException;
  void switchCalendar(String calendarName) throws InvalidCommandException;
  void changeCalendarName(String oldName, String newName) throws InvalidCommandException;
  void changeCalendarTimeZone(String calendarName, ZoneId newTimeZone);
  void copyEvents(String eventName, LocalDateTime copyDate, String targetCalendar,
                  LocalDateTime targetDate);
  void copyEvents(LocalDate copyDate, String targetCalendar, LocalDate targetDate);
  void copyEvents(LocalDate copyDateStart, LocalDate copyDateEnd,
                  String targetCalendar, LocalDate targetDate);

}
