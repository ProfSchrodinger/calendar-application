package model;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class to include calendarName and timezone on top of the CalendarModel.
 */

public class CalendarModelV2 extends CalendarModel implements ICalendarModelV2 {

  String calendarName;
  ZoneId timeZone;
  Color calendarColor;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Constructs an empty calendar model.
   * @param calendarName The name of the calendar.
   * @param timeZone The timezone designated to the calendar.
   */

  public CalendarModelV2(String calendarName, ZoneId timeZone, Color calendarColor) {
    super();
    this.calendarName = calendarName;
    this.timeZone = timeZone;
    this.calendarColor = calendarColor;

  }

  /**
   * Function to change calendar name.
   * @param newName The new name for the calendar.
   */

  @Override
  public void changeCalendarName(String newName) {
    this.calendarName = newName;
  }

  /**
   * Function to convert start and end times to the new timezone.
   * @param event The event to be changed.
   * @param newTimeZone The new timezone ID.
   */

  private void changeEventTime(CalendarEvent event, ZoneId newTimeZone) {
    ZonedDateTime oldStartZdt = event.startDateTime.atZone(this.timeZone);
    ZonedDateTime newStartZdt = oldStartZdt.withZoneSameInstant(newTimeZone);
    event.startDateTime = newStartZdt.toLocalDateTime();

    ZonedDateTime oldEndZdt = event.endDateTime.atZone(this.timeZone);
    ZonedDateTime newEndZdt = oldEndZdt.withZoneSameInstant(newTimeZone);
    event.endDateTime = newEndZdt.toLocalDateTime();
  }

  /**
   * Function to change the time zone of the calendar.
   * @param newTimeZone The new timezone ID.
   */

  @Override
  public void changeCalendarTimeZone(ZoneId newTimeZone) {
    for (CalendarEvent event : events) {
      if (event instanceof SingleEvent) {
        changeEventTime(event, newTimeZone);
      }
      else if (event instanceof RecurringEvent) {
        RecurringEvent recurringEvent = (RecurringEvent) event;
        for (SingleEvent singleEvent : recurringEvent.recurringEventList) {
          changeEventTime(singleEvent, newTimeZone);
        }
      }
    }
    this.timeZone = newTimeZone;
  }
}