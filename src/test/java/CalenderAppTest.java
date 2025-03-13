import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CalenderAppTest {
  private final PrintStream originalOut = System.out;
  private final java.io.InputStream originalIn = System.in;
  private SecurityManager originalSecurityManager;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  private static class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(java.security.Permission perm) {
    }
    @Override
    public void checkPermission(java.security.Permission perm, Object context) {
    }
    @Override
    public void checkExit(int status) {
      super.checkExit(status);
      throw new SecurityException("System.exit(" + status + ") called");
    }
  }

  @Before
  public void setUp() {
    originalSecurityManager = System.getSecurityManager();
    System.setSecurityManager(new NoExitSecurityManager());
    System.setOut(new PrintStream(outContent));
  }

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
      CalenderApp.main(args);
      fail("Expected System.exit to be called due to insufficient arguments");
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit(1)"));
      String output = outContent.toString();
      assertTrue(output.contains("Invalid mode, use: --mode interactive OR --mode headless <commandFile>"));
    }
  }

  /**
   * Test that if the first argument is not "--mode", the expected message is printed.
   */

  @Test
  public void testInvalidFirstArgument() {
    String[] args = { "wrong", "interactive" };
    CalenderApp.main(args);
    String output = outContent.toString();
    assertTrue(output.contains("First argument must be --mode"));
  }

  @Test
  public void testInteractiveModeExit() {
    String simulatedInput = "exit\n";
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

    String[] args = {"--mode", "interactive"};
    try {
      CalenderApp.main(args);
      fail("Expected System.exit(0) to be called in interactive mode");
    }
    catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit(0)"));
      String output = outContent.toString();
      assertTrue("Output should contain interactive mode prompt",
              output.contains("Interactive mode on. Type 'exit' to quit"));
    }

  }

  /**
   * Test headless mode when an invalid file path is provided.
   */

  @Test
  public void testHeadlessInvalidFilePath() {
    String simulatedInput = "nonexistent_file.csv\n";
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    String[] args = { "--mode", "headless" };
    CalenderApp.main(args);
    String output = outContent.toString();
    assertTrue(output.contains("Invalid Path:"));
  }

  /**
   * Test headless mode with a file that does not contain at least 2 commands or does not end with "exit".
   */

  @Test
  public void testHeadlessInsufficientCommands() throws Exception {
    Path tempFile = Files.createTempFile("commands", ".csv");
    Files.write(tempFile, "print events on 2025-03-12".getBytes());
    String simulatedInput = tempFile.toAbsolutePath().toString() + "\n";
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    String[] args = { "--mode", "headless" };
    CalenderApp.main(args);
    String output = outContent.toString();
    assertTrue(output.contains("File does not contain exit command or less than 2 commands"));
    Files.deleteIfExists(tempFile);
  }
}
