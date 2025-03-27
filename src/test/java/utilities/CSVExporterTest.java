package utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import exception.InvalidCommandException;

/**
 * Tests for exporting calendar.
 */

public class CSVExporterTest {

  /**
   * Test to check header and import value match.
   * @throws Exception if errored when processing.
   */

  @Test
  public void testExportCSV() throws Exception {
    List<List> eventList = new ArrayList<>();
    List<Object> event1 = new ArrayList<>();
    event1.add("Meeting");
    event1.add(LocalDate.of(2024, 3, 15));
    event1.add(LocalTime.of(10, 0));
    event1.add(LocalDate.of(2024, 3, 15));
    event1.add(LocalTime.of(11, 0));
    event1.add("Project discussion");
    event1.add("Room 101");
    event1.add(true);
    eventList.add(event1);

    Path tempFile = Files.createTempFile("test-export", ".csv");
    String fileName = tempFile.toAbsolutePath().toString();

    CSVExporter exporter = new CSVExporter();
    exporter.exportCSV(eventList, fileName);

    List<String> lines = Files.readAllLines(tempFile);

    String expectedHeader = "Subject, Start Date, Start Time, End Date, "
            + "End Time, Description, Location, Private";
    assertEquals("Header should match", expectedHeader, lines.get(0));

    String expectedEventLine = "\"Meeting\",03/15/2024,10:00 AM,\"03/15/2024\",\"11:00 AM\","
            + "\"Project discussion\",\"Room 101\",true";
    assertEquals("Event line should match", expectedEventLine, lines.get(1));

    Files.deleteIfExists(tempFile);
  }

  /**
   * Invalid file name.
   */

  @Test(expected = InvalidCommandException.class)
  public void testExportCSVIOExceptionThrowsException() {
    List<List> eventList = new ArrayList<>();
    String fileName = ".";

    CSVExporter exporter = new CSVExporter();
    exporter.exportCSV(eventList, fileName);
  }

  /**
   * Test that the thrown InvalidCommandException has the expected message.
   */

  @Test(expected = InvalidCommandException.class)
  public void testExportCSVIOExceptionMessage() {
    List<List> eventList = new ArrayList<>();
    String fileName = ".";

    CSVExporter exporter = new CSVExporter();
    try {
      exporter.exportCSV(eventList, fileName);
      fail("Expected InvalidCommandException due to IOException when writing CSV file.");
    } catch (InvalidCommandException e) {
      assertEquals("Error writing CSV file", e.getMessage());
      throw e;
    }
  }
}
