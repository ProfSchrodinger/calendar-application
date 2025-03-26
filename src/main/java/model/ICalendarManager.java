package model;

import java.time.ZoneId;

import exception.InvalidCommandException;

public interface ICalendarManager {
  void createCalendar(String calendarName, ZoneId timeZone) throws InvalidCommandException;
  void switchCalendar(String calendarName) throws InvalidCommandException;
  void changeCalendarName(String oldName, String newName) throws InvalidCommandException;
  void changeCalendarTimeZone(String calendarName, ZoneId newTimeZone);
}
