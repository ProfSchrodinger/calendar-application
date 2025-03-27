package controller;

import exception.InvalidCommandException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;


public class CalendarControllerCopyEventsTest {
  private CalendarController controller;

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  @Test
  public void testCopySingleEventToAnotherCalendar() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone US/Pacific");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Breakfast from 2025-04-01T09:00 to 2025-04-01T10:00");

    controller.processCommand("copy event Breakfast on 2025-04-01T09:00 --target TargetCal to 2025-04-02T16:00");

    controller.processCommand("use calendar --name TargetCal");
    Assert.assertEquals("[[Breakfast, 2025-04-02T13:00, 2025-04-02T14:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).toString());
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyNonexistentEventThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal1 --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal1 --timezone Europe/London");
    controller.processCommand("use calendar --name SourceCal1");

    try {
      controller.processCommand("copy event Event1 on 2025-04-01T09:00 --target TargetCal to 2025-04-02T10:00");
    } catch (InvalidCommandException e) {
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyToNonexistentTargetCalendarThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Review from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy event Review on 2025-04-01T09:00 --target TargetCal to 2025-04-02T09:00");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyFromNonexistentSourceCalendarThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name TargetCal --timezone America/New_York");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Review from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy event Review on 2025-04-01T09:00 --target TargetCal to 2025-04-02T09:00");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventInvalidDateFormat() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone Europe/London");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Demo from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy event Demo on 01-04-2025T09:00 --target TargetCal to 02-04-2025T10:00");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid date formats", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testCopyEventSameStartTimeToDifferentCalendar() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone America/New_York");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Yoga from 2025-04-01T07:00 to 2025-04-01T08:00");

    controller.processCommand("copy event Yoga on 2025-04-01T07:00 --target TargetCal to 2025-04-01T07:00");

    controller.processCommand("use calendar --name TargetCal");
    Assert.assertEquals("[[Yoga, 2025-04-01T07:00, 2025-04-01T08:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());
  }

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
            "[[Breakfast, 2025-04-02T13:00, 2025-04-02T14:00, ], [Standup, 2025-04-02T15:00, 2025-04-02T15:15, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).toString()
    );
  }

  @Test
  public void testCopyEventsOnDateWithNoEvents() {
    controller.processCommand("create calendar --name SourceCal1 --timezone UTC");
    controller.processCommand("create calendar --name TargetCal1 --timezone UTC");
    controller.processCommand("use calendar --name SourceCal1");

    controller.processCommand("copy events on 2025-04-01 --target TargetCal1 to 2025-04-02");

    controller.processCommand("use calendar --name TargetCal1");
    Assert.assertTrue(controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).isEmpty());
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventsToNonexistentTargetCalendarThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone UTC");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Work from 2025-04-01T09:00 to 2025-04-01T17:00");

    try {
      controller.processCommand("copy events on 2025-04-01 --target TargetCal to 2025-04-02");
      Assert.fail("Expected InvalidCommandException was not thrown");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventsWithInvalidDateFormatThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone UTC");
    controller.processCommand("create calendar --name TargetCal --timezone UTC");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Call from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy events on 01-04-2025 --target TargetCal to 2025/04/02");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid date formats", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventsFromNonexistentSourceCalendarThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name TargetCal --timezone UTC");

    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("copy events on 2025-04-01 --target TargetCal to 2025-04-02");
  }

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

  @Test
  public void testCopyEventsToSameCalendarOnNewDate() {
    controller.processCommand("create calendar --name SelfCal --timezone UTC");
    controller.processCommand("use calendar --name SelfCal");
    controller.processCommand("create event Repeat from 2025-04-01T09:00 to 2025-04-01T10:00");

    controller.processCommand("copy events on 2025-04-01 --target SelfCal to 2025-04-02");

    Assert.assertEquals("[[Repeat, 2025-04-02T09:00, 2025-04-02T10:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).toString());
  }

  @Test
  public void testCopyMultipleDaysOfEventsToTargetCalendar() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone US/Pacific");
    controller.processCommand("use calendar --name SourceCal");

    controller.processCommand("create event A from 2025-04-01T09:00 to 2025-04-01T10:00");
    controller.processCommand("create event B from 2025-04-02T10:00 to 2025-04-02T11:00");
    controller.processCommand("create event C from 2025-04-03T11:00 to 2025-04-03T12:00");

    controller.processCommand("copy events between 2025-04-01 and 2025-04-04 --target TargetCal to 2025-04-10");

    controller.processCommand("use calendar --name TargetCal");

    Assert.assertEquals("[[A, 2025-04-10T06:00, 2025-04-10T07:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 10)).toString());
    Assert.assertEquals("[[B, 2025-04-11T07:00, 2025-04-11T08:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 11)).toString());
    Assert.assertEquals("[[C, 2025-04-12T08:00, 2025-04-12T09:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 12)).toString());
  }

  @Test
  public void testCopyIntervalWithNoEventsCopiesNothing() {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone Asia/Tokyo");
    controller.processCommand("use calendar --name SourceCal");

    controller.processCommand("copy events between 2025-04-01 and 2025-04-03 --target TargetCal to 2025-04-10");

    controller.processCommand("use calendar --name TargetCal");
    Assert.assertTrue(controller.model.getEventsOn(LocalDate.of(2025, 4, 10)).isEmpty());
    Assert.assertTrue(controller.model.getEventsOn(LocalDate.of(2025, 4, 11)).isEmpty());
    Assert.assertTrue(controller.model.getEventsOn(LocalDate.of(2025, 4, 12)).isEmpty());
  }

  @Test(expected = InvalidCommandException.class)
  public void testCopyEventsBetweenWithInvalidDateFormatThrowsError() throws InvalidCommandException {
    controller.processCommand("create calendar --name SourceCal --timezone America/New_York");
    controller.processCommand("create calendar --name TargetCal --timezone Asia/Tokyo");
    controller.processCommand("use calendar --name SourceCal");

    controller.processCommand("copy events between 04-01-2025 and 2025/04/03 --target TargetCal to 2025-04-10");
  }

  @Test
  public void testCopyEventsToSameCalendar() {
    controller.processCommand("create calendar --name MyCal --timezone America/New_York");
    controller.processCommand("use calendar --name MyCal");

    controller.processCommand("create event Meeting from 2025-04-01T08:00 to 2025-04-01T09:00");
    controller.processCommand("create event Meeting2 from 2025-04-02T08:00 to 2025-04-02T09:00");

    controller.processCommand("copy events between 2025-04-01 and 2025-04-03 --target MyCal to 2025-04-10");

    Assert.assertEquals("[[Meeting, 2025-04-10T08:00, 2025-04-10T09:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 10)).toString());
    Assert.assertEquals("[[Meeting2, 2025-04-11T08:00, 2025-04-11T09:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 11)).toString());
  }

  @Test
  public void testCopyEventsBetweenToNonexistentTargetCalendarThrowsError() {
    controller.processCommand("create calendar --name SourceCal --timezone UTC");
    controller.processCommand("use calendar --name SourceCal");
    controller.processCommand("create event Task from 2025-04-01T09:00 to 2025-04-01T10:00");

    try {
      controller.processCommand("copy events between 2025-04-01 and 2025-04-02 --target TargetCal to 2025-04-10");
      Assert.fail("Expected InvalidCommandException was not thrown");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
    }
  }
}
