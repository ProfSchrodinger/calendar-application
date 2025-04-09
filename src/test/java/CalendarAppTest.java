import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the CalendarApp main method.
 * This class tests different branches of the application including interactive and headless modes.
 */

public class CalendarAppTest {
  private final PrintStream originalOut = System.out;
  private final java.io.InputStream originalIn = System.in;
  private SecurityManager originalSecurityManager;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  /**
   * Custom SecurityManager to intercept calls to System.exit.
   * When System.exit is called, this SecurityManager throws a SecurityException,
   * allowing tests to verify that System.exit was attempted.
   */

  private static class NoExitSecurityManager extends SecurityManager {

    /**
     * Allow all permissions.
     * @param perm   the requested permission.
     */

    @Override
    public void checkPermission(java.security.Permission perm) {
      // Allow all permissions.
    }

    /**
     * Allow all permissions.
     * @param perm   the requested permission.
     */

    @Override
    public void checkPermission(java.security.Permission perm, Object context) {
      // Allow all permissions.
    }

    /**
     * function to check exit.
     * @param status   the exit status.
     */

    @Override
    public void checkExit(int status) {
      super.checkExit(status);
      throw new SecurityException("System.exit(" + status + ") called");
    }
  }

  /**
   * Setup of the securityManager object.
   */
  @Before
  public void setUp() {
    originalSecurityManager = System.getSecurityManager();
    System.setSecurityManager(new NoExitSecurityManager());
    System.setOut(new PrintStream(outContent));
  }

  /**
   * Restores the original System.out, System.in, and SecurityManager after tests.
   */

  @After
  public void tearDown() {
    System.setSecurityManager(originalSecurityManager);
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  /**
   * Test that with fewer than 2 arguments the application prints the error message
   * and calls System.exit(1).
   */

  @Test
  public void testInsufficientArguments() {
    String[] args = { "--mode" };
    try {
      CalendarApp.main(args);
      fail("Expected System.exit to be called due to insufficient arguments");
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit(1)"));
      String output = outContent.toString();
      assertTrue(output.contains("Invalid mode, use: --mode interactive "
              + "OR --mode headless <commandFile>"));
    }
  }

  /**
   * Test that if the first argument is not "--mode", the expected message is printed.
   */

  @Test
  public void testInvalidFirstArgument() {
    String[] args = { "wrong", "interactive" };
    CalendarApp.main(args);
    String output = outContent.toString();
    assertTrue(output.contains("First argument must be --mode"));
  }

  /**
   * Test headless mode when an invalid file path is provided.
   */

  @Test
  public void testHeadlessInvalidFilePath() {
    String simulatedInput = "nonexistent_file.csv\n";
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    String[] args = { "--mode", "headless" };
    CalendarApp.main(args);
    String output = outContent.toString();
    assertTrue(output.contains("Invalid Path:"));
  }

  /**
   * Test headless mode with a file that does not contain at least
   * 2 commands or does not end with "exit".
   */

  @Test
  public void testHeadlessInsufficientCommands() throws Exception {
    Path tempFile = Files.createTempFile("commands", ".csv");
    Files.write(tempFile, "print events on 2025-03-12".getBytes());
    String simulatedInput = tempFile.toAbsolutePath().toString() + "\n";
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    String[] args = { "--mode", "headless" };
    CalendarApp.main(args);
    String output = outContent.toString();
    assertTrue(output.contains("File does not contain exit command or less than 2 commands"));
    Files.deleteIfExists(tempFile);
  }
}
