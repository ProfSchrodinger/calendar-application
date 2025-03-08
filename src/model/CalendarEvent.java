package model;

import java.time.LocalDateTime;

public class CalendarEvent {
  private String subject;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String description;
  private String location;
  private boolean isPublic;

  public CalendarEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                       String description, String location, boolean isPublic) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
  }

  public boolean conflictsWith(CalendarEvent other) {
    LocalDateTime thisStart = this.startDateTime;
    LocalDateTime thisEnd = this.endDateTime;
    LocalDateTime otherStart = other.startDateTime;
    LocalDateTime otherEnd = other.endDateTime;

    // Simplified conflict check: if both have end times.
    if (thisEnd != null && otherEnd != null) {
      return thisStart.isBefore(otherEnd) && otherStart.isBefore(thisEnd);
    }
    // Handle cases where one or both events are all-day (end time is null)
    return thisStart.equals(otherStart);
  }
}
