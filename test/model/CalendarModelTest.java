package model;

import exception.InvalidCommandException;
import model.CalendarModel;
import model.SingleEvent;
import model.RecurringEvent;
import model.CalendarEvent;
import exception.EventConflictException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class CalendarModelTest {

  private CalendarModel calendarModel;

  @Before
  public void setUp() {
    calendarModel = new CalendarModel();
  }

  // 1. Export calendar successfully with events
  @Test
  public void testExportCalendarSuccess() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);
    calendarModel.createSingleEvent(new SingleEvent("Meeting", start, end, "Discussion", "Office", false), false);

    String filePath = calendarModel.exportCalendar("calendar_export.csv");
    File exportedFile = new File(filePath);

    assertTrue(exportedFile.exists());
    assertTrue(exportedFile.length() > 0);
  }

  // 2. Export empty calendar
  @Test
  public void testExportEmptyCalendar() throws Exception {
    String filePath = calendarModel.exportCalendar("empty_calendar_export.csv");
    File exportedFile = new File(filePath);

    assertTrue(exportedFile.exists());
    assertEquals(0, exportedFile.length());
  }

  // 3. Export calendar with no file name
  @Test(expected = InvalidCommandException.class)
  public void testExportCalendarWithNoFileName() throws Exception {
    calendarModel.exportCalendar("");
  }

  // 4. Export calendar with invalid file name
  @Test(expected = InvalidCommandException.class)
  public void testExportCalendarWithInvalidFileName() throws Exception {
    calendarModel.exportCalendar("invalid*name.csv");  // Invalid character *
  }

  // 5. Export calendar with a large number of events
  @Test
  public void testExportCalendarWithLargeNumberOfEvents() throws Exception {
    for (int i = 0; i < 1000; i++) {
      LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0).plusHours(i);
      LocalDateTime end = start.plusHours(1);
      calendarModel.createSingleEvent(new SingleEvent("Event " + i, start, end, "Description", "Location", false), false);
    }

    String filePath = calendarModel.exportCalendar("large_calendar_export.csv");
    File exportedFile = new File(filePath);

    assertTrue(exportedFile.exists());
    assertTrue(exportedFile.length() > 0);
  }

  // 6. Export calendar to an invalid file path
  @Test(expected = InvalidCommandException.class)
  public void testExportCalendarToInvalidFilePath() throws Exception {
    calendarModel.exportCalendar("/invalid/path/calendar_export.csv");
  }

  // 7. Create calendar, export, edit events, and export with overwriting
  @Test
  public void testCreateExportEditExportWithOverwrite() throws Exception {
    // Create an event
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);
    calendarModel.createSingleEvent(new SingleEvent("Meeting", start, end, "Discussion", "Office", false), false);

    // Export first time
    String filePath = "overwrite_calendar.csv";
    String export1 = calendarModel.exportCalendar(filePath);
    File exportedFile1 = new File(export1);
    assertTrue(exportedFile1.exists());
    assertTrue(exportedFile1.length() > 0);

    // Edit the event
    calendarModel.editEvents("subject", "Meeting", "Updated Meeting");

    // Export again (overwrite existing file)
    String export2 = calendarModel.exportCalendar(filePath);
    File exportedFile2 = new File(export2);
    assertTrue(exportedFile2.exists());
    assertTrue(exportedFile2.length() > 0);
    assertEquals(export1, export2); // Ensure it's the same file path
  }

  /**
   * Create single event
   */

//  // 1. Create a valid event with all specifications (public event)
  @Test
  public void testCreateValidPublicEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);
    SingleEvent event = new SingleEvent("Meeting", start, end, "Project Discussion", "Office", true);

    CalendarEvent createdEvent = calendarModel.createSingleEvent(event, false);
    assertNotNull(createdEvent);
    assertEquals("Meeting", createdEvent.subject);
    assertEquals(start, createdEvent.startDateTime);
    assertEquals(end, createdEvent.endDateTime);
    assertEquals("Project Discussion", createdEvent.description);
    assertEquals("Office", createdEvent.location);
    assertTrue(createdEvent.isPublic);
  }

  // 2. Create event with subject, start date and time
  @Test
  public void testCreateEventWithSubjectStartDateTime() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = start.plusHours(1);
    SingleEvent event = new SingleEvent("Quick Meeting", start, end, "", "", false);

    CalendarEvent createdEvent = calendarModel.createSingleEvent(event, false);
    assertNotNull(createdEvent);
    assertEquals("Quick Meeting", createdEvent.subject);
    assertEquals(start, createdEvent.startDateTime);
  }

  // 3. Create with only subject - should fail
  @Test(expected = InvalidCommandException.class)
  public void testCreateWithOnlySubject_NotPossible() {
    SingleEvent event = new SingleEvent("Meeting", null, null, "", "", false);
    calendarModel.createSingleEvent(event, false);
  }

  // 4. Create with only start date and time - should fail
  @Test(expected = InvalidCommandException.class)
  public void testCreateWithOnlyStartDateTime_NotPossible() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    SingleEvent event = new SingleEvent("", start, null, "", "", false);
    calendarModel.createSingleEvent(event, false);
  }

  // 5. Create only with subject and start date (without time) - should fail
  @Test(expected = InvalidCommandException.class)
  public void testCreateWithSubjectStartDate_NoTime_NotPossible() {
    LocalDate date = LocalDate.of(2025, 3, 15);
    SingleEvent event = new SingleEvent("Meeting", date.atStartOfDay(), null, "", "", false);
    calendarModel.createSingleEvent(event, false);
  }

  // 6. Create with subject and starting time without starting date - should fail
  @Test(expected = InvalidCommandException.class)
  public void testCreateWithSubjectStartTime_NoDate_NotPossible() {
    LocalDateTime start = LocalDateTime.of(0, 1, 1, 10, 0); // Invalid date
    SingleEvent event = new SingleEvent("Meeting", start, null, "", "", false);
    calendarModel.createSingleEvent(event, false);
  }

  // 7. Create with only description - should fail
  @Test(expected = InvalidCommandException.class)
  public void testCreateWithOnlyDescription_NotPossible() {
    SingleEvent event = new SingleEvent("", null, null, "Project discussion", "", false);
    calendarModel.createSingleEvent(event, false);
  }

  // 8. Create with only end date and time - should fail
  @Test(expected = InvalidCommandException.class)
  public void testCreateWithOnlyEndDateTime_NotPossible() {
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);
    SingleEvent event = new SingleEvent("", null, end, "", "", false);
    calendarModel.createSingleEvent(event, false);
  }

  // 9. Create with only location - should fail
  @Test(expected = InvalidCommandException.class)
  public void testCreateWithOnlyLocation_NotPossible() {
    SingleEvent event = new SingleEvent("", null, null, "", "Conference Room", false);
    calendarModel.createSingleEvent(event, false);
  }

  // 12. Create a valid event with all specifications (private event)
  @Test
  public void testCreateValidPrivateEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);
    SingleEvent event = new SingleEvent("Private Meeting", start, end, "Private Discussion", "Home", false);

    CalendarEvent createdEvent = calendarModel.createSingleEvent(event, false);
    assertNotNull(createdEvent);
    assertEquals("Private Meeting", createdEvent.subject);
    assertFalse(createdEvent.isPublic);
  }

  // 13. Create an all-day event
  @Test
  public void testCreateAllDayEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 0, 0);
    LocalDateTime end = start.plusDays(1); // Ends at midnight the next day
    SingleEvent event = new SingleEvent("All Day Conference", start, end, "Full day event", "Hotel", false);

    CalendarEvent createdEvent = calendarModel.createSingleEvent(event, false);
    assertNotNull(createdEvent);
    assertEquals(start, createdEvent.startDateTime);
    assertEquals(end, createdEvent.endDateTime);
  }

  // 14. Create an event that spans multiple days
  @Test
  public void testCreateMultiDayEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 14, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 16, 18, 0);
    SingleEvent event = new SingleEvent("Multi-Day Workshop", start, end, "3-day event", "Convention Center", false);

    CalendarEvent createdEvent = calendarModel.createSingleEvent(event, false);
    assertNotNull(createdEvent);
    assertEquals(start, createdEvent.startDateTime);
    assertEquals(end, createdEvent.endDateTime);
  }

  // 16. Create an event that starts at midnight
  @Test
  public void testCreateMidnightStartEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 2, 0);
    SingleEvent event = new SingleEvent("Midnight Launch", start, end, "Product launch at midnight", "Online", false);

    CalendarEvent createdEvent = calendarModel.createSingleEvent(event, false);
    assertNotNull(createdEvent);
    assertEquals(start, createdEvent.startDateTime);
    assertEquals(end, createdEvent.endDateTime);
  }

  /**
   * Conflicts testing
   */

//  // 1. Conflict at exact same time
  @Test(expected = EventConflictException.class)
  public void testEventConflict_ExactSameTime() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);

    calendarModel.createSingleEvent(new SingleEvent("Meeting", start, end, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("Workshop", start, end, "", "", false), true);
  }

  // 2. Conflict as starts during existing event
  @Test(expected = EventConflictException.class)
  public void testEventConflict_StartsDuringExistingEvent() {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 15, 12, 0);
    LocalDateTime start2 = LocalDateTime.of(2025, 3, 15, 11, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 15, 13, 0);

    calendarModel.createSingleEvent(new SingleEvent("Meeting", start1, end1, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("Client Call", start2, end2, "", "", false), true);
  }

  // 3. Conflict because ends during existing event
  @Test(expected = EventConflictException.class)
  public void testEventConflict_EndsDuringExistingEvent() {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 15, 12, 0);
    LocalDateTime start2 = LocalDateTime.of(2025, 3, 15, 9, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 15, 11, 0);

    calendarModel.createSingleEvent(new SingleEvent("Morning Meeting", start1, end1, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("Breakfast Meeting", start2, end2, "", "", false), true);
  }

  // 4. Conflict: Event is within the timespan of another event
  @Test(expected = EventConflictException.class)
  public void testEventConflict_WithinTimespan() {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 15, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 15, 12, 0);
    LocalDateTime start2 = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 15, 11, 0);

    calendarModel.createSingleEvent(new SingleEvent("Main Event", start1, end1, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("Sub Event", start2, end2, "", "", false), true);
  }

  // 5. Conflict: Existing event is within new event
  @Test(expected = EventConflictException.class)
  public void testEventConflict_NewEventEncapsulatesExisting() {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 15, 11, 0);
    LocalDateTime start2 = LocalDateTime.of(2025, 3, 15, 9, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 15, 12, 0);

    calendarModel.createSingleEvent(new SingleEvent("Short Meeting", start1, end1, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("Big Event", start2, end2, "", "", false), true);
  }

  // 6-10: No conflicts when auto decline is disabled
  @Test
  public void testNoConflict_AutoDeclineDisabled() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);

    calendarModel.createSingleEvent(new SingleEvent("Event 1", start, end, "", "", false), false);
    calendarModel.createSingleEvent(new SingleEvent("Event 2", start, end, "", "", false), false); // Should not throw exception
  }

  // 11. Recurring event with same time conflict
  @Test(expected = EventConflictException.class)
  public void testRecurringEventConflict() {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);
    RecurringEvent recurringEvent = new RecurringEvent("Daily Standup", start, end, "", "", false, "MTWRF", 5, null);

    calendarModel.createRecurringEvent(recurringEvent);
    calendarModel.createSingleEvent(new SingleEvent("Workshop", start, end, "", "", false), true);
  }

  // 12. Multi-day event overlaps recurring event
  @Test(expected = EventConflictException.class)
  public void testMultiDayOverlapsRecurring() {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 14, 10, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 16, 12, 0);
    LocalDateTime start2 = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 15, 12, 0);

    RecurringEvent recurringEvent = new RecurringEvent("Daily Meeting", start2, end2, "", "", false, "MTWRF", 5, null);
    calendarModel.createRecurringEvent(recurringEvent);
    calendarModel.createSingleEvent(new SingleEvent("Multi-Day Conference", start1, end1, "", "", false), true);
  }

  // 13. Multi-day event overlaps single-day event
  @Test(expected = EventConflictException.class)
  public void testMultiDayOverlapsSingleDay() {
    LocalDateTime multiDayStart = LocalDateTime.of(2025, 3, 14, 10, 0);
    LocalDateTime multiDayEnd = LocalDateTime.of(2025, 3, 16, 12, 0);
    LocalDateTime singleDayStart = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime singleDayEnd = LocalDateTime.of(2025, 3, 15, 12, 0);

    calendarModel.createSingleEvent(new SingleEvent("Multi-Day Event", multiDayStart, multiDayEnd, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("Single-Day Event", singleDayStart, singleDayEnd, "", "", false), true);
  }

  // 14. Auto-decline disabled: No conflict when multi-day event overlaps single-day event
  @Test
  public void testNoConflict_MultiDayOverlapsSingleDay_AutoDeclineDisabled() {
    LocalDateTime multiDayStart = LocalDateTime.of(2025, 3, 14, 10, 0);
    LocalDateTime multiDayEnd = LocalDateTime.of(2025, 3, 16, 12, 0);
    LocalDateTime singleDayStart = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime singleDayEnd = LocalDateTime.of(2025, 3, 15, 12, 0);

    calendarModel.createSingleEvent(new SingleEvent("Multi-Day Event", multiDayStart, multiDayEnd, "", "", false), false);
    calendarModel.createSingleEvent(new SingleEvent("Single-Day Event", singleDayStart, singleDayEnd, "", "", false), false);
  }

  // 15. All-day event overlaps with a timed event
  @Test(expected = EventConflictException.class)
  public void testAllDayEventOverlapsTimedEvent() {
    LocalDateTime allDayStart = LocalDateTime.of(2025, 3, 15, 0, 0);
    LocalDateTime allDayEnd = LocalDateTime.of(2025, 3, 16, 0, 0);
    LocalDateTime timedStart = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime timedEnd = LocalDateTime.of(2025, 3, 15, 12, 0);

    calendarModel.createSingleEvent(new SingleEvent("All-Day Event", allDayStart, allDayEnd, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("Timed Event", timedStart, timedEnd, "", "", false), true);
  }

  // 16. Auto-decline disabled: No conflict when all-day event overlaps timed event
  @Test
  public void testNoConflict_AllDayOverlapsTimedEvent_AutoDeclineDisabled() {
    LocalDateTime allDayStart = LocalDateTime.of(2025, 3, 15, 0, 0);
    LocalDateTime allDayEnd = LocalDateTime.of(2025, 3, 16, 0, 0);
    LocalDateTime timedStart = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime timedEnd = LocalDateTime.of(2025, 3, 15, 12, 0);

    calendarModel.createSingleEvent(new SingleEvent("All-Day Event", allDayStart, allDayEnd, "", "", false), false);
    calendarModel.createSingleEvent(new SingleEvent("Timed Event", timedStart, timedEnd, "", "", false), false);
  }

  // 17. All-day event overlaps with another all-day event
  @Test(expected = EventConflictException.class)
  public void testAllDayEventOverlapsAllDayEvent() {
    LocalDateTime allDayStart1 = LocalDateTime.of(2025, 3, 15, 0, 0);
    LocalDateTime allDayEnd1 = LocalDateTime.of(2025, 3, 16, 0, 0);
    LocalDateTime allDayStart2 = LocalDateTime.of(2025, 3, 15, 0, 0);
    LocalDateTime allDayEnd2 = LocalDateTime.of(2025, 3, 16, 0, 0);

    calendarModel.createSingleEvent(new SingleEvent("All-Day Event 1", allDayStart1, allDayEnd1, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("All-Day Event 2", allDayStart2, allDayEnd2, "", "", false), true);
  }

  // 18. Auto-decline disabled: No conflict when all-day event overlaps with another all-day event
  @Test
  public void testNoConflict_AllDayOverlapsAllDayEvent_AutoDeclineDisabled() {
    LocalDateTime allDayStart1 = LocalDateTime.of(2025, 3, 15, 0, 0);
    LocalDateTime allDayEnd1 = LocalDateTime.of(2025, 3, 16, 0, 0);
    LocalDateTime allDayStart2 = LocalDateTime.of(2025, 3, 15, 0, 0);
    LocalDateTime allDayEnd2 = LocalDateTime.of(2025, 3, 16, 0, 0);

    calendarModel.createSingleEvent(new SingleEvent("All-Day Event 1", allDayStart1, allDayEnd1, "", "", false), false);
    calendarModel.createSingleEvent(new SingleEvent("All-Day Event 2", allDayStart2, allDayEnd2, "", "", false), false);
  }

  // 19. Editing event causes conflict with an overlapping existing event
  @Test(expected = EventConflictException.class)
  public void testEditingEventCausesConflict() throws Exception {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 15, 12, 0);
    LocalDateTime start2 = LocalDateTime.of(2025, 3, 15, 8, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 15, 9, 0);

    calendarModel.createSingleEvent(new SingleEvent("Event 1", start1, end1, "", "", false), true);
    calendarModel.createSingleEvent(new SingleEvent("Event 2", start2, end2, "", "", false), true);

    calendarModel.editEvents("startDateTime", "2025-03-15T08:00", start2, "2025-03-15T10:30"); // Should cause conflict
  }

  // 20. Auto-decline disabled: No conflict when editing event overlaps an existing one
  @Test
  public void testNoConflict_EditingEventOverlap_AutoDeclineDisabled() throws Exception {
    LocalDateTime start1 = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end1 = LocalDateTime.of(2025, 3, 15, 12, 0);
    LocalDateTime start2 = LocalDateTime.of(2025, 3, 15, 8, 0);
    LocalDateTime end2 = LocalDateTime.of(2025, 3, 15, 9, 0);

    calendarModel.createSingleEvent(new SingleEvent("Event 1", start1, end1, "", "", false), false);
    calendarModel.createSingleEvent(new SingleEvent("Event 2", start2, end2, "", "", false), false);

    calendarModel.editEvents("startDateTime", "2025-03-15T08:00", start2, "2025-03-15T10:30"); // No conflict
  }

  // 21. Editing recurring event to overlap existing event
  @Test(expected = EventConflictException.class)
  public void testEditingRecurringEventCausesConflict() throws Exception {
    LocalDateTime start = LocalDateTime.of(2025, 3, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 15, 12, 0);

    RecurringEvent recurringEvent = new RecurringEvent("Daily Standup", start, end, "", "", false, "MTWRF", 5, null);
    calendarModel.createRecurringEvent(recurringEvent);

    calendarModel.editEvents("startDateTime", "2025-03-15T10:00", start, "2025-03-15T11:00"); // Should cause conflict
  }
}
