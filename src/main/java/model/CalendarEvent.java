package model;

import java.time.LocalDateTime;

/**
 * Represents a calendar event with details such as subject, start date time,
 * end date time, description, location , private or public.
 */

public class CalendarEvent {
  protected String subject;
  protected LocalDateTime startDateTime;
  protected LocalDateTime endDateTime;
  protected String description;
  protected String location;
  protected boolean isPublic;

  /**
   * Constructs a calendar event with specified details.
   * @param subject subject of event.
   * @param startDateTime start date and time of event.
   * @param endDateTime end date and time of event.
   * @param description a brief description of event.
   * @param location location where event takes place.
   * @param isPublic whether event is public or private.
   */

  public CalendarEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                       String description, String location, boolean isPublic) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
  }

  /**
   * Checks if event conflicts with the other event.
   * @param other other event to compare against.
   * @return true if conflicts, false if not.
   */

  public boolean conflictsWith(CalendarEvent other) {
    LocalDateTime thisStart = this.startDateTime;
    LocalDateTime thisEnd = this.endDateTime;
    LocalDateTime otherStart = other.startDateTime;
    LocalDateTime otherEnd = other.endDateTime;

    boolean condition =  (thisStart.compareTo(otherEnd) >= 0)
            || (thisEnd.compareTo(otherStart) <= 0 );

    return !condition;
  }
}
