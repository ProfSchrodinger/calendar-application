package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import exception.InvalidCommandException;

public class CalendarAppTest {

  private ICalendarModel model;

  @Before
  public void setUp() {
    model = new CalendarModel();
  }

  @Test
  public void testCreateSingleEvent() {
    CalendarEvent event = new SingleEvent(
            "Team Meeting",
            LocalDateTime.parse("2025-12-01T10:00"),
            LocalDateTime.parse("2025-12-01T11:00"),
            "Discuss project progress",
            "Conference Room",
            true
    );
    CalendarEvent createdEvent = model.createSingleEvent(event, false);
    assertNotNull(createdEvent);
    assertEquals("Team Meeting", createdEvent.subject);
  }

  @Test
  public void testCreateSingleEvent_WithSubjectAndStartDateTime() {
    CalendarEvent event = new SingleEvent(
            "Morning Standup",
            LocalDateTime.parse("2025-12-01T09:00"),
            null,
            null,
            null,
            false
    );

    CalendarEvent createdEvent = model.createSingleEvent(event, false);
    assertNotNull(createdEvent);
    assertEquals("Morning Standup", createdEvent.subject);
  }

  @Test
  public void testCreateEvent_OnlySubject() {
    CalendarEvent event = new SingleEvent(
            "Meeting Only Subject",
            null,
            null,
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_OnlyStartDateTime() {
    CalendarEvent event = new SingleEvent(
            null,
            LocalDateTime.parse("2025-12-02T10:00"),
            null,
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_OnlyStartDate() {
    CalendarEvent event = new SingleEvent(
            null,
            LocalDate.parse("2025-12-03").atStartOfDay(),
            null,
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_OnlyStartTime() {
    CalendarEvent event = new SingleEvent(
            null,
            LocalDateTime.of(LocalDate.now(), java.time.LocalTime.parse("10:00")),
            null,
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }


  @Test
  public void testCreateEvent_OnlyEndTime() {
    CalendarEvent event = new SingleEvent(
            null,
            null,
            LocalDateTime.of(LocalDate.now(), java.time.LocalTime.parse("11:00")),
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_OnlyEndDate() {
    CalendarEvent event = new SingleEvent(
            null,
            null,
            LocalDate.parse("2025-12-04").atStartOfDay(),
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_OnlyEndDateTime() {
    CalendarEvent event = new SingleEvent(
            null,
            null,
            LocalDateTime.parse("2025-12-05T18:00"),
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_SubjectAndStartDate_NoTime() {
    CalendarEvent event = new SingleEvent(
            "No Time Event",
            LocalDate.parse("2025-12-06").atStartOfDay(),
            null,
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_SubjectAndStartTime_NoDate() {
    CalendarEvent event = new SingleEvent(
            "Floating Time Event",
            LocalDateTime.of(LocalDate.now(), java.time.LocalTime.parse("14:00")),
            null,
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_OnlyStartDateTime_NoSubject() {
    CalendarEvent event = new SingleEvent(
            null,
            LocalDateTime.parse("2025-12-07T15:00"),
            null,
            null,
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_OnlyDescription() {
    CalendarEvent event = new SingleEvent(
            null,
            null,
            null,
            "Project Review",
            null,
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_OnlyLocation() {
    CalendarEvent event = new SingleEvent(
            null,
            null,
            null,
            null,
            "Main Hall",
            false
    );
    model.createSingleEvent(event, false);
  }

  @Test
  public void testCreateEvent_Public() {
    CalendarEvent event = new SingleEvent(
            "Public Conference",
            LocalDateTime.parse("2025-12-08T10:00"),
            LocalDateTime.parse("2025-12-08T12:00"),
            null,
            null,
            true
    );
    CalendarEvent createdEvent = model.createSingleEvent(event, false);
    assertTrue(createdEvent.isPublic);
  }

  @Test
  public void testCreateEvent_Private() {
    CalendarEvent event = new SingleEvent(
            "Private Meeting",
            LocalDateTime.parse("2025-12-09T14:00"),
            LocalDateTime.parse("2025-12-09T15:30"),
            null,
            null,
            false
    );
    CalendarEvent createdEvent = model.createSingleEvent(event, false);
    assertFalse(createdEvent.isPublic);

  }

  @Test
  public void testCreateEvent_AllDay() {
    CalendarEvent event = new SingleEvent(
            "All Day Workshop",
            LocalDateTime.parse("2025-12-10T00:00"),
            LocalDateTime.parse("2025-12-11T00:00"),
            null,
            null,
            false
    );
    CalendarEvent createdEvent = model.createSingleEvent(event, false);
    assertNotNull(createdEvent);
  }

  @Test
  public void testCreateEvent_SpanningDays() {
    CalendarEvent event = new SingleEvent(
            "Multi-Day Conference",
            LocalDateTime.parse("2025-12-11T09:00"),
            LocalDateTime.parse("2025-12-14T18:00"),
            null,
            null,
            false
    );
    CalendarEvent createdEvent = model.createSingleEvent(event, false);
    assertNotNull(createdEvent);
  }

  @Test
  public void testCreateEvent_StartsAtMidnight() {
    CalendarEvent event = new SingleEvent(
            "Midnight Launch",
            LocalDateTime.parse("2025-12-15T00:00"),
            LocalDateTime.parse("2025-12-15T02:00"),
            null,
            null,
            false
    );
    CalendarEvent createdEvent = model.createSingleEvent(event, false);
    assertNotNull(createdEvent);
  }
}
