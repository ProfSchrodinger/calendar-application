package view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ConsoleView.
 */

public class ConsoleViewTest {

  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;

  private ByteArrayOutputStream outContent;

  /**
   * Sets up System.out capture before each test.
   */

  @Before
  public void setUp() {
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  /**
   * Restores System.out and System.in after each test.
   */

  @After
  public void tearDown() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  /**
   * Tests that displayMessage prints the given message to System.out.
   */

  @Test
  public void testDisplayMessage() {
    ConsoleView view = new ConsoleView();
    String message = "Hello, World!";
    view.displayMessage(message);

    String output = outContent.toString();
    assertTrue("Output should contain the message", output.contains(message));
  }

  /**
   * Tests that getInput returns the simulated input string.
   */

  @Test
  public void testGetInput() {
    String simulatedInput = "test input";
    ByteArrayInputStream inContent = new ByteArrayInputStream(simulatedInput.getBytes());
    System.setIn(inContent);

    ConsoleView view = new ConsoleView();
    String input = view.getInput();

    assertEquals("The input should match the simulated input", simulatedInput, input);
  }
}
