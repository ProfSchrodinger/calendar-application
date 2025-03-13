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

public class ConsoleViewTest {

  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;

  private ByteArrayOutputStream outContent;

  @Before
  public void setUp() {
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  @Test
  public void testDisplayMessage() {
    ConsoleView view = new ConsoleView();
    String message = "Hello, World!";
    view.displayMessage(message);

    String output = outContent.toString();
    assertTrue("Output should contain the message", output.contains(message));
  }

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
