package utilities;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * JUnit4 test cases for CSVImporter.
 */

public class CSVImporterTest {

  private Path tempFile;

  @Before
  public void setUp() throws Exception {
    tempFile = Files.createTempFile("CSVImporterTest", ".csv");
  }

  @After
  public void tearDown() throws Exception {
    Files.deleteIfExists(tempFile);
  }

  /**
   * Test the private unquote method using reflection.
   */

  @Test
  public void testUnquote() throws Exception {
    CSVImporter importer = new CSVImporter();
    Method unquoteMethod = CSVImporter.class.getDeclaredMethod("unquote", String.class);
    unquoteMethod.setAccessible(true);

    String result = (String) unquoteMethod.invoke(importer, (Object) null);
    assertNull("Unquote should return null for null input", result);

    String result2 = (String) unquoteMethod.invoke(importer, "\"Hello\"");
    assertEquals("Hello", result2);

    String result3 = (String) unquoteMethod.invoke(importer, "World");
    assertEquals("World", result3);
  }

  /**
   * Test that if the CSV file contains an End Date header but the value is empty,
   * the importer uses the default end date (startDate + 1).
   */

  @Test
  public void testOptionalEndDateDefault() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Description, Location, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,,11:00 AM,\"Desc\",\"Loc\",false";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());

    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List eventDetails = events.get(0);
    LocalDate startDate = (LocalDate) eventDetails.get(1);
    LocalDate endDate = (LocalDate) eventDetails.get(3);
    assertEquals("Default end date should be startDate + 1 day", startDate.plusDays(1), endDate);
  }

  /**
   * Test that if the CSV file contains an End Time header but the value is empty,
   * the importer uses the default end time (00:00).
   */

  @Test
  public void testOptionalEndTimeDefault() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Description, Location, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,03/15/2024,,\"Desc\",\"Loc\",false";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());

    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List eventDetails = events.get(0);
    LocalTime endTime = (LocalTime) eventDetails.get(4);
    assertEquals("Default end time should be 00:00", LocalTime.of(0, 0), endTime);
  }

  /**
   * Test that if the CSV file is missing the "End Date" header, the default end date is used.
   */

  @Test
  public void testMissingEndDateColumn() throws Exception {
    String header = "Subject, Start Date, Start Time, End Time, Description, Location, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,11:00 AM,\"Desc\",\"Loc\",false";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());

    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List eventDetails = events.get(0);
    LocalDate startDate = (LocalDate) eventDetails.get(1);
    LocalDate endDate = (LocalDate) eventDetails.get(3);
    assertEquals("Missing End Date should default to startDate + 1 day", startDate.plusDays(1), endDate);
  }

  /**
   * Test that if the CSV file is missing the "End Time" header, the default end time is used.
   */

  @Test
  public void testMissingEndTimeColumn() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, Description, Location, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,03/15/2024,\"Desc\",\"Loc\",false";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());

    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List eventDetails = events.get(0);
    LocalTime endTime = (LocalTime) eventDetails.get(4);
    assertEquals("Missing End Time should default to 00:00", LocalTime.of(0, 0), endTime);
  }

  /**
   * Test that if the CSV file is missing the "Description" header,
   * the default value (empty string) is used.
   */

  @Test
  public void testMissingDescriptionColumn() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Location, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,03/15/2024,11:00 AM,\"Room 101\",false";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());

    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List eventDetails = events.get(0);
    String description = (String) eventDetails.get(5);
    assertEquals("Missing Description should default to empty string", "", description);
  }

  /**
   * Test that if the CSV file is missing the "Location" header,
   * the default value (empty string) is used.
   */

  @Test
  public void testMissingLocationColumn() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Description, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,03/15/2024,11:00 AM,\"Project discussion\",false";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());

    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List eventDetails = events.get(0);
    String location = (String) eventDetails.get(6);
    assertEquals("Missing Location should default to empty string", "", location);
  }

  /**
   * Test that if the CSV file is missing the "Private" header,
   * the default isPublic value remains false.
   */

  @Test
  public void testMissingPrivateColumn() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Description, Location";
    String row = "\"Test Event\",03/15/2024,10:00 AM,03/15/2024,11:00 AM,\"Project discussion\",\"Room 101\"";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());

    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List eventDetails = events.get(0);
    Boolean isPublic = (Boolean) eventDetails.get(7);
    assertFalse("Missing Private should default isPublic to false", isPublic);
  }

  /**
   * Test that an invalid date in a row is reported with the correct line number,
   * and that the row is skipped (verifying lineIndex is incremented correctly).
   */

  @Test
  public void testInvalidDateReportsCorrectLine() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Description, Location, Private";
    String row1 = "\"Valid Event\",03/15/2024,10:00 AM,03/15/2024,11:00 AM,\"Desc\",\"Loc\",false";
    String row2 = "\"Invalid Event\",invalid,10:00 AM,03/15/2024,11:00 AM,\"Desc\",\"Loc\",false";
    String content = header + "\n" + row1 + "\n" + row2;
    Files.write(tempFile, content.getBytes());

    java.io.ByteArrayOutputStream errContent = new java.io.ByteArrayOutputStream();
    System.setErr(new java.io.PrintStream(errContent));

    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals("Only one valid event should be imported", 1, events.size());
    String stderr = errContent.toString();
    assertTrue("Error message should mention line 3", stderr.contains("line 3"));

    System.setErr(System.err);
  }

  /**
   * Helper method to invoke a private method via reflection.
   */

  private Object invokePrivateMethod(String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
    CSVImporter importer = new CSVImporter();
    Method method = CSVImporter.class.getDeclaredMethod(methodName, paramTypes);
    method.setAccessible(true);
    return method.invoke(importer, args);
  }

  /**
   * Test that parseDate uses the alternate formatter when the primary fails.
   * For example, "4/5/25" (which does not match MM/dd/yyyy) should be parsed with altDateFormatter.
   */

  @Test
  public void testParseDateUsesAlternateFormatter() throws Exception {
    String inputDate = "4/5/25";
    int lineIndex = 1;
    LocalDate expectedDate = LocalDate.of(2025, 4, 5);
    LocalDate parsedDate = (LocalDate) invokePrivateMethod(
            "parseDate", new Class[]{String.class, int.class}, new Object[]{inputDate, lineIndex});
    assertEquals("Should use alternate formatter for '4/5/25'", expectedDate, parsedDate);
  }

  /**
   * Test that parseOptionalDate returns null when given a null or empty string.
   */

  @Test
  public void testParseOptionalDateReturnsNull() throws Exception {
    int lineIndex = 1;
    LocalDate result = (LocalDate) invokePrivateMethod("parseOptionalDate", new Class[]{String.class, int.class}, new Object[]{null, lineIndex});
    assertNull("parseOptionalDate should return null for null input", result);

    result = (LocalDate) invokePrivateMethod("parseOptionalDate", new Class[]{String.class, int.class}, new Object[]{"   ", lineIndex});
    assertNull("parseOptionalDate should return null for empty string", result);
  }

  /**
   * Test that parseOptionalDate returns a valid LocalDate when given a valid date string.
   */

  @Test
  public void testParseOptionalDateNonEmpty() throws Exception {
    String inputDate = "03/15/2024";
    int lineIndex = 1;
    LocalDate expectedDate = LocalDate.of(2024, 3, 15);
    LocalDate parsedDate = (LocalDate) invokePrivateMethod("parseOptionalDate", new Class[]{String.class, int.class}, new Object[]{inputDate, lineIndex});
    assertEquals("Should parse a valid date using primary formatter", expectedDate, parsedDate);
  }

  /**
   * Test that parseTime correctly parses a valid time string.
   */

  @Test
  public void testParseTimeValid() throws Exception {
    String inputTime = "10:00 AM";
    int lineIndex = 1;
    LocalTime expectedTime = LocalTime.of(10, 0);
    LocalTime parsedTime = (LocalTime) invokePrivateMethod("parseTime", new Class[]{String.class, int.class}, new Object[]{inputTime, lineIndex});
    assertEquals("Should correctly parse time '10:00 AM'", expectedTime, parsedTime);
  }

  /**
   * Test that parseOptionalTime returns null when given a null or empty input.
   */

  @Test
  public void testParseOptionalTimeReturnsNull() throws Exception {
    int lineIndex = 1;
    LocalTime result = (LocalTime) invokePrivateMethod("parseOptionalTime", new Class[]{String.class, int.class}, new Object[]{null, lineIndex});
    assertNull("parseOptionalTime should return null for null input", result);

    result = (LocalTime) invokePrivateMethod("parseOptionalTime", new Class[]{String.class, int.class}, new Object[]{"   ", lineIndex});
    assertNull("parseOptionalTime should return null for empty string", result);
  }

  /**
   * Test that parseOptionalTime returns a valid LocalTime when given a valid time string.
   */

  @Test
  public void testParseOptionalTimeNonEmpty() throws Exception {
    String inputTime = "10:00 AM";
    int lineIndex = 1;
    LocalTime expectedTime = LocalTime.of(10, 0);
    LocalTime parsedTime = (LocalTime) invokePrivateMethod("parseOptionalTime", new Class[]{String.class, int.class}, new Object[]{inputTime, lineIndex});
    assertEquals("Should correctly parse time '10:00 AM'", expectedTime, parsedTime);
  }

  /**
   * Test that if the "Private" column value is "true", then isPublic is set to false.
   */

  @Test
  public void testPrivateValueParsingTrue() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Description, Location, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,03/15/2024,11:00 AM,\"Desc\",\"Room A\",\"true\"";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());
    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List<?> eventDetails = events.get(0);
    Boolean isPublic = (Boolean) eventDetails.get(7);
    assertFalse("Private value 'true' should result in isPublic false", isPublic);
  }

  /**
   * Test that if the "Private" column value is "false", then isPublic is set to true.
   */

  @Test
  public void testPrivateValueParsingFalse() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Description, Location, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,03/15/2024,11:00 AM,\"Desc\",\"Room A\",\"false\"";
    String content = header + "\n" + row;
    Files.write(tempFile, content.getBytes());
    CSVImporter importer = new CSVImporter();
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List<?> eventDetails = events.get(0);
    Boolean isPublic = (Boolean) eventDetails.get(7);
    assertTrue("Private value 'false' should result in isPublic true", isPublic);
  }

  /**
   * Test that if the "Private" column value has surrounding whitespace,
   * it is correctly trimmed before parsing.
   */

  @Test
  public void testPrivateValueParsingWithWhitespace() throws Exception {
    String header = "Subject, Start Date, Start Time, End Date, End Time, Description, Location, Private";
    String row = "\"Test Event\",03/15/2024,10:00 AM,03/15/2024,11:00 AM,\"Desc\",\"Room A\",\"  true  \"";
    String content = header + "\n" + row;
    CSVImporter importer = new CSVImporter();
    Files.write(tempFile, content.getBytes());
    List<List> events = importer.importEvents(tempFile.toAbsolutePath().toString());
    assertEquals(1, events.size());
    List<?> eventDetails = events.get(0);
    Boolean isPublic = (Boolean) eventDetails.get(7);
    assertFalse("Private value '  true  ' should trim to 'true' and result in isPublic false", isPublic);
  }
}