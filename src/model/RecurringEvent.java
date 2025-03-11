package model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RecurringEvent extends CalendarEvent{
  List<SingleEvent> recurringEventList;

  public RecurringEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                        String description, String location, boolean isPublic, String weekDays,
                        int n, LocalDateTime untilDateTime) {
    super(subject, startDateTime, endDateTime, description, location, isPublic);

    boolean isEntireDay = startDateTime.toLocalTime().equals(LocalTime.MIDNIGHT);
    recurringEventList = new ArrayList<SingleEvent>();
    LocalDateTime currentDateTime = startDateTime;

    if (n == 0) {
      while (currentDateTime.isBefore(untilDateTime)) {
        if (checkWeekDayValidity(currentDateTime.getDayOfWeek().toString(), weekDays)) {
          if (isEntireDay) {
            recurringEventList.add(new SingleEvent(subject, LocalDateTime.of(currentDateTime.toLocalDate(), startDateTime.toLocalTime()),
                    LocalDateTime.of(currentDateTime.plusDays(1).toLocalDate(), endDateTime.toLocalTime()), description, location, isPublic));
          }
          else {
            recurringEventList.add(new SingleEvent(subject, LocalDateTime.of(currentDateTime.toLocalDate(), startDateTime.toLocalTime()),
                    LocalDateTime.of(currentDateTime.toLocalDate(), endDateTime.toLocalTime()), description, location, isPublic));
          }
        }
        currentDateTime = currentDateTime.plusDays(1);
      }
    }
    else {
      while (n > 0) {
        if (checkWeekDayValidity(currentDateTime.getDayOfWeek().toString(), weekDays)) {
          if (isEntireDay) {
            recurringEventList.add(new SingleEvent(subject, LocalDateTime.of(currentDateTime.toLocalDate(), startDateTime.toLocalTime()),
                    LocalDateTime.of(currentDateTime.plusDays(1).toLocalDate(), endDateTime.toLocalTime()), description, location, isPublic));
          }
          else {
            recurringEventList.add(new SingleEvent(subject, LocalDateTime.of(currentDateTime.toLocalDate(), startDateTime.toLocalTime()),
                    LocalDateTime.of(currentDateTime.toLocalDate(), endDateTime.toLocalTime()), description, location, isPublic));
          }
          n--;
        }
        currentDateTime = currentDateTime.plusDays(1);
      }
    }
  }

  private boolean checkWeekDayValidity(String currentDay, String weekDays) {
    if (currentDay.equals("MONDAY") && weekDays.contains(String.valueOf('M'))) {
      return true;
    }
    else if (currentDay.equals("TUESDAY") && weekDays.contains(String.valueOf('T'))) {
      return true;
    }
    else if (currentDay.equals("WEDNESDAY") && weekDays.contains(String.valueOf('W'))) {
      return true;
    }
    else if (currentDay.equals("THURSDAY") && weekDays.contains(String.valueOf('R'))) {
      return true;
    }
    else if (currentDay.equals("FRIDAY") && weekDays.contains(String.valueOf('F'))) {
      return true;
    }
    else if (currentDay.equals("SATURDAY") && weekDays.contains(String.valueOf('S'))) {
      return true;
    }
    else if (currentDay.equals("SUNDAY") && weekDays.contains(String.valueOf('U'))) {
      return true;
    }
    return false;
  }
}
