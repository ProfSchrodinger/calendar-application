package controller;

import exception.InvalidCommandException;
import model.CalendarModel;
import model.SingleEvent;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;
import controller.CalendarController;
import view.ConsoleView;

public class CalendarControllerTest {
  private CalendarController controller;

  @Before
  public void setUp() {
    // Use real implementations instead of mocks
    controller = new CalendarController();
  }

  @Test
  public void testProcessCommand_CreateEvent_Valid() {
    // Arrange: Correct date-time format
    String command = "create event \"Meeting\" from 2025-03-15T10:00 to 2025-03-15T11:00";

    // Act
    controller.processCommand(command);
  }

  @Test(expected = InvalidCommandException.class)
  public void testProcessCommand_CreateEvent_InvalidDate() {
    // Arrange: Invalid date-time format
    String command = "create event \"Meeting\" from 2025T10:00 to 2025T11:00";

    // Act
    controller.processCommand(command);
  }

  @Test
  public void testCheckDateTimeValidity_Valid() {
    assertTrue(controller.checkDateTimeValidity("2025-03-15T10:00"));
  }

  @Test
  public void testCheckDateTimeValidity_Invalid() {
    assertFalse(controller.checkDateTimeValidity("invalid-date"));
  }

  @Test(expected = InvalidCommandException.class)
  public void testCreateEventWithoutSubject() {
    controller.processCommand("create event from 2025-03-15T10:00 to 2025-03-15T12:00");
  }
}
