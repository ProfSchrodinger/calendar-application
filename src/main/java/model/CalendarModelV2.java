package model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarModelV2 extends CalendarModel implements ICalendarModelV2 {

  String calendarName;
  ZoneId timeZone;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Constructs an empty calendar model.
   * @param calendarName
   * @param timeZone
   */
  public CalendarModelV2(String calendarName, ZoneId timeZone) {
    super();
    this.calendarName = calendarName;
    this.timeZone = timeZone;
  }

  @Override
  public void changeCalendarName(String newName) {
    this.calendarName = newName;
  }

  private void changeEventTime(CalendarEvent event, ZoneId newTimeZone) {
    ZonedDateTime oldStartZdt = event.startDateTime.atZone(this.timeZone);
    ZonedDateTime newStartZdt = oldStartZdt.withZoneSameInstant(newTimeZone);
    event.startDateTime = newStartZdt.toLocalDateTime();

    ZonedDateTime oldEndZdt = event.endDateTime.atZone(this.timeZone);
    ZonedDateTime newEndZdt = oldEndZdt.withZoneSameInstant(newTimeZone);
    event.endDateTime = newEndZdt.toLocalDateTime();
  }

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