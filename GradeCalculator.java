import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class GradeCalculator extends JFrame {
    // Laboratory Fields
    private JTextField mp1Field, mp2Field, mp3Field, mp4Field;
    // Lecture Fields
    private JTextField essayField, pvmField, javaBasicsField, introJsField, prelimExamField;
    // Prelim Exam Fields for Laboratory Panel
    private JTextField java1Field, java2Field, js1Field, js2Field; 

    // Attendance checkboxes for each panel
    private JCheckBox[] labAttendanceCheckboxes;
    private JCheckBox[] lecAttendanceCheckboxes;
    
    // Outer CardLayout for "Laboratory" vs "Lecture"
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Sub-CardLayouts for each panel: "Blank", "Manual" vs "CSV"
    private CardLayout labModeLayout;
    private JPanel labModePanel; 
    private JPanel labBlankPanel;
    private JPanel labManualPanel; 
    private JPanel labCsvPanelWrapper;

    private CardLayout lecModeLayout;
    private JPanel lecModePanel;
    private JPanel lecBlankPanel;
    private JPanel lecManualPanel;
    private JPanel lecCsvPanelWrapper;

    private JLabel labResultLabel, lecResultLabel;

    // CSV tables
    private JTable lectureCsvTable, labCsvTable;

    // Paths to animation GIFs
    private final String successGifPath = "C:\\Users\\1APH\\Desktop\\Programming 2 - LAB Midterm\\congrats.gif";
    private final String failureGifPath = "C:\\Users\\1APH\\Desktop\\Programming 2 - LAB Midterm\\failed.gif";


    // Custom JPanel to set a background image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(imagePath).getImage();
            setLayout(new BorderLayout());
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public GradeCalculator() {
        setTitle("BSCSIT 1203 9301 Programming 2 - Grade Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new BackgroundPanel("C:/Users/ARNOLD/firstyr_BSCS/SECOND SEM/Midterm_Requirement_1/background.jpg");
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(40, 20, 20, 20));
        setContentPane(mainPanel);

        // ========== Title and Button Panel at Top ==========
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Programming 2 - Grade Calculator 💻");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 30));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descriptionLabel = new JLabel("<html><div style='text-align: center; font-size:14px; font-weight:bold; color:#333;'>"
                + "Choose whether you want to compute for<br>Laboratory or Lecture grades."
                + "</div></html>");
        descriptionLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton labButton = new JButton("Laboratory");
        JButton lecButton = new JButton("Lecture");
        customizeButton(labButton);
        customizeButton(lecButton);
        buttonPanel.add(labButton);
        buttonPanel.add(lecButton);

        titlePanel.add(titleLabel, gbc);
        gbc.gridy++;
        titlePanel.add(descriptionLabel, gbc);
        gbc.gridy++;
        titlePanel.add(buttonPanel, gbc);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // ========== Card Layout for Panels ==========
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createLaboratoryPanel(), "Laboratory");
        cardPanel.add(createLecturePanel(), "Lecture");
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        labButton.addActionListener(_ -> cardLayout.show(cardPanel, "Laboratory"));
        lecButton.addActionListener(_ -> cardLayout.show(cardPanel, "Lecture"));

        setSize(800, 700);
        setVisible(true);
    }

    // ================== Laboratory Panel ==================
    private JPanel createLaboratoryPanel() {
        JPanel pinkPanel = new JPanel(new BorderLayout());
        // Set pastel teal background and a complementary teal border
        pinkPanel.setBackground(new Color(175, 238, 238));
        pinkPanel.setOpaque(true);
        pinkPanel.setBorder(new LineBorder(new Color(0, 128, 128), 3));

        // Mode selector at top for Lab
        JPanel modeSelectorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        modeSelectorPanel.setOpaque(false);
        JLabel modeLabel = new JLabel("How do you want to input LABORATORY grades?");
        JButton manualButton = new JButton("Manual Entry");
        JButton csvButton = new JButton("Upload File");
        customizeButton(manualButton);
        customizeButton(csvButton);
        modeSelectorPanel.add(modeLabel);
        modeSelectorPanel.add(manualButton);
        modeSelectorPanel.add(csvButton);
        pinkPanel.add(modeSelectorPanel, BorderLayout.NORTH);

        // Sub-panel with CardLayout for Lab: Blank, Manual, CSV
        labModeLayout = new CardLayout();
        labModePanel = new JPanel(labModeLayout);
        labModePanel.setOpaque(false);
        
        labBlankPanel = new JPanel();
        labBlankPanel.setOpaque(false);

        labManualPanel = buildLabManualPanel();
        labCsvPanelWrapper = buildLabCsvPanel();

        labModePanel.add(labBlankPanel, "Blank");
        labModePanel.add(labManualPanel, "Manual");
        labModePanel.add(labCsvPanelWrapper, "CSV");

        // Show Manual by default
        labModeLayout.show(labModePanel, "Manual");
        pinkPanel.add(labModePanel, BorderLayout.CENTER);

        manualButton.addActionListener(_ -> labModeLayout.show(labModePanel, "Manual"));
        csvButton.addActionListener(_ -> labModeLayout.show(labModePanel, "CSV"));

        return pinkPanel;
    }

    private JPanel buildLabManualPanel() {
        // Create the panel and set its background to pastel teal
        JPanel manualPanel = new JPanel(new BorderLayout());
        manualPanel.setBackground(new Color(175, 238, 238));
        manualPanel.setOpaque(true);
        
        // Main input panel using GridBagLayout for two columns
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(175, 238, 238));
        inputPanel.setOpaque(true);
        GridBagConstraints gbc = new GridBagConstraints();  
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Attendance Panel (spanning both columns)
        JPanel attendancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attendancePanel.setOpaque(false);
        JLabel attendanceLabel = new JLabel("Attendance (Check if present): ");
        attendancePanel.add(attendanceLabel);

        // Define attendance dates for Laboratory and Lecture
        String[] labDates = {"Jan 21", "Jan 28", "Feb 4", "Feb 11", "Feb 18"};
        String[] lecDates = {"Jan 23", "Jan 30", "Feb 6", "Feb 13", "Feb 20"};

        // Create checkboxes for attendance (default to Laboratory)
        labAttendanceCheckboxes = new JCheckBox[labDates.length];
        for (int i = 0; i < labDates.length; i++) {
            labAttendanceCheckboxes[i] = new JCheckBox(labDates[i]);
            labAttendanceCheckboxes[i].setOpaque(false);
            attendancePanel.add(labAttendanceCheckboxes[i]);
        }

        // Function to update attendance based on mode
        ActionListener updateAttendance = e -> {
            boolean isLabMode = e.getActionCommand().equals("Laboratory");
            String[] newDates = isLabMode ? labDates : lecDates;

            // Update checkbox labels
            for (int i = 0; i < labAttendanceCheckboxes.length; i++) {
                labAttendanceCheckboxes[i].setText(newDates[i]);
                labAttendanceCheckboxes[i].setSelected(false); // Reset selection
            }
        };

        // Buttons to switch modes
        JButton labButton = new JButton("Laboratory");
        JButton lecButton = new JButton("Lecture");

        labButton.addActionListener(updateAttendance);
        lecButton.addActionListener(updateAttendance);

        // Add attendance panel to inputPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(attendancePanel, gbc);

        // Reset gridwidth for next rows
        gbc.gridwidth = 1;
        
        // Column titles for Laboratory Requirement and Prelim Exam
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel labReqLabel = new JLabel("Laboratory Requirement");
        labReqLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        labReqLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(labReqLabel, gbc);
        
        gbc.gridx = 1;
        JLabel prelimExamLabel = new JLabel("   Prelim Exam");
        prelimExamLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        prelimExamLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(prelimExamLabel, gbc);
        
        // Row for MP1 and Java 1
        gbc.gridy++;
        gbc.gridx = 0;
        inputPanel.add(createFieldRow("MP1:           ", mp1Field = new JTextField(10)), gbc);
        gbc.gridx = 1;
        inputPanel.add(createFieldRow("                    Java 1:", java1Field = new JTextField(11)), gbc);
        
        // Row for MP2 and Java 2
        gbc.gridy++;
        gbc.gridx = 0;
        inputPanel.add(createFieldRow("MP2:           ", mp2Field = new JTextField(10)), gbc);
        gbc.gridx = 1;
        inputPanel.add(createFieldRow("                    Java 2:", java2Field = new JTextField(11)), gbc);
        
        // Row for MP3 and JS 1
        gbc.gridy++;
        gbc.gridx = 0;
        inputPanel.add(createFieldRow("MP3:           ", mp3Field = new JTextField(10)), gbc);
        gbc.gridx = 1;
        inputPanel.add(createFieldRow("                    JS 1:    ", js1Field = new JTextField(11)), gbc);
        
        // Row for MP3 Docu and JS 2
        gbc.gridy++;
        gbc.gridx = 0;
        inputPanel.add(createFieldRow("MP3 Docu: ", mp4Field = new JTextField(10)), gbc);
        gbc.gridx = 1;
        inputPanel.add(createFieldRow("                    JS 2:    ", js2Field = new JTextField(11)), gbc);
        
        // Compute Button (spanning both columns)
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonRow.setOpaque(false);
        JButton calculateButton = new JButton("Compute Grade");
        customizeButton(calculateButton);
        buttonRow.add(calculateButton);
        inputPanel.add(buttonRow, gbc);
        
        // Final Result Label (spanning both columns)
        gbc.gridy++;
        labResultLabel = new JLabel("Final Prelim Grade: --");
        labResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputPanel.add(labResultLabel, gbc);
        
        calculateButton.addActionListener(this::computeLabGrade);
        
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        manualPanel.add(scrollPane, BorderLayout.CENTER);
        
        return manualPanel;
    }

    /**
     * Helper method to create a row with a label and a text field.
     */
    private JPanel createFieldRow(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        textField.setPreferredSize(new Dimension(150, 30));
        textField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        panel.add(label);
        panel.add(textField);
        return panel;
    }
    
    private JPanel buildLabCsvPanel() {
        // Build CSV panel for Lab
        JPanel csvPanel = new JPanel(new BorderLayout());
        csvPanel.setOpaque(true);
        csvPanel.setBackground(new Color(175, 238, 238));
        
        // Top panel for CSV buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setOpaque(false);
        
        JButton labCsvButton = new JButton("Upload File");
        customizeButton(labCsvButton);
        labCsvButton.addActionListener(_ -> processLabCSV());
        topPanel.add(labCsvButton);
        
        JButton clearLabButton = new JButton("Clear File");
        customizeButton(clearLabButton);
        clearLabButton.addActionListener(_ -> {
            DefaultTableModel model = (DefaultTableModel) labCsvTable.getModel();
            model.setRowCount(0);
        });
        topPanel.add(clearLabButton);
        
        csvPanel.add(topPanel, BorderLayout.NORTH);
        
        labCsvTable = new JTable(new DefaultTableModel(new Object[]{
            "Name", "Absences", "MP1", "MP2", "MP3", "MP3 (Docu)",
            "Java 1", "Java 2", "JS 1", "JS 2", "Prelim Grade"
        }, 0));
        customizeTable(labCsvTable);
        JScrollPane scroll = new JScrollPane(labCsvTable);
        csvPanel.add(scroll, BorderLayout.CENTER);
        return csvPanel;
    }
    
    // ================== Lecture Panel ==================
    private JPanel createLecturePanel() {
        JPanel pinkPanel = new JPanel(new BorderLayout());
        pinkPanel.setBackground(new Color(175, 238, 238));
        pinkPanel.setOpaque(true);
        pinkPanel.setBorder(new LineBorder(new Color(0, 128, 128), 3));

        JPanel modeSelectorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        modeSelectorPanel.setOpaque(false);
        JLabel modeLabel = new JLabel("How do you want to input LECTURE grades?");
        JButton manualButton = new JButton("Manual Entry");
        JButton csvButton = new JButton("Upload File");
        customizeButton(manualButton);
        customizeButton(csvButton);
        modeSelectorPanel.add(modeLabel);
        modeSelectorPanel.add(manualButton);
        modeSelectorPanel.add(csvButton);
        pinkPanel.add(modeSelectorPanel, BorderLayout.NORTH);

        lecModeLayout = new CardLayout();
        lecModePanel = new JPanel(lecModeLayout);
        lecModePanel.setOpaque(false);
        
        lecBlankPanel = new JPanel();
        lecBlankPanel.setOpaque(false);

        lecManualPanel = buildLecManualPanel();
        lecCsvPanelWrapper = buildLecCsvPanel();

        lecModePanel.add(lecBlankPanel, "Blank");
        lecModePanel.add(lecManualPanel, "Manual");
        lecModePanel.add(lecCsvPanelWrapper, "CSV");

        lecModeLayout.show(lecModePanel, "Manual");
        pinkPanel.add(lecModePanel, BorderLayout.CENTER);

        manualButton.addActionListener(_ -> lecModeLayout.show(lecModePanel, "Manual"));
        csvButton.addActionListener(_ -> lecModeLayout.show(lecModePanel, "CSV"));

        return pinkPanel;
    }

    private JPanel buildLecManualPanel() {
        JPanel manualPanel = new JPanel(new BorderLayout());
        manualPanel.setBackground(new Color(175, 238, 238));
        manualPanel.setOpaque(true);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(true);
        inputPanel.setBackground(new Color(175, 238, 238));

        JPanel attendancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attendancePanel.setOpaque(false);
        JLabel attendanceLabel = new JLabel("Attendance (Check if present): ");
        attendancePanel.add(attendanceLabel);
        String[] dates = {"Jan 23", "Jan 30", "Feb 6", "Feb 13", "Feb 20"};
        lecAttendanceCheckboxes = new JCheckBox[dates.length];
        for (int i = 0; i < dates.length; i++) {
            lecAttendanceCheckboxes[i] = new JCheckBox(dates[i]);
            lecAttendanceCheckboxes[i].setOpaque(false);
            attendancePanel.add(lecAttendanceCheckboxes[i]);
        }        
        inputPanel.add(attendancePanel);
        
        inputPanel.add(createCenteredField("Essay Score:  ", essayField = new JTextField(10)));
        inputPanel.add(createCenteredField("PVM Score:    ", pvmField = new JTextField(10)));
        inputPanel.add(createCenteredField("Java Basics:  ", javaBasicsField = new JTextField(10)));
        inputPanel.add(createCenteredField("Intro to JS:    ", introJsField = new JTextField(10)));
        inputPanel.add(createCenteredField("Prelim Exam:  ", prelimExamField = new JTextField(10)));
        
        JButton calculateButton = new JButton("Compute Grade");
        customizeButton(calculateButton);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonRow.setOpaque(false);
        buttonRow.add(calculateButton);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(buttonRow);
        
        lecResultLabel = new JLabel("Final Prelim Grade: --");
        lecResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(lecResultLabel);
        
        calculateButton.addActionListener(this::computeLecGrade);
        
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        manualPanel.add(scrollPane, BorderLayout.CENTER);
        
        return manualPanel;
    }
    
    private JPanel buildLecCsvPanel() {
        JPanel csvPanel = new JPanel(new BorderLayout());
        csvPanel.setOpaque(true);
        csvPanel.setBackground(new Color(175, 238, 238));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setOpaque(false);
        
        JButton lecCsvButton = new JButton("Upload File");
        customizeButton(lecCsvButton);
        lecCsvButton.addActionListener(_ -> processLectureCSV());
        topPanel.add(lecCsvButton);
        
        JButton clearLecButton = new JButton("Clear File");
        customizeButton(clearLecButton);
        clearLecButton.addActionListener(_ -> {
            DefaultTableModel model = (DefaultTableModel) lectureCsvTable.getModel();
            model.setRowCount(0);
        });
        topPanel.add(clearLecButton);
        
        csvPanel.add(topPanel, BorderLayout.NORTH);
        
        lectureCsvTable = new JTable(new DefaultTableModel(new Object[]{
            "Name", "Absences", "Essay", "PVM", "Java Basics", "Intro to JS", "Exam Score", "Prelim Grade"
        }, 0));
        customizeTable(lectureCsvTable);
        JScrollPane scroll = new JScrollPane(lectureCsvTable);
        csvPanel.add(scroll, BorderLayout.CENTER);
        return csvPanel;
    }
    
    // ================== CSV UPLOAD METHODS ==================
    private void processLabCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if(option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                ArrayList<Object[]> rows = new ArrayList<>();
                boolean headerSkipped = false;
                while((line = br.readLine()) != null) {
                    line = line.trim();
                    if(line.isEmpty()) continue;
                    String[] cols = line.split(",");
                    if(!headerSkipped && cols[0].toLowerCase().contains("name")) {
                        headerSkipped = true;
                        continue;
                    }
                    if(cols.length < 10) continue;
                    String name = cols[0].trim();
                    int absences = Integer.parseInt(cols[1].trim());
                    double mp1 = Double.parseDouble(cols[2].trim());
                    double mp2 = Double.parseDouble(cols[3].trim());
                    double mp3 = Double.parseDouble(cols[4].trim());
                    double mp3Docu = Double.parseDouble(cols[5].trim());
                    double java1 = Double.parseDouble(cols[6].trim());
                    double java2 = Double.parseDouble(cols[7].trim());
                    double js1 = Double.parseDouble(cols[8].trim());
                    double js2 = Double.parseDouble(cols[9].trim());
                    
                    // Check for negative scores; if found, skip this row.
                    if(mp1 < 0 || mp2 < 0 || mp3 < 0 || mp3Docu < 0 ||
                       java1 < 0 || java2 < 0 || js1 < 0 || js2 < 0) {
                        continue;
                    }
                    
                    String finalGrade;
                    if(absences >= 3) {
                        finalGrade = "Failed (Excessive Absences)";
                    } else {
                        double examScore = (java1 * 0.2) + (java2 * 0.3) + (js1 * 0.2) + (js2 * 0.3);
                        double labWork = (mp1 + mp2 + mp3 + mp3Docu) / 4;
                        double attendanceScore = 100 - (absences * 10);
                        double classStanding = (labWork * 0.6) + (attendanceScore * 0.4);
                        double prelimGradeCalc = (examScore * 0.6) + (classStanding * 0.4);
                        finalGrade = String.format("%.2f", prelimGradeCalc);
                    }
                    rows.add(new Object[]{name, absences, mp1, mp2, mp3, mp3Docu, java1, java2, js1, js2, finalGrade});
                }
                DefaultTableModel model = (DefaultTableModel) labCsvTable.getModel();
                model.setRowCount(0);
                for(Object[] row : rows) {
                    model.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        }
    }

    private void processLectureCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if(option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                ArrayList<Object[]> rows = new ArrayList<>();
                boolean headerSkipped = false;
                while((line = br.readLine()) != null) {
                    line = line.trim();
                    if(line.isEmpty()) continue;
                    String[] cols = line.split(",");
                    if(!headerSkipped && cols[0].toLowerCase().contains("name")) {
                        headerSkipped = true;
                        continue;
                    }
                    if(cols.length < 7) continue;
                    String name = cols[0].trim();
                    int absences = Integer.parseInt(cols[1].trim());
                    double essay = Double.parseDouble(cols[2].trim());
                    double pvm = Double.parseDouble(cols[3].trim());
                    double javaBasics = Double.parseDouble(cols[4].trim());
                    double introJs = Double.parseDouble(cols[5].trim());
                    double examScore = Double.parseDouble(cols[6].trim());
                    
                    // Check for negative scores; if any are negative, skip the row.
                    if(essay < 0 || pvm < 0 || javaBasics < 0 || introJs < 0 || examScore < 0) {
                        continue;
                    }
                    
                    String finalGrade;
                    if(absences >= 3) {
                        finalGrade = "Failed (Excessive Absences)";
                    } else {
                        double essayPct = essay;
                        double pvmPct = (pvm / 60.0) * 100.0;
                        double javaBasicsPct = (javaBasics / 40.0) * 100.0;
                        double introJsPct = (introJs / 40.0) * 100.0;
                        double quizAvg = (essayPct + pvmPct + javaBasicsPct + introJsPct) / 4;
                        double attendanceScore = 100 - (absences * 10);
                        double classStanding = (quizAvg * 0.6) + (attendanceScore * 0.4);
                        double prelimGradeCalc = (examScore * 0.6) + (classStanding * 0.4);
                        finalGrade = String.format("%.2f", prelimGradeCalc);
                    }
                    rows.add(new Object[]{name, absences, essay, pvm, javaBasics, introJs, examScore, finalGrade});
                }
                DefaultTableModel model = (DefaultTableModel) lectureCsvTable.getModel();
                model.setRowCount(0);
                for(Object[] row : rows) {
                    model.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        }
    }
    
    // ================== Manual Grade Computation ==================
    private void computeLabGrade(ActionEvent e) {
        try {
            // Parse Laboratory component inputs
            double mp1 = Double.parseDouble(mp1Field.getText());
            double mp2 = Double.parseDouble(mp2Field.getText());
            double mp3 = Double.parseDouble(mp3Field.getText());
            double mp4 = Double.parseDouble(mp4Field.getText());
            // Parse Laboratory Exam inputs
            double java1 = Double.parseDouble(java1Field.getText());
            double java2 = Double.parseDouble(java2Field.getText());
            double js1 = Double.parseDouble(js1Field.getText());
            double js2 = Double.parseDouble(js2Field.getText());
            
            // Check for invalid ranges: negative values and values greater than 100
            if (mp1 < 0 || mp2 < 0 || mp3 < 0 || mp4 < 0 ||
                java1 < 0 || java2 < 0 || js1 < 0 || js2 < 0) {
                JOptionPane.showMessageDialog(this, "Invalid Input! Scores cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (mp1 > 100 || mp2 > 100 || mp3 > 100 || mp4 > 100 ||
                java1 > 100 || java2 > 100 || js1 > 100 || js2 > 100) {
                JOptionPane.showMessageDialog(this, "Invalid Input! You can only input up to 100 for each score.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double labWork = (mp1 + mp2 + mp3 + mp4) / 4;

            int totalClasses = labAttendanceCheckboxes.length;
            int attendedClasses = 0;
            for (JCheckBox cb : labAttendanceCheckboxes) {
                if (cb.isSelected()) attendedClasses++;
            }
            int absences = totalClasses - attendedClasses;
            double attendance = Math.max(0, 100 - (absences * 10));

            double examScore = (java1 * 0.20) + (java2 * 0.30) + (js1 * 0.20) + (js2 * 0.30);

            double prelimClassStanding = (labWork * 0.60) + (attendance * 0.40);
            double prelimGrade = (examScore * 0.60) + (prelimClassStanding * 0.40);

            String breakdown = String.format(
                "<html><div style='font-family: Segoe UI Emoji; font-size: 14px;'>" +
                "<h2 style='text-align: center;'>Grade Breakdown</h2>" +
                "<b>Prelim Grade</b> = 60%% Prelim Exam + 40%% Prelim Class Standing<br>" +
                "<b>Prelim Exam</b> = 20%% Java 1 + 30%% Java 2 + 20%% JS 1 + 30%% JS 2<br>" +
                "<b>Prelim Class Standing</b> = 60%% Lab Work + 40%% Attendance<br>" +
                "<b>Attendance</b> = 100%% default - 10%% per occurred absence<br><br>" +
                "<b>Lab Work Average:</b> %.2f<br>" +
                "<b>Attendance:</b> %.2f<br>" +
                "<b>Prelim Class Standing:</b> %.2f<br>" +
                "<b>Exam Score:</b> %.2f<br>" +
                "<b>Final Prelim Grade:</b> %.2f" +
                "</div></html>",
                labWork, attendance, prelimClassStanding, examScore, prelimGrade
            );

            JOptionPane.showMessageDialog(this, breakdown, "Grade Computation", JOptionPane.INFORMATION_MESSAGE);
            labResultLabel.setText(String.format("Final Prelim Grade: %.2f", prelimGrade));

            if (absences < 3 && prelimGrade >= 75) {
                showResultAnimation("Congratulations! You Passed!", successGifPath, JOptionPane.INFORMATION_MESSAGE);
            } else {
                showResultAnimation("Sorry, you have failed.", failureGifPath, JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input! Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void computeLecGrade(ActionEvent e) {
        try {
            double essay = Double.parseDouble(essayField.getText().trim());
            double pvm = Double.parseDouble(pvmField.getText().trim());
            double javaBasics = Double.parseDouble(javaBasicsField.getText().trim());
            double introJs = Double.parseDouble(introJsField.getText().trim());
            double prelimExam = Double.parseDouble(prelimExamField.getText().trim());
            
            // Validate that no score is negative
            if (essay < 0 || pvm < 0 || javaBasics < 0 || introJs < 0 || prelimExam < 0) {
                JOptionPane.showMessageDialog(this, "Invalid Input! Scores cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate maximum values (with pvm, javaBasics, and introJs having their own limits)
            StringBuilder errors = new StringBuilder();
            if (pvm > 60) {
                errors.append("PVM score must be 60 or less.\n");
            }
            if (javaBasics > 40) {
                errors.append("Java Basics score must be 40 or less.\n");
            }
            if (introJs > 40) {
                errors.append("Intro to JS score must be 40 or less.\n");
            }
            if (essay > 100 || prelimExam > 100) {
                errors.append("Essay and Prelim Exam scores must be 100 or less.\n");
            }
            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this, errors.toString(), "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double essayPct = essay; // already out of 100
            double pvmPct = (pvm / 60.0) * 100.0;
            double javaBasicsPct = (javaBasics / 40.0) * 100.0;
            double introJsPct = (introJs / 40.0) * 100.0;
            
            double prelimQuizzes = (essayPct + pvmPct + javaBasicsPct + introJsPct) / 4;
            
            int totalClasses = lecAttendanceCheckboxes.length;
            int attendedClasses = 0;
            for (JCheckBox cb : lecAttendanceCheckboxes) {
                if (cb.isSelected()) attendedClasses++;
            }
            int absences = totalClasses - attendedClasses;
            double attendance = Math.max(0, 100 - (absences * 10));
            
            double classStanding = (prelimQuizzes * 0.60) + (attendance * 0.40);
            double prelimGrade = (classStanding * 0.40) + (prelimExam * 0.60);
            
            String breakdown = String.format(
                "<html><div style='font-family: Segoe UI Emoji; font-size: 14px;'>"+
                "<h2 style='text-align: center;'>Grade Breakdown</h2>" +
                "<b>Quiz Breakdown:</b><br>" +
                "Essay (%%): %.2f, PVM (%%): %.2f, Java Basics (%%): %.2f, Intro to JS (%%): %.2f<br>" +
                "<b>Attendance</b> = 100%% default - 10%% per occurred absence<br><br>" +
                "<b>Weighted Quiz Score:</b> %.2f<br>" +
                "<b>Attendance:</b> %.2f<br>" +
                "<b>Class Standing:</b> %.2f<br>" +
                "<b>Prelim Exam:</b> %.2f<br>" +
                "<b>Final Prelim Grade:</b> %.2f" +
                "</div></html>",
                essayPct, pvmPct, javaBasicsPct, introJsPct,
                prelimQuizzes, attendance, classStanding, prelimExam, prelimGrade
            );
            
            JOptionPane.showMessageDialog(this, breakdown, "Grade Computation Breakdown", JOptionPane.INFORMATION_MESSAGE);
            lecResultLabel.setText(String.format("Final Prelim Grade: %.2f", prelimGrade));
            
            if (absences < 3 && prelimGrade >= 75) {
                showResultAnimation("Congratulations! You Passed!", successGifPath, JOptionPane.INFORMATION_MESSAGE);
            } else {
                showResultAnimation("Sorry, you have failed.", failureGifPath, JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input! Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a row that is center-aligned with a label and text field.
     */
    private JPanel createCenteredField(String labelText, JTextField textField) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rowPanel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        rowPanel.add(label);

        textField.setPreferredSize(new Dimension(200, 30));
        textField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        rowPanel.add(textField);

        return rowPanel;
    }

    // ================== Utility method: Customize button ==================
    private void customizeButton(JButton button) {
        button.setBackground(new Color(72, 209, 204));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
    }

    // ================== Utility method: Customize table appearance ==================
    private void customizeTable(JTable table) {
        table.setRowHeight(25);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        header.setBackground(new Color(150, 230, 230));
        header.setForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
    }

    // ================== Show Result Animation ==================
    private void showResultAnimation(String message, String gifPath, int messageType) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel gifLabel = new JLabel(new ImageIcon(gifPath));
        gifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel messageLabel = new JLabel(message);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        panel.add(gifLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(messageLabel);
        JOptionPane.showMessageDialog(this, panel, "Result", messageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GradeCalculator::new);
    }
}
