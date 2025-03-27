package controller;

import exception.InvalidCommandException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for printing list of events , checking busy status
 * and exporting calendar.
 */

public class CalendarControllerPrintShowExportTest {
  private CalendarController controller;

  /**
   * Initialize the calendar controller.
   */

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  /**
   * Print events on valid date.
   */

  @Test
  public void testPrintEventsOnValidDateWithEvents() {
    controller.processCommand("create event MeetingOne from 2025-03-12T10:00 to 2025-03-12T11:00");

    controller.processCommand("print events on 2025-03-12");

    Assert.assertEquals("[[MeetingOne, 2025-03-12T10:00, 2025-03-12T11:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 12)).toString());
  }


  /**
   * Printing events on a date where no events exist.
   */

  @Test
  public void testPrintEventsOnValidDateNoEvents() {
    controller.processCommand("print events on 2025-04-01");

    Assert.assertEquals("[]", controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());
  }

  /**
   * Test case: Printing events on an invalid date format.
   */

  @Test(expected = InvalidCommandException.class)
  public void testPrintEventsOnInvalidDateFormat() {
    controller.processCommand("print events on 12-03-2025");
  }

  /**
   * Creating multiple events to test.
   */

  @Test
  public void testPrintEventsFromToValidRange() {
    controller.processCommand("create event MeetingA from 2025-03-15T10:00 to 2025-03-15T12:00");
    controller.processCommand("create event MeetingB from 2025-03-15T14:00 to 2025-03-15T16:00");

    controller.processCommand("print events from 2025-03-15T10:00 to 2025-03-15T12:00");

    Assert.assertEquals("[[MeetingA, 2025-03-15T10:00, 2025-03-15T12:00, ]]",
            controller.model.getEventsBetween(
                    LocalDateTime.of(2025, 3, 15, 10, 0),
                    LocalDateTime.of(2025, 3, 15, 12, 0)
            ).toString());
  }

  /**
   * Creating multiple events to test.
   */

  @Test
  public void testPrintEventsFromToValidRangeRecurring() {
    controller.processCommand("create event MeetingA from 2025-03-12T10:00 to "
            + "2025-03-12T12:00 repeats MWF for 2 times");
    controller.processCommand("print events from 2025-03-12T10:00 to 2025-03-12T12:00");
    Assert.assertEquals("[[MeetingA, 2025-03-12T10:00, 2025-03-12T12:00, ]]",
            controller.model.getEventsBetween(
                    LocalDateTime.of(2025, 3, 12, 10, 0),
                    LocalDateTime.of(2025, 3, 12, 12, 0)
            ).toString());
  }

  /**
   * Printing events on a date where multiple events exist.
   */

  @Test
  public void testPrintEventsOnMultipleEvents() {
    controller.processCommand("create event EventA from 2025-06-01T08:00 to "
            + "2025-06-01T09:00 at Room1");
    controller.processCommand("create event EventB from 2025-06-01T10:00 to "
            + "2025-06-01T11:30 at Room2");

    controller.processCommand("print events on 2025-06-01");

    Assert.assertEquals("[[EventA, 2025-06-01T08:00, 2025-06-01T09:00, ], "
                    + "[EventB, 2025-06-01T10:00, 2025-06-01T11:30, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 6, 1)).toString());
  }

  /**
   * Test case: No events exist in the given time range.
   */

  @Test
  public void testPrintEventsFromToNoEvents() {
    controller.processCommand("print events from 2025-05-01T10:00 to 2025-05-01T12:00");

    Assert.assertEquals("[]", controller.model.getEventsBetween(
            LocalDateTime.of(2025, 5, 1, 10, 0),
            LocalDateTime.of(2025, 5, 1, 12, 0)
    ).toString());
  }

  /**
   * Test case: Printing events with an invalid date-time format.
   */

  @Test(expected = InvalidCommandException.class)
  public void testPrintEventsFromToInvalidDateTimeFormat() {
    controller.processCommand("print events from 03/15/2025T10:00 to 03/15/2025T12:00");
  }

  /**
   * Test case: Printing events with end time before start time.
   */

  @Test(expected = InvalidCommandException.class)
  public void testPrintEventsFromToInvalidRange() {
    controller.processCommand("print events from 2025-03-15T12:00 to 2025-03-15T10:00");
  }

  /**
   * Test case: Printing events where events span multiple days.
   */

  @Test
  public void testPrintEventsFromToMultiDayEvent() {
    controller.processCommand("create event Conference from 2025-09-01T09:00 to 2025-09-03T17:00");

    controller.processCommand("print events from 2025-09-01T00:00 to 2025-09-05T00:00");

    Assert.assertEquals("[[Conference, 2025-09-01T09:00, 2025-09-03T17:00, ]]",
            controller.model.getEventsBetween(
                    LocalDateTime.of(2025, 9, 1, 0, 0),
                    LocalDateTime.of(2025, 9, 5, 0, 0)
            ).toString());
  }

  /**
   * Test case: Printing recurring events with from and to.
   */

  @Test
  public void testPrintEventsFromToRecurringEvent() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 "
            + "repeats SU until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-16T00:00, 2025-03-16T01:00, ]]",
            controller.model.getEventsOn(
                    LocalDate.of(2025, 3, 16)
            ).toString());
  }


  /**
   * Invalid print command.
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidPrintCommand1() {
    try {
      controller.processCommand("print events blah");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid show command.
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidShowCommand1() {
    try {
      controller.processCommand("show status blah");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }


  /**
   * Exporting an empty calendar.
   */

  @Test
  public void testExportEmptyCalendar() {
    controller.processCommand("export cal events.csv");
    assertTrue("Exported file should exist even if empty", Files.exists(Paths.get("events.csv")));
  }

  /**
   * Exporting a calendar with events present.
   */

  @Test
  public void testExportCalendarWithEvents() {
    controller.processCommand("create event MeetingA from 2025-03-15T10:00 to 2025-03-15T12:00");
    controller.processCommand("create event MeetingB from 2025-03-16T14:00 to 2025-03-16T16:00");
    controller.processCommand("export cal events.csv");
    assertTrue("Exported file should exist", Files.exists(Paths.get("events.csv")));

    try {
      List<String> lines = Files.readAllLines(Paths.get("events.csv"));
      assertFalse("Exported file should not be empty", lines.isEmpty());
    } catch (IOException e) {
      throw new RuntimeException("Error reading exported file", e);
    }
  }

  /**
   * Exporting a calendar with events present.
   */

  @Test
  public void testExportCalendarWithEventsAllDaySingle() {
    controller.processCommand("create event MeetingA on 2025-03-15T10:00");
    controller.processCommand("create event MeetingB on 2025-03-16T14:00");
    controller.processCommand("export cal events.csv");
    assertTrue("Exported file should exist", Files.exists(Paths.get("events.csv")));

    try {
      List<String> lines = Files.readAllLines(Paths.get("events.csv"));
      assertFalse("Exported file should not be empty", lines.isEmpty());
    } catch (IOException e) {
      throw new RuntimeException("Error reading exported file", e);
    }
  }

  /**
   * Exporting a calendar with events present.
   */

  @Test
  public void testExportCalendarWithEventsAllDayRecurring() {
    controller.processCommand("create event MeetingA on 2025-03-15 repeats MFW for 3 times");
    controller.processCommand("export cal events.csv");
    assertTrue("Exported file should exist", Files.exists(Paths.get("events.csv")));

    try {
      List<String> lines = Files.readAllLines(Paths.get("events.csv"));
      assertFalse("Exported file should not be empty", lines.isEmpty());
    } catch (IOException e) {
      throw new RuntimeException("Error reading exported file", e);
    }
  }

  /**
   * Create, export, edit event, and export again (overwrite test).
   */

  @Test
  public void testCreateExportEditExportOverwrite() {
    controller.processCommand("create event InitialMeeting from "
            + "2025-03-20T09:00 to 2025-03-20T10:00");
    controller.processCommand("export cal events.csv");
    controller.processCommand("edit events subject InitialMeeting UpdatedMeeting");
    controller.processCommand("export cal events.csv");
    assertTrue("Exported file should exist", Files.exists(Paths.get("events.csv")));
  }

  /**
   * Exporting as docx gives error.
   */

  @Test(expected = InvalidCommandException.class)
  public void testExportCalendarInvalidFileExtension() {
    try {
      controller.processCommand("export cal events.docx");
    } catch (Exception e) {
      Assert.assertEquals("Invalid filename or extension", e.getMessage());
      throw e;
    }
  }

  /**
   * Exporting as docx gives error.
   */

  @Test(expected = InvalidCommandException.class)
  public void testExportCalendarInvalidFileExtension2() {
    try {
      controller.processCommand("export cal .csv");
    } catch (Exception e) {
      Assert.assertEquals("Invalid filename or extension", e.getMessage());
      throw e;
    }
  }

  /**
   * Checking busy status when events exist at the given time.
   */

  @Test
  public void testShowStatusBusySingleEvent() {
    controller.processCommand("create event Workshop from "
            + "2025-03-15T10:00 to 2025-03-15T12:00");
    controller.processCommand("show status on 2025-03-15T11:00");
    assertTrue(controller.model.isBusy(LocalDateTime.of(2025, 3, 15, 11, 0)));
  }

  /**
   * Checking busy status when events exist at the start time.
   */

  @Test
  public void testShowStatusBusyRecurringEventStart() {
    controller.processCommand("create event MeetingOne from "
            + "2025-03-12T00:00 to 2025-03-12T01:00 repeats "
            + "MFW until 2025-03-18T00:00");
    controller.processCommand("show status on 2025-03-12T00:00");
    assertTrue(controller.model.isBusy(LocalDateTime.of(2025, 3, 12, 00, 00)));
  }

  /**
   * Checking busy status when events exist at the given time.
   */

  @Test
  public void testShowStatusBusyRecurringEventInBetween() {
    controller.processCommand("create event MeetingOne from "
            + "2025-03-12T00:00 to 2025-03-12T01:00 repeats "
            + "MFW until 2025-03-18T00:00");
    controller.processCommand("show status on 2025-03-12T00:30");
    assertTrue(controller.model.isBusy(LocalDateTime.of(2025, 3, 12, 00, 30)));
  }

  /**
   * Checking busy status when events exist at the given time.
   */

  @Test
  public void testShowStatusBusyRecurringEventFalse() {
    controller.processCommand("create event MeetingOne from "
            + "2025-03-12T00:00 to 2025-03-12T01:00 repeats "
            + "MFW until 2025-03-18T00:00");
    controller.processCommand("show status on 2025-03-12T01:30");
    assertFalse(controller.model.isBusy(LocalDateTime.of(2025, 3, 12, 1, 30)));
  }

  /**
   * Checking busy status when events exist at the end time.
   */

  @Test
  public void testShowStatusBusyRecurringEvent() {
    controller.processCommand("create event MeetingOne from "
            + "2025-03-12T00:00 to 2025-03-12T01:00 repeats "
            + "MFW until 2025-03-18T00:00");
    controller.processCommand("show status on 2025-03-12T01:00");
    assertFalse(controller.model.isBusy(LocalDateTime.of(2025, 3, 12, 1, 0)));
  }

  /**
   * Checking availability when no events.
   */

  @Test
  public void testShowStatusAvailableNoEvents() {
    controller.processCommand("show status on 2025-04-01T10:00");
    assertFalse(controller.model.isBusy(LocalDateTime.of(2025, 4, 1, 10, 0)));
  }

  /**
   * Checking availability when no events exist at the given time.
   */

  @Test
  public void testShowStatusAvailable() {
    controller.processCommand("create event Workshop from 2025-03-15T10:00 to 2025-03-15T12:00");
    controller.processCommand("show status on 2025-04-01T1:00");
    assertFalse(controller.model.isBusy(LocalDateTime.of(2025, 4, 1, 10, 0)));
  }

  /**
   * Test case: Querying the exact start time of an event.
   */

  @Test
  public void testShowStatusAtExactStartTime() {
    controller.processCommand("create event Conference from 2025-05-10T14:00 to 2025-05-10T16:00");
    controller.processCommand("show status on 2025-05-10T14:00");
    assertTrue(controller.model.isBusy(LocalDateTime.of(2025, 5, 10, 14, 0)));
  }

  /**
   * Querying the exact end time of an event.
   */

  @Test
  public void testShowStatusAtExactEndTime() {
    controller.processCommand("create event Workshop from 2025-06-05T09:00 to 2025-06-05T11:00");

    controller.processCommand("show status on 2025-06-05T11:00");

    assertFalse(controller.model.isBusy(LocalDateTime.of(2025, 6, 5, 11, 0)));
  }

  /**
   * Querying a time between two non-overlapping events.
   */

  @Test
  public void testShowStatusBetweenEvents() {
    controller.processCommand("create event MorningMeeting from "
            + "2025-07-10T08:00 to 2025-07-10T09:00");
    controller.processCommand("create event EveningCall from 2025-07-10T18:00 to 2025-07-10T19:00");

    controller.processCommand("show status on 2025-07-10T12:00");

    assertFalse(controller.model.isBusy(LocalDateTime.of(2025, 7, 10, 12, 0)));
  }

  /**
   * Querying a date-time in the past.
   */

  @Test
  public void testShowStatusPastDate() {
    controller.processCommand("create event PastEvent from 2020-01-01T10:00 to 2020-01-01T12:00");

    controller.processCommand("show status on 2020-01-01T11:00");

    assertTrue(controller.model.isBusy(LocalDateTime.of(2020, 1, 1, 11, 0)));
  }
}

