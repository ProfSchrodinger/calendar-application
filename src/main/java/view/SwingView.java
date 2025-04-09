package view;

import controller.CalendarController;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Class for the GUI interface.
 */

public class SwingView extends UserView {

  private JFrame frame;
  private JPanel topPanel;
  private JLabel monthYearLabel;
  private JButton prevButton, nextButton;
  private JPanel calendarPanel;
  private JPanel bottomPanel;
  private JLabel bottomLabel;
  private JComboBox<String> calendarComboBox;
  private JButton createCalendarButton;
  private JButton newEventButton;
  private JButton editCalendarButton;
  private JButton showEventsButton;
  private JButton exportCalendarButton;
  private JButton importCalendarButton;
  private JButton editAcrossEventsButton;
  private CalendarController controller;
  private LocalDate currentDate;

  private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern(
          "MMMM yyyy", Locale.ENGLISH);
  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
          "yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);

  /**
   * Constructor. Initializes the GUI and gains control over the controller object that is passed.
   * @param controller Controller object passed from CalendarController.
   */

  public SwingView(CalendarController controller) {
    this.controller = controller;
    currentDate = LocalDate.now();
    initialize();
  }

  /**
   * Function to initialize the GUI with tables.
   */

  private void initialize() {
    frame = new JFrame("Calendar Application - GUI");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 700);
    frame.setLayout(new BorderLayout());

    // ---------- Top Panel ----------

    JPanel navigationPanel = new JPanel(new BorderLayout());
    prevButton = new JButton("<");
    nextButton = new JButton(">");
    monthYearLabel = new JLabel("", SwingConstants.CENTER);
    updateMonthYearLabel();
    navigationPanel.add(prevButton, BorderLayout.WEST);
    navigationPanel.add(monthYearLabel, BorderLayout.CENTER);
    navigationPanel.add(nextButton, BorderLayout.EAST);

    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    calendarComboBox = new JComboBox<>();
    updateCalendarComboBox();
    createCalendarButton = new JButton("Create Calendar");
    leftPanel.add(new JLabel("Select Calendar:"));
    leftPanel.add(calendarComboBox);
    leftPanel.add(createCalendarButton);

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    newEventButton = new JButton("Create Event");
    showEventsButton = new JButton("Show Events");
    editAcrossEventsButton = new JButton("Edit Across Calendar");
    rightPanel.add(newEventButton);
    rightPanel.add(showEventsButton);
    rightPanel.add(editAcrossEventsButton);

    JPanel managementPanel = new JPanel(new BorderLayout());
    managementPanel.add(leftPanel, BorderLayout.WEST);
    managementPanel.add(rightPanel, BorderLayout.EAST);

    JPanel combinedTopPanel = new JPanel(new BorderLayout());
    combinedTopPanel.add(navigationPanel, BorderLayout.NORTH);
    combinedTopPanel.add(managementPanel, BorderLayout.SOUTH);

    topPanel = new JPanel(new BorderLayout());
    topPanel.add(combinedTopPanel, BorderLayout.CENTER);
    frame.add(topPanel, BorderLayout.NORTH);

    // ---------- Center Panel ----------

    calendarPanel = new JPanel(new BorderLayout());
    frame.add(calendarPanel, BorderLayout.CENTER);

    // ---------- Bottom Panel ----------

    bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    editCalendarButton = new JButton("Edit Calendar");
    bottomPanel.add(editCalendarButton);
    bottomLabel = new JLabel("Calendar: " + controller.getActiveCalendarName() +
            " | Timezone: " + controller.getActiveCalendarTimeZone());
    exportCalendarButton = new JButton("Export Calendar");
    importCalendarButton = new JButton("Import Calendar");
    bottomPanel.add(bottomLabel);
    bottomPanel.add(exportCalendarButton);
    bottomPanel.add(importCalendarButton);
    frame.add(bottomPanel, BorderLayout.SOUTH);

    // ---------- Action Listeners ----------

    prevButton.addActionListener(e -> {
      currentDate = currentDate.minusMonths(1);
      updateMonthYearLabel();
      drawMonthView();
    });

    nextButton.addActionListener(e -> {
      currentDate = currentDate.plusMonths(1);
      updateMonthYearLabel();
      drawMonthView();
    });

    calendarComboBox.addActionListener(e -> {
      String selected = (String) calendarComboBox.getSelectedItem();
      if (selected != null) {
        try {
          controller.processCommand("use calendar --name " + selected);
          bottomLabel.setText("Calendar: " + controller.getActiveCalendarName() +
                  " | Timezone: " + controller.getActiveCalendarTimeZone());
          drawMonthView();
        }
        catch (Exception ex) {
          JOptionPane.showMessageDialog(frame, "Error switching calendar: "
                  + ex.getMessage());
        }
      }
    });

    createCalendarButton.addActionListener(e -> {
      String calName = JOptionPane.showInputDialog(frame, "Enter new calendar name:");
      String tz = JOptionPane.showInputDialog(frame, "Enter timezone (IANA format):");
      if (calName != null && tz != null && !calName.isEmpty() && !tz.isEmpty()) {
        try {
          controller.processCommand("create calendar --name " + calName + " --timezone " + tz);
          updateCalendarComboBox();
          calendarComboBox.setSelectedItem(controller.getActiveCalendarName());
          JOptionPane.showMessageDialog(frame, "Calendar created successfully.");
        }
        catch (Exception ex) {
          JOptionPane.showMessageDialog(frame, "Error creating calendar: "
                  + ex.getMessage());
        }
      }
    });

    editCalendarButton.addActionListener(e -> {
      Object[] options = {"Name", "Timezone"};
      int choice = JOptionPane.showOptionDialog(frame,
              "Select property to edit:",
              "Edit Calendar",
              JOptionPane.DEFAULT_OPTION,
              JOptionPane.INFORMATION_MESSAGE,
              null,
              options,
              options[0]);
      if (choice == -1) return;
      String currentCal = controller.getActiveCalendarName();
      String command = "";
      if (choice == 0) {
        String newName = JOptionPane.showInputDialog(frame, "Enter new calendar name:");
        if (newName == null || newName.trim().isEmpty()) return;
        command = "edit calendar --name " + currentCal + " --property name " + newName;
      }
      else {
        String newTZ = JOptionPane.showInputDialog(frame,
                "Enter new timezone (IANA format):");
        if (newTZ == null || newTZ.trim().isEmpty()) return;
        command = "edit calendar --name " + currentCal + " --property timezone " + newTZ;
      }
      if (!command.isEmpty()) {
        try {
          controller.processCommand(command);
          bottomLabel.setText("Calendar: " + controller.getActiveCalendarName() +
                  " | Timezone: " + controller.getActiveCalendarTimeZone());
          updateCalendarComboBox();
          JOptionPane.showMessageDialog(frame, "Calendar updated successfully.");
        }
        catch (Exception ex) {
          JOptionPane.showMessageDialog(frame, "Error updating calendar: "
                  + ex.getMessage());
        }
      }
    });

    newEventButton.addActionListener(e -> {
      Object[] eventTypes = {"Single Event", "Recurring Event"};
      int eventTypeChoice = JOptionPane.showOptionDialog(frame,
              "Select event type",
              "New Event",
              JOptionPane.DEFAULT_OPTION,
              JOptionPane.INFORMATION_MESSAGE,
              null,
              eventTypes,
              eventTypes[0]);
      if (eventTypeChoice == -1) return;

      String command = "";
      if (eventTypeChoice == 0) {
        Object[] timeOptions = {"Timed Event", "All-Day Event"};
        int timeChoice = JOptionPane.showOptionDialog(frame,
                "Is this a timed event or an all-day event?",
                "New Single Event",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                timeOptions,
                timeOptions[0]);
        if (timeChoice == -1) return;
        String subject = JOptionPane.showInputDialog(frame, "Enter event subject:");
        if (subject == null || subject.trim().isEmpty()) return;
        if (timeChoice == 0) {
          String startStr = JOptionPane.showInputDialog(frame,
                  "Enter start datetime (yyyy-MM-ddTHH:mm):");
          if (startStr == null || startStr.trim().isEmpty()) return;
          String endStr = JOptionPane.showInputDialog(frame,
                  "Enter end datetime (yyyy-MM-ddTHH:mm):");
          if (endStr == null || endStr.trim().isEmpty()) return;
          command = "create event " + subject + " from " + startStr + " to " + endStr;
        }
        else {
          String dateStr = JOptionPane.showInputDialog(frame,
                  "Enter date (yyyy-MM-ddTHH:mm):");
          if (dateStr == null || dateStr.trim().isEmpty()) return;
          command = "create event " + subject + " on " + dateStr;
        }
      }
      else {
        Object[] timeOptions = {"Timed Event", "All-Day Event"};
        int timeChoice = JOptionPane.showOptionDialog(frame,
                "Is this a timed event or an all-day event?",
                "New Recurring Event",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                timeOptions,
                timeOptions[0]);
        if (timeChoice == -1) return;
        Object[] repeatOptions = {"N times", "Until Date"};
        int repeatChoice = JOptionPane.showOptionDialog(frame,
                "Specify recurrence using:",
                "Recurring Event Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                repeatOptions,
                repeatOptions[0]);
        if (repeatChoice == -1) return;
        String subject = JOptionPane.showInputDialog(frame, "Enter event subject:");
        if (subject == null || subject.trim().isEmpty()) return;
        if (timeChoice == 0) {
          String startStr = JOptionPane.showInputDialog(frame,
                  "Enter start datetime (yyyy-MM-ddTHH:mm):");
          if (startStr == null || startStr.trim().isEmpty()) return;
          String endStr = JOptionPane.showInputDialog(frame,
                  "Enter end datetime (yyyy-MM-ddTHH:mm):");
          if (endStr == null || endStr.trim().isEmpty()) return;
          String weekDays = JOptionPane.showInputDialog(frame,
                  "Enter weekdays (e.g., MTWRFSU):");
          if (weekDays == null || weekDays.trim().isEmpty()) return;
          if (repeatChoice == 0) {
            String nValue = JOptionPane.showInputDialog(frame,
                    "Enter number of occurrences:");
            if (nValue == null || nValue.trim().isEmpty()) return;
            command = "create event " + subject + " from " + startStr + " to " + endStr +
                    " repeats " + weekDays + " for " + nValue + " times";
          }
          else {
            String untilStr = JOptionPane.showInputDialog(frame,
                    "Enter until datetime (yyyy-MM-ddTHH:mm):");
            if (untilStr == null || untilStr.trim().isEmpty()) return;
            command = "create event " + subject + " from " + startStr + " to " + endStr +
                    " repeats " + weekDays + " until " + untilStr;
          }
        }
        else {
          String dateStr = JOptionPane.showInputDialog(frame,
                  "Enter date (yyyy-MM-dd):");
          if (dateStr == null || dateStr.trim().isEmpty()) return;
          String weekDays = JOptionPane.showInputDialog(frame,
                  "Enter weekdays (e.g., MTWRFSU):");
          if (weekDays == null || weekDays.trim().isEmpty()) return;
          if (repeatChoice == 0) {
            String nValue = JOptionPane.showInputDialog(frame,
                    "Enter number of occurrences:");
            if (nValue == null || nValue.trim().isEmpty()) return;
            command = "create event " + subject + " on " + dateStr +
                    " repeats " + weekDays + " for " + nValue + " times";
          }
          else {
            String untilStr = JOptionPane.showInputDialog(frame,
                    "Enter until date (yyyy-MM-dd):");
            if (untilStr == null || untilStr.trim().isEmpty()) return;
            command = "create event " + subject + " on " + dateStr +
                    " repeats " + weekDays + " until " + untilStr;
          }
        }
      }

      if (!command.isEmpty()) {
        try {
          controller.processCommand(command);
          JOptionPane.showMessageDialog(frame, "Event created successfully.");
          drawMonthView();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(frame, "Error creating event: "
                  + ex.getMessage());
        }
      }
    });

    showEventsButton.addActionListener(e -> {
      String startStr = JOptionPane.showInputDialog(frame,
              "Enter start datetime (yyyy-MM-ddTHH:mm):");
      if (startStr == null || startStr.trim().isEmpty()) return;
      String endStr = JOptionPane.showInputDialog(frame,
              "Enter end datetime (yyyy-MM-ddTHH:mm):");
      if (endStr == null || endStr.trim().isEmpty()) return;
      try {
        LocalDateTime startDT = LocalDateTime.parse(startStr, dateTimeFormatter);
        LocalDateTime endDT = LocalDateTime.parse(endStr, dateTimeFormatter);
        List<List> events = controller.getEventsBetween(startDT, endDT);
        String message = events.isEmpty() ? "No events from " + startStr + " to " + endStr
                : String.join("\n", controller.returnResult(events));
        JOptionPane.showMessageDialog(frame, message, "Events from "
                + startStr + " to " + endStr, JOptionPane.INFORMATION_MESSAGE);
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(frame, "Error showing events: "
                + ex.getMessage());
      }
    });

    exportCalendarButton.addActionListener(e -> {
      String activeName = controller.getActiveCalendarName();
      String fileName = activeName + "Events.csv";
      try {
        controller.processCommand("export cal " + fileName);
        JOptionPane.showMessageDialog(frame, "Export successful. File created as: "
                + fileName);
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(frame, "Error exporting calendar: "
                + ex.getMessage());
      }
    });

    importCalendarButton.addActionListener(e -> {
      String filePath = JOptionPane.showInputDialog(frame,
              "Enter absolute path to CSV file:");
      if (filePath == null || filePath.trim().isEmpty()) return;
      try {
        controller.processCommand("import cal " + filePath);
        JOptionPane.showMessageDialog(frame, "Import successful.");
        drawMonthView();
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(frame, "Error importing calendar: "
                + ex.getMessage());
      }
    });

    editAcrossEventsButton.addActionListener(e -> {
      String eventName = JOptionPane.showInputDialog(frame,
              "Enter event name to edit across calendar:");
      if (eventName == null || eventName.trim().isEmpty()) return;
      Object[] propOptions = {"subject", "startDateTime", "endDateTime",
              "description", "location", "isPublic"};
      String property = (String) JOptionPane.showInputDialog(frame,
              "Select property to edit:",
              "Edit Across Events",
              JOptionPane.QUESTION_MESSAGE,
              null,
              propOptions,
              propOptions[0]);
      if (property == null || property.trim().isEmpty()) return;
      String newValue = JOptionPane.showInputDialog(frame,
              "Enter new value for " + property + ":");
      if (newValue == null || newValue.trim().isEmpty()) return;
      String command = "edit events " + property + " " + eventName + " " + newValue;
      try {
        controller.processCommand(command);
        JOptionPane.showMessageDialog(frame, "Events updated successfully.");
        drawMonthView();
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(frame, "Error updating events: "
                + ex.getMessage());
      }
    });

    drawMonthView();
    frame.setVisible(true);
  }

  /**
   * Function to update the month-year label using the formatted current date.
   */

  private void updateMonthYearLabel() {
    monthYearLabel.setText(currentDate.format(monthFormatter));
  }

  /**
   * Function to retrieve calendar names from the controller and updates the combo box model.
   */
  private void updateCalendarComboBox() {
    List<String> names = controller.getCalendarNames();
    calendarComboBox.setModel(new DefaultComboBoxModel<>(names.toArray(new String[0])));
  }

  /**
   * Draws the monthly calendar grid with day headers, buttons and events presence.
   */

  private void drawMonthView() {
    JPanel monthPanel = new JPanel(new GridLayout(0, 7));
    String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String header : headers) {
      JLabel headerLabel = new JLabel(header, SwingConstants.CENTER);
      headerLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      monthPanel.add(headerLabel);
    }
    LocalDate firstOfMonth = currentDate.withDayOfMonth(1);
    int offset = firstOfMonth.getDayOfWeek().getValue() % 7;
    for (int i = 0; i < offset; i++) {
      monthPanel.add(new JLabel(""));
    }
    int daysInMonth = currentDate.lengthOfMonth();
    for (int day = 1; day <= daysInMonth; day++) {
      LocalDate date = currentDate.withDayOfMonth(day);
      JButton dayButton = new JButton(String.valueOf(day));
      List<List> eventsForDay = controller.getEventsOn(date);
      if (!eventsForDay.isEmpty()) {
        dayButton.setBorder(BorderFactory.createLineBorder(controller.getActiveCalendarColor(),
                3));
        dayButton.setOpaque(true);
        dayButton.setContentAreaFilled(true);
      }
      dayButton.addActionListener(e -> {
        try {
          List<List> events = controller.getEventsOn(date);
          if (events.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No events on " + date.toString());
          }
          else {
            Object[] options = {"Cancel", "Edit Specific Event", "Edit Events After "
                    + date.toString()};
            int choice = JOptionPane.showOptionDialog(frame,
                    "Select an option for events on " + date.toString() + ":\n" +
                            String.join("\n", controller.returnResult(events)),
                    "Edit Events",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (choice == 1) {
              Object selected = JOptionPane.showInputDialog(frame,
                      "Select event to edit:",
                      "Select Event",
                      JOptionPane.QUESTION_MESSAGE,
                      null,
                      controller.returnResult(events).toArray(),
                      controller.returnResult(events).get(0));
              if (selected == null) return;
              String eventStr = selected.toString();
              String subject = eventStr.substring(2, eventStr.indexOf(" ("));
              String range = eventStr.substring(eventStr.indexOf("(") + 1, eventStr.indexOf(")"));
              String[] parts = range.split(" - ");
              String startDT = parts[0].trim();
              String endDT = parts[1].trim();
              Object[] propOptions = {"subject", "startDateTime", "endDateTime",
                      "description", "location", "isPublic"};
              String property = (String) JOptionPane.showInputDialog(frame,
                      "Select property to edit:",
                      "Edit Property",
                      JOptionPane.QUESTION_MESSAGE,
                      null,
                      propOptions,
                      propOptions[0]);
              if (property == null || property.trim().isEmpty()) return;
              String newValue = JOptionPane.showInputDialog(frame,
                      "Enter new value for " + property + ":");
              if (newValue == null || newValue.trim().isEmpty()) return;
              String command = "edit events " + property + " " + subject + " from "
                      + startDT + " to " + endDT + " with " + newValue;
              controller.processCommand(command);
              JOptionPane.showMessageDialog(frame, "Event updated successfully.");
              drawMonthView();
            }
            else if (choice == 2) {
              String eventName = JOptionPane.showInputDialog(frame,
                      "Enter event name to edit (applies to events after "
                              + date.toString() + "):");
              if (eventName == null || eventName.trim().isEmpty()) return;
              Object[] propOptions = {"subject", "startDateTime", "endDateTime",
                      "description", "location", "isPublic"};
              String property = (String) JOptionPane.showInputDialog(frame,
                      "Select property to edit:",
                      "Edit Property",
                      JOptionPane.QUESTION_MESSAGE,
                      null,
                      propOptions,
                      propOptions[0]);
              if (property == null || property.trim().isEmpty()) return;
              String newValue = JOptionPane.showInputDialog(frame,
                      "Enter new value for " + property + ":");
              if (newValue == null || newValue.trim().isEmpty()) return;
              String startTimestamp = date.atStartOfDay().format(dateTimeFormatter);
              String command = "edit events " + property + " " + eventName
                      + " from " + startTimestamp + " with " + newValue;
              controller.processCommand(command);
              JOptionPane.showMessageDialog(frame, "Events updated successfully.");
              drawMonthView();
            }
          }
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
      });
      monthPanel.add(dayButton);
    }
    calendarPanel.removeAll();
    calendarPanel.add(monthPanel, BorderLayout.CENTER);
    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  /**
   * Function to override the originally present display method.
   * Here it displays the message in the dialogue box.
   * @param message the message to be displayed.
   */

  @Override
  public void displayMessage(String message) {
    JOptionPane.showMessageDialog(frame, message);
  }

  /**
   * Function to override the originally present input method.
   * Here it gets input as a string using the dialogue box.
   * @return The input string.
   */

  @Override
  public String getInput() {
    return JOptionPane.showInputDialog(frame, "Enter input:");
  }
}