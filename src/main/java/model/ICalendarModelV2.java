package model;

import java.time.ZoneId;

public interface ICalendarModelV2 extends ICalendarModel {
  void changeCalendarName(String newName);
  void changeCalendarTimeZone(ZoneId newTimeZone);
}
