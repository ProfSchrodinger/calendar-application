package controller;

import exception.InvalidCommandException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests for creating, editing and using calendar.
 */

public class CalendarControllerCreateEditUseCalendarTest {
  private CalendarController controller;

  /**
   * Initialize the calendar controller.
   */

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  /**
   * Tests successful creation and usage of a calendar with a valid timezone.
   */

  @Test
  public void testCreateCalendarValid() {
    controller.processCommand("create calendar --name PersonalCal --timezone America/New_York");
    controller.processCommand("use calendar --name PersonalCal");
    controller.processCommand("create event Event on 2025-05-01T10:00");
    Assert.assertEquals("[[Event, 2025-05-01T10:00, 2025-05-02T00:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 5, 1)).toString());

  }

  /**
   * Tests creating multiple calendars with different names and timezones.
   */

  @Test
  public void testCreateMultipleCalendars() {
    controller.processCommand("create calendar --name HomeCal --timezone America/Chicago");
    controller.processCommand("create calendar --name HolidayCal --timezone Europe/London");

    controller.processCommand("use calendar --name HomeCal");
    controller.processCommand("create event Cleaning from 2025-04-01T10:00 to 2025-04-01T11:00");
    Assert.assertEquals("[[Cleaning, 2025-04-01T10:00, 2025-04-01T11:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());


    controller.processCommand("use calendar --name HolidayCal");
    controller.processCommand("create event Beach from 2025-04-01T12:00 to 2025-04-01T13:00");
    Assert.assertEquals("[[Beach, 2025-04-01T12:00, 2025-04-01T13:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());

  }

  /**
   * Tests that creating a calendar with a duplicate name throws an exception.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarSameName() {
    try {
      controller.processCommand("create calendar --name HomeCal --timezone Asia/Kolkata");
      controller.processCommand("create calendar --name HomeCal --timezone Asia/Tokyo");
      Assert.fail("Expected InvalidCommandException to be thrown for duplicate calendar name");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar already exists with same name.", e.getMessage());
      throw e;
    }
  }

  /**
   * Test creating a calendar with an invalid timezone.
   * @throws InvalidCommandException if the timezone is invalid.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarInvalidTimezone() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name InvalidCal --timezone Earth/Tokyo");
      Assert.fail("Expected InvalidCommandException was not thrown");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid Zone ID.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests using a calendar after it has been created.
   */

  @Test
  public void testUseCalendarValid() {
    controller.processCommand("create calendar --name SchoolCal --timezone America/New_York");
    controller.processCommand("use calendar --name SchoolCal");
    controller.processCommand("create event Event on 2025-05-01T10:00");
    Assert.assertEquals("[[Event, 2025-05-01T10:00, 2025-05-02T00:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 5, 1)).toString());
  }

  /**
   * Test using a non-existent calendar.
   * @throws InvalidCommandException if the calendar doesn't exist.
   */

  @Test(expected = InvalidCommandException.class)
  public void testUseCalendarNonexistent() throws InvalidCommandException {
    try {
      controller.processCommand("use calendar --name UnknownCal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests that an event can be created after switching to a valid calendar.
   */

  @Test
  public void testCreateEventAfterUsingCalendar() {
    controller.processCommand("create calendar --name PersonalCal --timezone America/New_York");
    controller.processCommand("use calendar --name PersonalCal");

    controller.processCommand("create event MeetingOne from 2025-03-12T10:00 to 2025-03-12T11:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T10:00, 2025-03-12T11:00, ]]",
            controller.model.getEventsOn(java.time.LocalDate.of(2025, 3, 12)).toString());
  }

  /**
   * Tests renaming a calendar successfully.
   */

  @Test
  public void testEditCalendarNameSuccess() {
    controller.processCommand("create calendar --name WorkCal --timezone America/New_York");
    controller.processCommand("edit calendar --name WorkCal --property name OfficeCal");
    controller.processCommand("use calendar --name OfficeCal");

    controller.processCommand("use calendar --name OfficeCal");
    controller.processCommand("create event TeamMeeting from 2025-04-01T09:00 to 2025-04-01T10:00");

    Assert.assertEquals("[[TeamMeeting, 2025-04-01T09:00, 2025-04-01T10:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());
  }

  /**
   * Tests editing a calendar's timezone and confirms events are time-shifted accordingly.
   */

  @Test
  public void testEditCalendarTimezoneSuccess() {
    controller.processCommand("create calendar --name WorkCal --timezone America/New_York");
    controller.processCommand("use calendar --name WorkCal");
    controller.processCommand("create event Meeting1 on 2025-03-27T11:00");
    Assert.assertEquals("[[Meeting1, 2025-03-27T11:00, 2025-03-28T00:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 27)).toString());
    controller.processCommand("edit calendar --name WorkCal --property timezone US/Pacific");
    Assert.assertEquals("[[Meeting1, 2025-03-27T08:00, 2025-03-27T21:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 3, 27)).toString());
  }

  /**
   * Tests editing a calendar's timezone with recurring events and verifies time shift.
   */

  @Test
  public void testEditCalendarTimezoneSuccessRecurring() {
    controller.processCommand("create calendar --name WorkCal --timezone America/New_York");
    controller.processCommand("use calendar --name WorkCal");
    controller.processCommand("create event Meeting1 on 2025-03-27 repeats RFS for 3 times");
    Assert.assertEquals("[[Meeting1, 2025-03-27T00:00, 2025-03-28T00:00, ], "
                    + "[Meeting1, 2025-03-28T00:00, 2025-03-29T00:00, ], "
                    + "[Meeting1, 2025-03-29T00:00, 2025-03-30T00:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 27, 0, 0),
                    LocalDateTime.of(2025, 3, 31, 0, 0)).toString());
    controller.processCommand("edit calendar --name WorkCal --property timezone US/Pacific");
    Assert.assertEquals("[[Meeting1, 2025-03-26T21:00, 2025-03-27T21:00, ], "
                    + "[Meeting1, 2025-03-27T21:00, 2025-03-28T21:00, ], "
                    + "[Meeting1, 2025-03-28T21:00, 2025-03-29T21:00, ]]",
            controller.model.getEventsBetween(LocalDateTime.of(2025, 3, 26, 0, 0),
                    LocalDateTime.of(2025, 3, 31, 0, 0)).toString());
  }

  /**
   * Tests to edit a non-existent calendar.
   * @throws InvalidCommandException if the calendar doesn't exist.
   */

  @Test(expected = InvalidCommandException.class)
  public void testEditNonexistentCalendar() throws InvalidCommandException {
    try {
      controller.processCommand("edit calendar --name UnknownCal --property name NewCal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  /**
   * Test to rename a calendar to an existing calendar name.
   * @throws InvalidCommandException if the new name already exists.
   */

  @Test(expected = InvalidCommandException.class)
  public void testRenameToExistingCalendarName() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name CalOne --timezone Europe/Paris");
      controller.processCommand("create calendar --name CalTwo --timezone Asia/Tokyo");
      controller.processCommand("edit calendar --name CalTwo --property name CalOne");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name already exists.", e.getMessage());
      throw e;
    }
  }

  /**
   * Test with an invalid timezone during edit.
   */

  @Test(expected = InvalidCommandException.class)
  public void testEditCalendarInvalidTimezone() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name ZoneTest --timezone America/Chicago");
      controller.processCommand("edit calendar --name ZoneTest --property timezone Pluto/Nowhere");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid ZoneID.", e.getMessage());
      throw e;
    }
  }

  /**
   * Test editing an unsupported calendar property.
   * @throws InvalidCommandException if the property is not supported.
   */

  @Test(expected = InvalidCommandException.class)
  public void testEditCalendarUnsupportedProperty() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name PropTest --timezone Europe/Berlin");
      controller.processCommand("edit calendar --name PropTest --property color Blue");
    } catch (InvalidCommandException e) {
      throw e;
    }
  }

  /**
   * Tests that a previously used calendar name can be reused after renaming.
   */

  @Test
  public void testReuseOldNameAfterRename() {
    controller.processCommand("create calendar --name Cal1 --timezone Asia/Kolkata");
    controller.processCommand("edit calendar --name Cal1 --property name Cal2");
    controller.processCommand("create calendar --name Cal1 --timezone Europe/London");

    controller.processCommand("use calendar --name Cal2");
    controller.processCommand("create event OldCalEvent from 2025-04-01T09:00 to 2025-04-01T10:00");
    Assert.assertEquals("[[OldCalEvent, 2025-04-01T09:00, 2025-04-01T10:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());

    controller.processCommand("use calendar --name Cal1");
    controller.processCommand("create event NewCalEvent from "
            + "2025-04-02T11:00 to 2025-04-02T12:00");
    Assert.assertEquals("[[NewCalEvent, 2025-04-02T11:00, 2025-04-02T12:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 2)).toString());
  }

  /**
   * Tests that missing edit parameters throw an appropriate exception.
   * @throws InvalidCommandException if command format is invalid.
   */

  @Test(expected = InvalidCommandException.class)
  public void testEditCalendarWithoutProperty() throws InvalidCommandException {
    try {
      controller.processCommand("edit calendar --name MyCal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid edit calendar command format.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests that calendar name matching is case-sensitive.
   * @throws InvalidCommandException if name case doesn't match.
   */

  @Test(expected = InvalidCommandException.class)
  public void testUseCalendarCaseMismatch() throws InvalidCommandException {
    controller.processCommand("create calendar --name MyCal --timezone America/New_York");
    try {
      controller.processCommand("use calendar --name mycal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests that extra arguments in calendar creation command are rejected.
   * @throws InvalidCommandException if command format is invalid.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarWithExtraCommand() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name Extra --timezone Asia/Kolkata blahblah");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid create calendar command format.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests that calendar creation with wrong argument order is rejected.
   * @throws InvalidCommandException if command format is invalid.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarInvalidOrder() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --timezone Asia/Kolkata --name WrongOrder");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid create calendar command format.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests missing required parameters while creating.
   * @throws InvalidCommandException if required arguments are missing.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarMissingCommandParameter() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name OnlyName");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests that creating a calendar with only timezone fails.
   * @throws InvalidCommandException if name is missing.
   */

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarWithTimezoneOnly() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --timezone Asia/Kolkata");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid create calendar command format.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests that leaving out a value in edit command throws an exception.
   * @throws InvalidCommandException if a value for the property is missing.
   */

  @Test(expected = InvalidCommandException.class)
  public void testEditCalendarMissingValue() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name TestEdit --timezone Asia/Kolkata");
      controller.processCommand("edit calendar --name TestEdit --property name");
    } catch (InvalidCommandException e) {
      throw e;
    }
  }

  /**
   * Tests that renaming a calendar back to its original name is allowed.
   */

  @Test
  public void testCalendarRenameRevertToOriginalName() {
    controller.processCommand("create calendar --name Calen --timezone America/New_York");
    controller.processCommand("edit calendar --name Calen --property name Calendar");
    controller.processCommand("edit calendar --name Calendar --property name Calen");

    controller.processCommand("use calendar --name Calen");
    controller.processCommand("create event RenameBackEvent from "
            + "2025-04-01T09:00 to 2025-04-01T10:00");

    Assert.assertEquals("[[RenameBackEvent, 2025-04-01T09:00, 2025-04-01T10:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());
  }

  /**
   * Test omitting name in the use calendar command.
   * @throws InvalidCommandException if command format is invalid.
   */

  @Test(expected = InvalidCommandException.class)
  public void testUseCalendarMissingNameParam() throws InvalidCommandException {
    try {
      controller.processCommand("use calendar SchoolCal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid use calendar command format.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests renaming a calendar to a name with special characters.
   */

  @Test
  public void testEditCalendarNameToSpecialCharacters() {
    controller.processCommand("create calendar --name OriginalName --timezone America/New_York");
    controller.processCommand("edit calendar --name OriginalName --property name @New123_");
    controller.processCommand("use calendar --name @New123_");

    controller.processCommand("create event SpecialEvent from 2025-04-01T10:00 "
            + "to 2025-04-01T11:00");

    Assert.assertEquals("[[SpecialEvent, 2025-04-01T10:00, 2025-04-01T11:00, ]]",
            controller.model.getEventsOn(LocalDate.of(2025, 4, 1)).toString());
  }

  /**
   * Tests that using an old calendar name after it’s been renamed throws an exception.
   * @throws InvalidCommandException if the calendar name no longer exists.
   */

  @Test(expected = InvalidCommandException.class)
  public void testOldCalendarNameNoLongerUsableAfterRename() throws InvalidCommandException {
    controller.processCommand("create calendar --name RenameTest --timezone Europe/Paris");
    controller.processCommand("edit calendar --name RenameTest --property name Blah");

    try {
      controller.processCommand("use calendar --name RenameTest");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  /**
   * Tests switching between calendars multiple times and creating events in each.
   */

  @Test
  public void testSwitchingCalendarsMultipleTimes() {
    controller.processCommand("create calendar --name WorkCal --timezone America/Chicago");
    controller.processCommand("create calendar --name PersonalCal --timezone Europe/London");

    controller.processCommand("use calendar --name WorkCal");
    controller.processCommand("create event WorkMeeting from 2025-03-10T10:00 to 2025-03-10T11:00");

    controller.processCommand("use calendar --name PersonalCal");
    controller.processCommand("create event PersonalEvent from 2025-03-10T12:00 to "
            + "2025-03-10T13:00");

    controller.processCommand("use calendar --name WorkCal");
    Assert.assertEquals("[[WorkMeeting, 2025-03-10T10:00, 2025-03-10T11:00, ]]",
            controller.model.getEventsOn(java.time.LocalDate.of(2025, 3, 10)).toString());
  }
}
