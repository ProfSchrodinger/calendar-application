package controller;

import exception.InvalidCommandException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Tests for editing events.
 */

public class CalendarControllerEditTest {
  private CalendarController controller;

  /**
   * Initialize the calendar controller.
   */

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  /**
   * Valid edit command across all same eventNames (for subject)
   */

  @Test
  public void testValidEditCommand1() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events subject MeetingOne MeetingTwo");
    Assert.assertEquals("[[MeetingTwo, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingTwo, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingTwo, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid edit command across all same eventNames (for startDateTime)
   */

  @Test
  public void testValidEditCommand2() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events startDateTime MeetingOne 2025-03-12T00:30");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:30, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid edit command across all same eventNames (for endDateTime)
   */

  @Test
  public void testValidEditCommand3() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events endDateTime MeetingOne 2025-03-12T00:30");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T00:30, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid edit command across all same eventNames (for location)
   */

  @Test
  public void testValidEditCommand4() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events location MeetingOne NEU");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, NEU], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, NEU], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, NEU]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid edit command across all same eventNames (for isPublic)
   */

  @Test
  public void testValidEditCommand5() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events isPublic MeetingOne true");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid edit command across all same eventNames (for description)
   */

  @Test
  public void testValidEditCommand6() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events description MeetingOne \"Changed description\"");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid edit command across events with specific start date times same eventNames (for subject)
   * Became invalid as overlapping not allowed in Assignment 5.
   */

//  @Test
//  public void testValidEditCommand7() {
//    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
//    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T02:00");
//    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
//                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
//                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
//                    "[MeetingOne, 2025-03-12T00:00, 2025-03-12T02:00, ]]",
//            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
//                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
//    controller.processCommand("edit events subject MeetingOne from 2025-03-12T00:00 with MeetingTwo");
//    Assert.assertEquals("[[MeetingTwo, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
//                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
//                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
//                    "[MeetingTwo, 2025-03-12T00:00, 2025-03-12T02:00, ]]",
//            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
//                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
//  }

  /**
   * Valid edit command across events with specific start and end date times same eventNames (for subject)
   * Became invalid as overlapping not allowed in Assignment 5.
   */

//  @Test
//  public void testValidEditCommand8() {
//    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
//    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
//    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T02:00");
//    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
//                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
//                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
//                    "[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
//                    "[MeetingOne, 2025-03-12T00:00, 2025-03-12T02:00, ]]",
//            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
//                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
//    controller.processCommand("edit events subject MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 with MeetingTwo");
//    Assert.assertEquals("[[MeetingTwo, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
//                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
//                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ], " +
//                    "[MeetingTwo, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
//                    "[MeetingOne, 2025-03-12T00:00, 2025-03-12T02:00, ]]",
//            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
//                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
//  }

  /**
   * Valid edit command across events with specific start date times same eventNames (for subject)
   */

  @Test
  public void testValidEditCommand9() {
    controller.processCommand("create event --autoDecline MeetingOne from 2025-03-12T00:00 to 2025-03-12T02:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events subject MeetingOne from 2025-03-12T00:00 with MeetingTwo");
    Assert.assertEquals("[[MeetingTwo, 2025-03-12T00:00, 2025-03-12T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Valid edit command across all events same eventNames (for subject)
   */

  @Test
  public void testValidEditCommand10() {
    controller.processCommand("create event --autoDecline MeetingOne from 2025-03-12T00:00 to 2025-03-12T02:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events subject MeetingOne MeetingTwo");
    Assert.assertEquals("[[MeetingTwo, 2025-03-12T00:00, 2025-03-12T02:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid edit command
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidEditCommand1() {
    try {
      controller.processCommand("edit events blah");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid edit property
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidEditCommand2() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00");
      controller.processCommand("edit events name MeetingOne MeetingTwo");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid edit startDateTime beyond endDateTime (no change)
   */

  @Test
  public void testInvalidEditCommand3() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events startDateTime MeetingOne 2025-03-12T02:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid edit startDateTime before the day for Recurring (no change)
   */

  @Test
  public void testInvalidEditCommand4() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events startDateTime MeetingOne 2025-03-11T23:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid edit endDateTime before startDateTime (no change)
   */

  @Test
  public void testInvalidEditCommand5() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events endDateTime MeetingOne 2025-03-11T23:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid edit endDateTime after the day for Recurring (no change)
   */

  @Test
  public void testInvalidEditCommand6() {
    controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
    controller.processCommand("edit events endDateTime MeetingOne 2025-03-13T01:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T00:00, 2025-03-12T01:00, ], " +
                    "[MeetingOne, 2025-03-14T00:00, 2025-03-14T01:00, ], " +
                    "[MeetingOne, 2025-03-17T00:00, 2025-03-17T01:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 12, 00, 00)
                    , LocalDateTime.of(2025, 3, 20, 00, 00)).toString());
  }

  /**
   * Invalid edit command for events of same start Date Time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidEditCommand7() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
      controller.processCommand("edit events subject MeetingOne from 2025-03-12 with MeetingTwo");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid edit command for events of specific start and end Date Time
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidEditCommand8() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
      controller.processCommand("edit events subject MeetingOne from 2025-03-12T00:00 to 2025-03-12T01 with MeetingTwo");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid edit command - wrong format for StartDateTime
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidEditCommand9() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
      controller.processCommand("edit events startDateTime MeetingOne MeetingTwo");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid edit command - wrong format for endDateTime
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidEditCommand10() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
      controller.processCommand("edit events endDateTime MeetingOne MeetingTwo");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }

  /**
   * Invalid edit command - wrong format for isPublic
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidEditCommand11() {
    try {
      controller.processCommand("create event MeetingOne from 2025-03-12T00:00 to 2025-03-12T01:00 repeats MFW until 2025-03-18T00:00");
      controller.processCommand("edit events isPublic MeetingOne MeetingTwo");
    } catch (Exception e) {
      Assert.assertEquals("Invalid datetime or property", e.getMessage());
      throw e;
    }
  }
}
