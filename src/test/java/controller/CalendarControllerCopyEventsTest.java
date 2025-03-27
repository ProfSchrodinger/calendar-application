package controller;

import exception.InvalidCommandException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests for copying events.
 */

public class CalendarControllerCopyEventsTest {
  private CalendarController controller;

  /**
   * Initialize the calendar controller.
   */

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  /**
   * Tests copying a single event to another calendar with timezone change.
   */

  @Test
  public void testCopySingleEventToAnotherCalendar() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone US/Pacific");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Breakfast from 2025-04-01T09:00 to 2025-04-01T10:00");

    controller.processCommand("copy event Breakfast on 2025-04-01T09:00 "
            + "--target TargetCal to 2025-04-02T16:00");

    controller.processCommand("use calendar --name TargetCal");
    Assert.assertEquals("[[Breakfast, 2025-04-02T13:00, 2025-04-02T14:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).toString());
  }

  /**
   * Tests that copying to a non-existent target calendar.
   * @throws InvalidCommandException if the target calendar does not exist.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCopyToNonexistentTargetCalendarThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Review from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy event Review on 2025-04-01T09:00 "
              + "--target TargetCal to 2025-04-02T09:00");
    }
    catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  /**
   * Test to copy from a non-existent source calendar.
   * @throws InvalidCommandException if the source calendar does not exist.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCopyFromNonexistentSourceCalendarThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name TargetCal --timezone America/New_York");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Review from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy event Review on 2025-04-01T09:00 "
              + "--target TargetCal to 2025-04-02T09:00");
    }
    catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests with an invalid date format in the copy command.
   * @throws InvalidCommandException if the date format is invalid.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventInvalidDateFormat() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone Europe/London");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Demo from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy event Demo on 01-04-2025T09:00 "
              + "--target TargetCal to 02-04-2025T10:00");
    }
    catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid date formats", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests copying an event to the same start time on a different calendar
   * in the same time zone.
   */

  @Test
  public void testCopyEventSameStartTimeToDifferentCalendar() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone America/New_York");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Yoga from 2025-04-01T07:00 to 2025-04-01T08:00");

    controller.processCommand("copy event Yoga on 2025-04-01T07:00 "
            + "--target TargetCal to 2025-04-01T07:00");

    controller.processCommand("use calendar --name TargetCal");
    Assert.assertEquals("[[Yoga, 2025-04-01T07:00, 2025-04-01T08:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());
  }

  /**
   * Test copying multiple events on the same date to another calendar,
   * with correct timezone-based time conversion.
   */

  @Test
  public void testCopyMultipleEventsToAnotherCalendar() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone Europe/London");
    controller.processCommand("use calendar --name SourceCal");

    controller.processCommand("create event Breakfast from 2025-04-01T08:00 to 2025-04-01T09:00");
    controller.processCommand("create event Standup from 2025-04-01T10:00 to 2025-04-01T10:15");

    controller.processCommand("copy events on 2025-04-01 --target TargetCal to 2025-04-02");

    controller.processCommand("use calendar --name TargetCal");
    Assert.assertEquals(
            "[[Breakfast, 2025-04-02T13:00, 2025-04-02T14:00, ], "
                    + "[Standup, 2025-04-02T15:00, 2025-04-02T15:15, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).toString()
    );
  }

  /**
   * Tests copying from a date with no events.
   * Should result in no copied events.
   */

  @Test
  public void testCopyEventsOnDateWithNoEvents() {
    controller.processCommand("create calendar --name SourceCal1 --timezone UTC");
    controller.processCommand("create calendar --name TargetCal1 --timezone UTC");
    controller.processCommand("use calendar --name SourceCal1");

    controller.processCommand("copy events on 2025-04-01 --target TargetCal1 to 2025-04-02");

    controller.processCommand("use calendar --name TargetCal1");
    Assert.assertTrue(controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).isEmpty());
  }

  /**
   * Tests that copying events to a non-existent calendar throws an exception.
   * @throws InvalidCommandException if the target calendar does not exist.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventsToNonexistentTargetCalendarThrowsError()
          throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone UTC");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Work from 2025-04-01T09:00 to 2025-04-01T17:00");

    try {
      controller.processCommand("copy events on 2025-04-01 --target TargetCal to 2025-04-02");
      Assert.fail("Expected InvalidCommandException was not thrown");
    }
    catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests with invalid date format in a bulk event copy.
   * @throws InvalidCommandException if the date format is invalid.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventsWithInvalidDateFormatThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone UTC");
    controller.processCommand("create calendar --name TargetCal --timezone UTC");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Call from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy events on 01-04-2025 --target TargetCal to 2025/04/02");
    }
    catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid date formats", e.getMessage());
      throw e;
    }
  }

  /**
   * Test to copy an event near midnight, verifying correct timezone
   * conversion and date change in the target calendar.
   */

  @Test
  public void testCopyEventsWithTimezoneCrossingMidnight() {
    controller.processCommand("create calendar --name SourceCal --timezone Asia/Tokyo");
    controller.processCommand("create calendar --name TargetCal --timezone US/Pacific");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event LateWork from 2025-04-01T23:00 to 2025-04-02T00:00");

    controller.processCommand("copy events on 2025-04-01 --target TargetCal to 2025-04-03");

    controller.processCommand("use calendar --name TargetCal");
    Assert.assertEquals("[[LateWork, 2025-04-03T07:00, 2025-04-03T08:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 3)).toString());
  }

  /**
   * Tests copying events to the same calendar but on a new date.
   */

  @Test
  public void testCopyEventsToSameCalendarOnNewDate() {
    controller.processCommand("create calendar --name SelfCal --timezone UTC");
    controller.processCommand("use calendar --name SelfCal");
    controller.processCommand("create event Repeat from 2025-04-01T09:00 to 2025-04-01T10:00");

    controller.processCommand("copy events on 2025-04-01 --target SelfCal to 2025-04-02");

    Assert.assertEquals("[[Repeat, 2025-04-02T09:00, 2025-04-02T10:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).toString());
  }

  /**
   * Tests copying multiple days of events between calendars with time zone change.
   */

  @Test
  public void testCopyMultipleDaysOfEventsToTargetCalendar() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone US/Pacific");
    controller.processCommand("use calendar --name SourceCal");

    controller.processCommand("create event A from 2025-04-01T09:00 to 2025-04-01T10:00");
    controller.processCommand("create event B from 2025-04-02T10:00 to 2025-04-02T11:00");
    controller.processCommand("create event C from 2025-04-03T11:00 to 2025-04-03T12:00");

    controller.processCommand("copy events between 2025-04-01 and 2025-04-04 "
            + "--target TargetCal to 2025-04-10");

    controller.processCommand("use calendar --name TargetCal");

    Assert.assertEquals("[[A, 2025-04-10T06:00, 2025-04-10T07:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 10)).toString());
    Assert.assertEquals("[[B, 2025-04-11T07:00, 2025-04-11T08:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 11)).toString());
    Assert.assertEquals("[[C, 2025-04-12T08:00, 2025-04-12T09:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 12)).toString());
  }

  /**
   * Tests copying from a date range that has no events;
   * target calendar should remain empty.
   */

  @Test
  public void testCopyIntervalWithNoEventsCopiesNothing() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone Asia/Tokyo");
    controller.processCommand("use calendar --name SourceCal");

    controller.processCommand("copy events between 2025-04-01 and 2025-04-03 "
            + "--target TargetCal to 2025-04-10");

    controller.processCommand("use calendar --name TargetCal");
    Assert.assertTrue(controller.model.getEventsOn(LocalDate.of(2025, 4, 10)).isEmpty());
    Assert.assertTrue(controller.model.getEventsOn(LocalDate.of(2025, 4, 11)).isEmpty());
    Assert.assertTrue(controller.model.getEventsOn(LocalDate.of(2025, 4, 12)).isEmpty());
  }

  /**
   * Tests with invalid date formats in a range-based copy command.
   * @throws InvalidCommandException if the date format is invalid.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventsBetweenWithInvalidDateFormatThrowsError()
          throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone Asia/Tokyo");
    controller.processCommand("use calendar --name SourceCal");

    controller.processCommand("copy events between 04-01-2025 and 2025/04/03 "
            + "--target TargetCal to 2025-04-10");
  }

  /**
   * Tests copying multiple events to the same calendar.
   */

  @Test
  public void testCopyEventsToSameCalendar() {
    controller.processCommand("create calendar --name MyCal --timezone America/New_York");
    controller.processCommand("use calendar --name MyCal");

    controller.processCommand("create event Meeting from 2025-04-01T08:00 to 2025-04-01T09:00");
    controller.processCommand("create event Meeting2 from 2025-04-02T08:00 to 2025-04-02T09:00");

    controller.processCommand("copy events between 2025-04-01 and 2025-04-03 "
            + "--target MyCal to 2025-04-10");

    Assert.assertEquals("[[Meeting, 2025-04-10T08:00, 2025-04-10T09:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 10)).toString());
    Assert.assertEquals("[[Meeting2, 2025-04-11T08:00, 2025-04-11T09:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 11)).toString());
  }

  /**
   * Test to copy a range of events to a non-existent calendar.
   */

  @Test
  public void testCopyEventsBetweenToNonexistentTargetCalendarThrowsError() {
    controller.processCommand("create calendar --name SourceCal --timezone UTC");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Task from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy events between 2025-04-01 and 2025-04-02 "
              + "--target TargetCal to 2025-04-10");
      Assert.fail("Expected InvalidCommandException was not thrown");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
    }
  }

  /**
   * Tests copying an all-day recurring event to another calendar with time zone conversion.
   */

  @Test
  public void testCopyRecurringEventsCopyType1() {
    controller.processCommand("create event MeetingOne on 2025-03-12 repeats MWF for 3 times");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-13T00:00, ], "
                    + "[MeetingOne, 2025-03-14T00:00, 2025-03-15T00:00, ], "
                    + "[MeetingOne, 2025-03-17T00:00, 2025-03-18T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00),
                    LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("create calendar --name Calendar2 --timezone US/Pacific");
    controller.processCommand("copy event MeetingOne on 2025-03-12T00:00 "
            + "--target Calendar2 to 2025-06-01T14:00");
    controller.processCommand("use calendar --name Calendar2");
    Assert.assertEquals("[[MeetingOne, 2025-06-01T11:00, 2025-06-02T11:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 6, 01, 00, 00),
                    LocalDateTime.of(2025, 6, 20, 00, 00)).toString());
  }

  /**
   * Tests copying both a recurring and non-recurring time-based event to another calendar.
   */

  @Test
  public void testCopyRecurringEventsCopyType2() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to "
            + "2025-03-12T01:00 repeats MWF for 3 times");
    controller.processCommand("create event MeetingTwo from 2025-03-12T01:00 to 2025-03-12T02:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], "
                    + "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], "
                    + "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], "
                    + "[MeetingTwo, 2025-03-12T01:00, 2025-03-12T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00),
                    LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("create calendar --name Calendar2 --timezone US/Pacific");
    controller.processCommand("copy events on 2025-03-12 --target Calendar2 to 2025-06-01");
    controller.processCommand("use calendar --name Calendar2");
    Assert.assertEquals("[[MeetingOne, 2025-05-31T21:00, 2025-05-31T22:00, ], "
                    + "[MeetingTwo, 2025-05-31T22:00, 2025-05-31T23:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 5, 31, 00, 00),
                    LocalDateTime.of(2025, 6, 20, 00, 00)).toString());
  }

  /**
   * Tests copying multiple intervals and types of
   * recurring and one-time events to a target calendar.
   */

  @Test
  public void testCopyRecurringEventsCopyType3() {
    controller.processCommand("create event MeetingZero on 2025-03-10 repeats MWF for 1 times");
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to "
            + "2025-03-12T01:00 repeats MWF for 3 times");
    controller.processCommand("create event MeetingTwo from 2025-03-12T01:00 to 2025-03-12T02:00");
    controller.processCommand("create event MeetingThree on 2025-03-17T01:00");
    controller.processCommand("create event MeetingFour on 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingZero, 2025-03-10T00:00, 2025-03-11T00:00, ], "
                    + "[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], "
                    + "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], "
                    + "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], "
                    + "[MeetingTwo, 2025-03-12T01:00, 2025-03-12T02:00, ], "
                    + "[MeetingThree, 2025-03-17T01:00, 2025-03-18T00:00, ], "
                    + "[MeetingFour, 2025-03-18T00:00, 2025-03-19T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 10, 00, 00),
                    LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("create calendar --name Calendar2 --timezone US/Pacific");
    controller.processCommand("copy events between 2025-03-12 and "
            + "2025-03-18 --target Calendar2 to 2025-06-01");
    controller.processCommand("copy events between 2025-03-18 and "
            + "2025-03-19 --target Calendar2 to 2025-07-01");
    controller.processCommand("copy events between 2025-03-10 and "
            + "2025-03-11 --target Calendar2 to 2025-08-01");
    controller.processCommand("copy events between 2025-03-11 and "
            + "2025-03-12 --target Calendar2 to 2025-09-01");
    controller.processCommand("use calendar --name Calendar2");
    Assert.assertEquals("[[MeetingOne, 2025-05-31T21:00, 2025-05-31T22:00, ], "
                    + "[MeetingOne, 2025-06-02T21:00, 2025-06-02T22:00, ], "
                    + "[MeetingOne, 2025-06-05T21:00, 2025-06-05T22:00, ], "
                    + "[MeetingTwo, 2025-05-31T22:00, 2025-05-31T23:00, ], "
                    + "[MeetingThree, 2025-06-05T22:00, 2025-06-06T21:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 5, 30, 00, 00),
                    LocalDateTime.of(2025, 6, 20, 00, 00)).toString());
    Assert.assertEquals("[[MeetingFour, 2025-06-30T21:00, 2025-07-01T21:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 6, 30, 00, 00),
                    LocalDateTime.of(2025, 7, 20, 00, 00)).toString());
    Assert.assertEquals("[[MeetingZero, 2025-07-31T21:00, 2025-08-01T21:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 7, 30, 00, 00),
                    LocalDateTime.of(2025, 8, 20, 00, 00)).toString());
    Assert.assertEquals("[]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 8, 30, 00, 00),
                    LocalDateTime.of(2025, 9, 20, 00, 00)).toString());
  }
}
