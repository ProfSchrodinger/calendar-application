package controller;

import exception.EventConflictException;
import exception.InvalidCommandException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CalendarControllerCreateTest {
  private CalendarController controller;

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  /**
   * Test Random command
   */

  @Test
  public void testInvalidRandomCommand() {
    try {
      controller.processCommand("random command");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid command", e.getMessage());
    }
  }

  /**
   * No subject and start and end time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand1() {
    try {
      controller.processCommand("create event");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * No subject and start date and time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand2() throws InvalidCommandException {
    try {
      controller.processCommand("create event --autoDecline");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * No start date and time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand3() {
    try {
      controller.processCommand("create event --autoDecline MeetingOne");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * No start date and time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand4() {
    try {
      controller.processCommand("create event MeetingOne");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid start date and time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand5() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * No end date and time as from is present
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand6() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * No end date and time as from is present
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand7() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid end date and time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand8() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid end date and time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand9() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 01:00");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid create command using from
   */

  @Test
  public void testValidSingleCreateCommand1() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 12)).toString());
  }

  /**
   * Valid create command using from and autoDecline
   */

  @Test
  public void testValidSingleCreateCommand2() {
    controller.processCommand("create event --autoDecline MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 12)).toString());
  }

  /**
   * Valid create command using from and spaced subject name
   */

  @Test
  public void testValidSingleCreateCommand3() {
    controller.processCommand("create event \"Meeting One\" from 2025-03-12T00:00 to 2025-03-12T01:00");
    Assert.assertEquals("[[Meeting One, 2025-03-12T00:00, 2025-03-12T01:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 12)).toString());
  }

  /**
   * Valid create command using from, multiple days
   */

  @Test
  public void testValidSingleCreateCommand4() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-14T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-14T00:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 12)).toString());
  }

  /**
   * Invalid create command using on
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand10() {
    try {
      controller.processCommand("create event MeetingOne on");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid start date time create command using on
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand11() {
    try {
      controller.processCommand("create event MeetingOne on 2025-03-12");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid start date time create command using on
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand12() {
    try {
      controller.processCommand("create event MeetingOne on 00:00");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid logic on start and end date time.
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand13() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-10T00:00");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid logic on start and end date time.
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidSingleCreateCommand14() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T00:00");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid create command using on
   */

  @Test
  public void testValidSingleCreateCommand5() {
    controller.processCommand("create event MeetingOne on 2025-03-12T12:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T12:00, 2025-03-13T00:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 12)).toString());
  }

  /**
   * Valid create command using on and autoDecline
   */

  @Test
  public void testValidSingleCreateCommand6() {
    controller.processCommand("create event --autoDecline MeetingOne on 2025-03-12T12:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T12:00, 2025-03-13T00:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 12)).toString());
  }

  /**
   * Valid Recurring event create command using N times
   */

  @Test
  public void testValidRecurringCreateCommand1() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MWF for 3 times");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid Recurring event create command using N times
   */

  @Test
  public void testValidRecurringCreateCommand2() {
    controller.processCommand("create event --autoDecline MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MWF for 3 times");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid Recurring event create command using N times on different start date
   */

  @Test
  public void testValidRecurringCreateCommand3() {
    controller.processCommand("create event MeetingOne from 2025-03-13T00:00 to 2025-03-13T01:00 repeats MWF for 3 times");
    Assert.assertEquals("[[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
                    "[MeetingOne, 2025-03-19T00:00, 2025-03-19T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid logic on start and end date time.
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand1() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T01:00 to 2025-03-12T00:00 repeats MWF for 3 times");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid end date time.
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand2() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12 repeats MWF for 3 times");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid weekdays
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand3() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats ZA for 3 times");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid weekdays
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand4() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats for 3 times");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid 0 repeats
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand5() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW for 0 times");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid negative repeats
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand6() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW for -3 times");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid Recurring event create command using until
   */

  @Test
  public void testValidRecurringCreateCommand4() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MWF until 2025-03-20T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
                    "[MeetingOne, 2025-03-19T00:00, 2025-03-19T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid Recurring event create command using until but strict until time
   */

  @Test
  public void testValidRecurringCreateCommand5() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MWF until 2025-03-19T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid Recurring event create command using until and --autoDecline
   */

  @Test
  public void testValidRecurringCreateCommand6() {
    controller.processCommand("create event --autoDecline MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MWF until 2025-03-20T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
                    "[MeetingOne, 2025-03-19T00:00, 2025-03-19T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid until date
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand7() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-12T00:00");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid until date 2
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand8() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-12T00:30");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid Recurring full day event create command using N times
   */

  @Test
  public void testValidRecurringCreateCommand7() {
    controller.processCommand("create event MeetingOne on 2025-03-12 repeats MWF for 3 times");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-13T00:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-15T00:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-18T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid Recurring full day event create command using N times, used DateTime instead of Date
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand9() {
    try {
      controller.processCommand("create event MeetingOne on 2025-03-12T00:00 repeats MWF for 3 times");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid Recurring full day event create command using until
   */

  @Test
  public void testValidRecurringCreateCommand8() {
    controller.processCommand("create event MeetingOne on 2025-03-12 repeats MWF until 2025-03-20");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-13T00:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-15T00:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-18T00:00, ], " +
                    "[MeetingOne, 2025-03-19T00:00, 2025-03-20T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid Recurring full day event create command using until, used DateTime instead of Date
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand10() {
    try {
      controller.processCommand("create event MeetingOne on 2025-03-12T00:00 repeats MWF until 2025-03-20");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid Recurring full day event create command using until, used DateTime instead of Date
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand11() {
    try {
      controller.processCommand("create event MeetingOne on 2025-03-12 repeats MWF until 2025-03-20T00:00");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid Recurring full day event create command using until, used DateTime instead of Date
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidRecurringCreateCommand12() {
    try {
      controller.processCommand("create event MeetingOne on 2025-03-12 repeats MWF until 2025-03-11");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid 2 Single events
   */

  @Test
  public void testValidSingleSingleCommand1() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
    controller.processCommand("create event MeetingTwo from 2025-03-12T01:00 to 2025-03-12T02:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingTwo, 2025-03-12T01:00, 2025-03-12T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid 2 Single events overlapping
   */

  @Test
  public void testValidSingleSingleCommand2() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
    controller.processCommand("create event MeetingTwo from 2025-03-12T00:00 to 2025-03-12T01:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingTwo, 2025-03-12T00:00, 2025-03-12T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid 2 Single events overlapping (different commands)
   */

  @Test
  public void testValidSingleSingleCommand3() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
    controller.processCommand("create event MeetingTwo on 2025-03-12T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingTwo, 2025-03-12T00:00, 2025-03-13T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid 2 Single events overlapping (same commands) MeetingOne < MeetingTwo
   */

  @Test
  public void testValidSingleSingleCommand4() {
    controller.processCommand("create event --autoDecline MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
    controller.processCommand("create event --autoDecline MeetingTwo from 2025-03-11T23:00 to 2025-03-12T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingTwo, 2025-03-11T23:00, 2025-03-12T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 11, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid 2 Single events overlapping (same commands) MeetingOne > MeetingTwo
   */

  @Test
  public void testValidSingleSingleCommand5() {
    controller.processCommand("create event --autoDecline MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
    controller.processCommand("create event --autoDecline MeetingTwo from 2025-03-12T01:00 to 2025-03-12T02:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingTwo, 2025-03-12T01:00, 2025-03-12T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 11, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid 2 Single events overlapping declined due to autoDecline
   */

  @Test (expected = EventConflictException.class)
  public void testInvalidSingleSingleCommand1() {
    try {
      controller.processCommand("create event --autoDecline MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
      controller.processCommand("create event --autoDecline MeetingTwo from 2025-03-12T00:00 to 2025-03-12T01:00");
    } catch (Exception e) {
      Assert.assertEquals("Event Conflict Occurred", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid 2 Single events overlapping declined due to autoDecline (different create)
   */

  @Test (expected = EventConflictException.class)
  public void testInvalidSingleSingleCommand2() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
      controller.processCommand("create event --autoDecline MeetingTwo on 2025-03-12T00:00");
    } catch (Exception e) {
      Assert.assertEquals("Event Conflict Occurred", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid Single event and recurring event no overlapping
   */

  @Test
  public void testValidSingleRecurringCommand1() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
    controller.processCommand("create event MeetingTwo on 2025-03-13 repeats MFW until 2025-03-19");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingTwo, 2025-03-14T00:00, 2025-03-15T00:00, ], " +
                    "[MeetingTwo, 2025-03-17T00:00, 2025-03-18T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid Single event and recurring event due to overlapping
   */

  @Test (expected = EventConflictException.class)
  public void testInvalidSingleRecurringCommand1() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
      controller.processCommand("create event MeetingTwo on 2025-03-12 repeats MFW until 2025-03-19");
    } catch (Exception e) {
      Assert.assertEquals("Event Conflict Occurred", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid 2 recurring events no overlapping
   */

  @Test
  public void testValidRecurringRecurringCommand1() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    controller.processCommand("create event MeetingTwo from 2025-03-12T01:00 to 2025-03-12T02:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
                    "[MeetingTwo, 2025-03-12T01:00, 2025-03-12T02:00, ], " +
                    "[MeetingTwo, 2025-03-14T01:00, 2025-03-14T02:00, ], " +
                    "[MeetingTwo, 2025-03-17T01:00, 2025-03-17T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid 2 recurring events (timed and all day long) no overlapping
   */

  @Test
  public void testValidRecurringRecurringCommand2() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    controller.processCommand("create event MeetingTwo on 2025-03-13 repeats TR until 2025-03-19");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
                    "[MeetingTwo, 2025-03-13T00:00, 2025-03-14T00:00, ], " +
                    "[MeetingTwo, 2025-03-18T00:00, 2025-03-19T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid 2 recurring events due to overlapping
   */

  @Test (expected = EventConflictException.class)
  public void testInvalidRecurringRecurringCommand1() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
      controller.processCommand("create event MeetingTwo on 2025-03-13 repeats FM until 2025-03-19");
    } catch (Exception e) {
      Assert.assertEquals("Event Conflict Occurred", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid 2 recurring events due to overlapping even if 1 event clashed
   */

  @Test (expected = EventConflictException.class)
  public void testInvalidRecurringRecurringCommand2() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
      controller.processCommand("create event MeetingTwo from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-12T06:00");
    } catch (Exception e) {
      Assert.assertEquals("Event Conflict Occurred", e.getMessage());
      throw e;
    }
  }

  /**
   * Valid Recurring event followed by Single event with autoDecline
   */

  @Test
  public void testValidRecurringSingleCommand1() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    controller.processCommand("create event --autoDecline MeetingTwo from 2025-03-12T01:00 to 2025-03-12T02:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
                    "[MeetingTwo, 2025-03-12T01:00, 2025-03-12T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid Recurring event followed by Single event collision without autoDecline
   */

  @Test
  public void testValidRecurringSingleCommand2() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    controller.processCommand("create event MeetingTwo from 2025-03-12T00:00 to 2025-03-12T01:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
                    "[MeetingTwo, 2025-03-12T00:00, 2025-03-12T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 0, 0)
                    , LocalDateTime.of(2025, 3, 20, 0, 0)).toString());
  }

  /**
   * Invalid Recurring event followed by Single event collision with autoDecline
   */

  @Test (expected = EventConflictException.class)
  public void testInvalidRecurringSingleCommand1() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
      controller.processCommand("create event --autoDecline MeetingTwo from 2025-03-12T00:00 to 2025-03-12T01:00");
    } catch (Exception e) {
      Assert.assertEquals("Event Conflict Occurred", e.getMessage());
      throw e;
    }
  }
}
