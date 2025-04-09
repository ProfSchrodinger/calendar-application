package utilities;

import exception.InvalidCommandException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class to import events from CSV files to the calendar system.
 */

public class CSVImporter {

  private final DateTimeFormatter primaryDateFormatter = DateTimeFormatter.ofPattern(
          "MM/dd/yyyy", Locale.ENGLISH);
  private final DateTimeFormatter altDateFormatter = DateTimeFormatter.ofPattern(
          "M/d/yy", Locale.ENGLISH);
  private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(
          "hh:mm a", Locale.ENGLISH);

  /**
   * Function to read the file and extract the required information.
   * @param filePath The path to the file.
   * @return List of List containing the events that are recognised.
   */

  public List<List> importEvents(String filePath) {
    List<List> importedEvents = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(filePath).toFile())))
    {
      String headerLine = reader.readLine();
      if (headerLine == null) {
        throw new InvalidCommandException("CSV file is empty.");
      }
      String[] headers = headerLine.split("\\s*,\\s*");
      Map<String, Integer> headerMap = new HashMap<>();
      for (int i = 0; i < headers.length; i++) {
        headerMap.put(headers[i].trim(), i);
      }

      if (!headerMap.containsKey("Subject") || !headerMap.containsKey("Start Date")
              || !headerMap.containsKey("Start Time")) {
        throw new InvalidCommandException("CSV must contain 'Subject', "
                + "'Start Date', and 'Start Time' columns.");
      }

      String line;
      int lineIndex = 1;
      while ((line = reader.readLine()) != null) {
        lineIndex++;
        String[] tokens = line.split("\\s*,\\s*");

        try {
          List<Object> eventDetails = new ArrayList<>();

          String subject = unquote(tokens[headerMap.get("Subject")]);
          eventDetails.add(subject);

          String startDateStr = unquote(tokens[headerMap.get("Start Date")]);
          String startTimeStr = unquote(tokens[headerMap.get("Start Time")]);
          LocalDate startDate = parseDate(startDateStr, lineIndex);
          LocalTime startTime = parseTime(startTimeStr, lineIndex);
          eventDetails.add(startDate);
          eventDetails.add(startTime);

          LocalDate endDate = null;
          LocalTime endTime = null;
          if (headerMap.containsKey("End Date")) {
            String rawEndDate = unquote(tokens[headerMap.get("End Date")]);
            endDate = parseOptionalDate(rawEndDate, lineIndex);
          }
          if (headerMap.containsKey("End Time")) {
            String rawEndTime = unquote(tokens[headerMap.get("End Time")]);
            endTime = parseOptionalTime(rawEndTime, lineIndex);
          }
          if (endDate == null) {
            endDate = startDate.plusDays(1);
          }
          if (endTime == null) {
            endTime = LocalTime.of(0, 0);
          }
          eventDetails.add(endDate);
          eventDetails.add(endTime);

          String description = "";
          if (headerMap.containsKey("Description")) {
            description = unquote(tokens[headerMap.get("Description")]);
          }
          eventDetails.add(description);

          String location = "";
          if (headerMap.containsKey("Location")) {
            location = unquote(tokens[headerMap.get("Location")]);
          }
          eventDetails.add(location);

          boolean isPublic = false;
          if (headerMap.containsKey("Private")) {
            String privateValue = unquote(tokens[headerMap.get("Private")]);
            if (privateValue != null && !privateValue.trim().isEmpty()) {
              isPublic = !Boolean.parseBoolean(privateValue.trim());
            }
          }
          eventDetails.add(isPublic);

          importedEvents.add(eventDetails);
        }
        catch (Exception e) {
          System.err.println("Error parsing line " + lineIndex + ": " + e.getMessage());
        }
      }
    }
    catch (IOException e) {
      throw new InvalidCommandException("Error reading CSV file");
    }
    return importedEvents;
  }

  /**
   * Removes surrounding quotes if present.
   * @param s The string to be stripped of quotes.
   * @return Trimmed string.
   */

  private String unquote(String s) {
    if (s == null) return null;
    return s.replaceAll("^\"|\"$", "");
  }

  /**
   * Parses a required date using the primary formatter first and then an alternate formatter.
   */

  /**
   * Parses a required date using the primary formatter first and then an alternate formatter.
   * @param value The string value of the date.
   * @param lineIndex The line at which the date is present.
   * @return Parsed value.
   */

  private LocalDate parseDate(String value, int lineIndex) {
    try {
      return LocalDate.parse(value, primaryDateFormatter);
    }
    catch (DateTimeParseException e) {
      try {
        return LocalDate.parse(value, altDateFormatter);
      }
      catch (DateTimeParseException ex) {
        throw new InvalidCommandException("Error parsing date on line " + lineIndex);
      }
    }
  }

  /**
   * For optional dates, return null if empty, otherwise parse.
   * @param value The string value of the date.
   * @param lineIndex The line at which the date is present.
   * @return Parsed value.
   */

  private LocalDate parseOptionalDate(String value, int lineIndex) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    return parseDate(value, lineIndex);
  }

  /**
   * Parses a required time.
   * @param value The string value of the time.
   * @param lineIndex The line at which the time is present.
   * @return Parsed value.
   */

  private LocalTime parseTime(String value, int lineIndex) {
    try {
      return LocalTime.parse(value, timeFormatter);
    } catch (DateTimeParseException e) {
      throw new InvalidCommandException("Error parsing time on line " + lineIndex);
    }
  }

  /**
   * For optional times, return null if empty, otherwise parse.
   * @param value The string value of the time.
   * @param lineIndex The line at which the time is present.
   * @return Parsed value.
   */

  private LocalTime parseOptionalTime(String value, int lineIndex) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    return parseTime(value, lineIndex);
  }
}
