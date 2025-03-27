package model;

import java.time.ZoneId;

/**
 * Interface for the CalendarModelV2. Involves only timezones and calendar names.
 */

public interface ICalendarModelV2 extends ICalendarModel {

  /**
   * Function to change calendar name.
   * @param newName The new name of the calendar.
   */

  void changeCalendarName(String newName);

  /**
   * Function to change timezone of a calendar.
   * @param newTimeZone the new timezone ID.
   */

  void changeCalendarTimeZone(ZoneId newTimeZone);
}
