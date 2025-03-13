package controller;

import exception.InvalidCommandException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class CalendarControllerPrintShowExportTest {
  private CalendarController controller;

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  /**
   * Invalid print command
   */

  @Test (expected = InvalidCommandException.class)
  public void testInvalidEditCommand1() {
    try {
      controller.processCommand("print events blah");
    } catch (Exception e) {
      Assert.assertEquals("Invalid command", e.getMessage());
      throw e;
    }
  }
}
