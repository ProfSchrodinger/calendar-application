package controller;

import exception.InvalidCommandException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CalendarControllerCreateEditUseCalendarTest {
  private CalendarController controller;

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  @Test
  public void testCreateCalendarValid() {
    controller.processCommand("create calendar --name PersonalCal --timezone America/New_York");
    controller.processCommand("use calendar --name PersonalCal");
  }

  @Test
  public void testCreateMultipleCalendars() {
    controller.processCommand("create calendar --name HomeCal --timezone America/Chicago");
    controller.processCommand("create calendar --name HolidayCal --timezone Europe/London");
  }

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

  @Test
  public void testUseCalendarValid() {
    controller.processCommand("create calendar --name SchoolCal --timezone America/New_York");
    controller.processCommand("use calendar --name SchoolCal");
  }

  @Test(expected = InvalidCommandException.class)
  public void testUseCalendarNonexistent() throws InvalidCommandException {
    try {
      controller.processCommand("use calendar --name UnknownCal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testCreateEventAfterUsingCalendar() {
    controller.processCommand("create calendar --name PersonalCal --timezone America/New_York");
    controller.processCommand("use calendar --name PersonalCal");

    controller.processCommand("create event MeetingOne from 2025-03-12T10:00 to 2025-03-12T11:00");
    Assert.assertEquals("[[MeetingOne, 2025-03-12T10:00, 2025-03-12T11:00, ]]",
            controller.model.getEventsOn(java.time.LocalDate.of(2025, 3, 12)).toString());
  }

  @Test
  public void testEditCalendarNameSuccess() {
    controller.processCommand("create calendar --name WorkCal --timezone America/New_York");
    controller.processCommand("edit calendar --name WorkCal --property name OfficeCal");
    controller.processCommand("use calendar --name OfficeCal");
  }

  @Test
  public void testEditCalendarTimezoneSuccess() {
    controller.processCommand("create calendar --name WorkCal --timezone America/New_York");
    controller.processCommand("edit calendar --name WorkCal --property timezone Africa/Cairo");
      controller.processCommand("use calendar --name WorkCal");
  }

  @Test(expected = InvalidCommandException.class)
  public void testEditNonexistentCalendar() throws InvalidCommandException {
    try {
      controller.processCommand("edit calendar --name UnknownCal --property name NewCal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Calendar with the given name does not exist.", e.getMessage());
      throw e;
    }
  }

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

  @Test(expected = InvalidCommandException.class)
  public void testEditCalendarUnsupportedProperty() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name PropTest --timezone Europe/Berlin");
      controller.processCommand("edit calendar --name PropTest --property color Blue");
    } catch (InvalidCommandException e) {
      throw e;
    }
  }

  @Test
  public void testReuseOldNameAfterRename() {
    controller.processCommand("create calendar --name Cal1 --timezone Asia/Kolkata");
    controller.processCommand("edit calendar --name Cal1 --property name Cal2");
    controller.processCommand("create calendar --name Cal1 --timezone Europe/London");
  }

  @Test(expected = InvalidCommandException.class)
  public void testEditCalendarWithoutProperty() throws InvalidCommandException {
    try {
      controller.processCommand("edit calendar --name MyCal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid edit calendar command format.", e.getMessage());
      throw e;
    }
  }

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

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarWithExtraCommand() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name Extra --timezone Asia/Kolkata blahblah");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid create calendar command format.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarInvalidOrder() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --timezone Asia/Kolkata --name WrongOrder");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid create calendar command format.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarMissingCommandParameter() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name OnlyName");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateCalendarWithTimezoneOnly() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --timezone Asia/Kolkata");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid create calendar command format.", e.getMessage());
      throw e;
    }
  }

  @Test(expected = InvalidCommandException.class)
  public void testEditCalendarMissingValue() throws InvalidCommandException {
    try {
      controller.processCommand("create calendar --name TestEdit --timezone Asia/Kolkata");
      controller.processCommand("edit calendar --name TestEdit --property name");
    } catch (InvalidCommandException e) {
      throw e;
    }
  }

  @Test
  public void testCalendarRenameRevertToOriginalName() {
    controller.processCommand("create calendar --name Calen --timezone America/New_York");
    controller.processCommand("edit calendar --name Calen --property name Calendar");
    controller.processCommand("edit calendar --name Calendar --property name Calen");
  }

  @Test(expected = InvalidCommandException.class)
  public void testUseCalendarMissingNameParam() throws InvalidCommandException {
    try {
      controller.processCommand("use calendar SchoolCal");
    } catch (InvalidCommandException e) {
      Assert.assertEquals("Invalid use calendar command format.", e.getMessage());
      throw e;
    }
  }

  @Test
  public void testEditCalendarNameToSpecialCharacters() {
    controller.processCommand("create calendar --name OriginalName --timezone America/New_York");
    controller.processCommand("edit calendar --name OriginalName --property name @New123_");
    controller.processCommand("use calendar --name @New123_");
  }

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

  @Test
  public void testSwitchingCalendarsMultipleTimes() {
    controller.processCommand("create calendar --name WorkCal --timezone America/Chicago");
    controller.processCommand("create calendar --name PersonalCal --timezone Europe/London");

    controller.processCommand("use calendar --name WorkCal");
    controller.processCommand("create event WorkMeeting from 2025-03-10T10:00 to 2025-03-10T11:00");

    controller.processCommand("use calendar --name PersonalCal");
    controller.processCommand("create event PersonalEvent from 2025-03-10T12:00 to 2025-03-10T13:00");

    controller.processCommand("use calendar --name WorkCal");
    Assert.assertEquals("[[WorkMeeting, 2025-03-10T10:00, 2025-03-10T11:00, ]]",
            controller.model.getEventsOn(java.time.LocalDate.of(2025, 3, 10)).toString());
  }
}
