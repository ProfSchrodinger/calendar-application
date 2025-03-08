package model;

import java.time.LocalDateTime;

public class SingleEvent extends CalendarEvent{
  public SingleEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                     String description, String location, boolean isPublic) {
    super(subject, startDateTime, endDateTime, description, location, isPublic);
  }
}
