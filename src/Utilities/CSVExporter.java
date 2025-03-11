package Utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDateTime;

public class CSVExporter {
  public String exportCSV(List<List> eventList, String fileName) {

    String absolutePath = Paths.get(fileName).toAbsolutePath().toString();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    try (FileWriter writer = new FileWriter(absolutePath)) {
      writer.write("Subject,StartDateTime,EndDateTime,Description,Location,Public\n");

      for (List<Object> eventDetails : eventList) {
        String subject = (String) eventDetails.get(0);
        LocalDateTime startDateTime = (LocalDateTime) eventDetails.get(1);
        LocalDateTime endDateTime = (LocalDateTime) eventDetails.get(2);
        String description = (String) eventDetails.get(3);
        String location = (String) eventDetails.get(4);
        Boolean isPublic = (Boolean) eventDetails.get(5);

        String startStr = (startDateTime != null) ? startDateTime.format(formatter) : "";
        String endStr = (endDateTime != null) ? endDateTime.format(formatter) : "";

        writer.write(String.format("\"%s\",%s,%s,\"%s\",\"%s\",%b\n",
                subject,
                startStr,
                endStr,
                description,
                location,
                isPublic));
      }
      return absolutePath;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
