package model;

import java.time.LocalDateTime;

/**
 * This class represents a single event.
 */

public class SingleEvent extends CalendarEvent{

  /**
   * Constructs a SingleEvent with the specified details.
   * @param subject subject of event.
   * @param startDateTime The start date and time of the event.
   * @param endDateTime The end date and time of the event.
   * @param description Small description of event.
   * @param location Location of event.
   * @param isPublic tells whether event is public or private.
   */

  public SingleEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                     String description, String location, boolean isPublic) {
    super(subject, startDateTime, endDateTime, description, location, isPublic);
  }

  /**
   * Copy constructor.
   * @param other other Single Event object.
   */

  public SingleEvent(SingleEvent other) {
    super(other.subject, other.startDateTime, other.endDateTime,
            other.description, other.location, other.isPublic);
  }
}
