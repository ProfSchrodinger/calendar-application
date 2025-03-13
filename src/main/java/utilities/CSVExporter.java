package utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import exception.InvalidCommandException;

public class CSVExporter {
  public void exportCSV(List<List> eventList, String fileName) {

    String absolutePath = Paths.get(fileName).toAbsolutePath().toString();
    DateTimeFormatter csvDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    DateTimeFormatter csvTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    try (FileWriter writer = new FileWriter(absolutePath)) {
      writer.write("Subject, Start Date, Start Time, End Date, End Time, Description, Location, Private\n");

      for (List<Object> eventDetails : eventList) {
        String subject = (String) eventDetails.get(0);
        LocalDate startDate = (LocalDate) eventDetails.get(1);
        LocalTime startTime = (LocalTime) eventDetails.get(2);
        LocalDate endDate = (LocalDate) eventDetails.get(3);
        LocalTime endTime = (LocalTime) eventDetails.get(4);
        String description = (String) eventDetails.get(5);
        String location = (String) eventDetails.get(6);
        Boolean isPublic = (Boolean) eventDetails.get(7);

        String startDateStr = (startDate != null) ? startDate.format(csvDateFormatter) : "";
        String startTimeStr = (startTime != null) ? startTime.format(csvTimeFormatter) : "";
        String endDateStr = (endDate != null) ? endDate.format(csvDateFormatter) : "";
        String endTimeStr = (endTime != null) ? endTime.format(csvTimeFormatter) : "";

        writer.write(String.format("\"%s\",%s,%s,\"%s\",\"%s\",\"%s\",\"%s\",%b\n",
                subject,
                startDateStr,
                startTimeStr,
                endDateStr,
                endTimeStr,
                description,
                location,
                isPublic));
      }
//      return absolutePath;
    }
    catch (IOException e) {
      throw new InvalidCommandException("Error writing CSV file");
    }
  }
}
