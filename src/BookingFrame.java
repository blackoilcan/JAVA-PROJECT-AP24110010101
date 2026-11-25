import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * BookingFrame - Modern ticket booking window with balanced 2x2 grid alignment,
 * dynamic live fare calculation, train overview summary card, and transaction-safe booking commit.
 */
public class BookingFrame extends JFrame {
    private final int scheduleId;
    private final String trainName;
    private final double fare;
    private final int availableSeats;

    private ModernTextField txtName, txtAge, txtSeats;
    private JComboBox<String> cbGender;
    private JLabel lblTotalFare, lblFareCalculation;
    private ModernButton btnConfirm, btnBack;

    private static final DecimalFormat CURRENCY_FMT = new DecimalFormat("₹ #,##0.00");

    public BookingFrame(int scheduleId, String trainName, double fare, int availableSeats) {
        this.scheduleId = scheduleId;
        this.trainName = trainName;
        this.fare = fare;
        this.availableSeats = availableSeats;

        setTitle("RailConnect - Confirm Reservation (" + trainName + ")");
        setSize(780, 600);
        setMinimumSize(new Dimension(680, 540));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);

        initUI();
        updateFareCalculation();
    }

    public BookingFrame() {
        this(1, "12952 - Rajdhani Express", 2150.00, 98);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Top Navigation Header
        AppHeader header = new AppHeader("Confirm Booking", null);
        add(header, BorderLayout.NORTH);

        // Center Content Wrapper
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 32, 20, 32));

        // 1. Train Overview Card (North)
        JPanel trainCard = UITheme.createCardPanel(18, UITheme.PRIMARY_LIGHT);
        trainCard.setLayout(new BorderLayout(16, 0));

        // Train Icon + Text
        JPanel trainLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        trainLeft.setOpaque(false);

        JLabel trainIcon = new JLabel(UITheme.getIcon("train", 40, UITheme.PRIMARY));
        trainLeft.add(trainIcon);

        JPanel trainDetails = new JPanel(new GridLayout(2, 1, 0, 4));
        trainDetails.setOpaque(false);

        JLabel lblName = new JLabel(trainName);
        lblName.setFont(UITheme.FONT_TITLE);
        lblName.setForeground(UITheme.PRIMARY);

        JLabel lblMeta = new JLabel("Schedule #" + scheduleId + "  •  " + availableSeats + " seats available in inventory");
        lblMeta.setFont(UITheme.FONT_BODY_BOLD);
        lblMeta.setForeground(UITheme.TEXT_SECONDARY);

        trainDetails.add(lblName);
        trainDetails.add(lblMeta);
        trainLeft.add(trainDetails);
        trainCard.add(trainLeft, BorderLayout.WEST);

        // Fare Chip on Right
        JPanel fareChip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        fareChip.setOpaque(false);
        JLabel lblBaseFare = new JLabel("Base Fare: " + CURRENCY_FMT.format(fare) + " / seat");
        lblBaseFare.setFont(UITheme.FONT_BODY_BOLD);
        lblBaseFare.setForeground(UITheme.PRIMARY);
        lblBaseFare.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        fareChip.add(lblBaseFare);
        trainCard.add(fareChip, BorderLayout.EAST);

        mainContent.add(trainCard, BorderLayout.NORTH);

        // 2. Passenger Input Form Card (Center)
        JPanel formCard = UITheme.createCardPanel(24);
        formCard.setLayout(new BorderLayout(0, 18));

        // Form Title
        JPanel formHeader = new JPanel(new GridLayout(2, 1, 0, 3));
        formHeader.setOpaque(false);

        JLabel lblFormTitle = new JLabel("Passenger Information & Seat Reservation");
        lblFormTitle.setFont(UITheme.FONT_SUBTITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblFormSub = new JLabel("Please enter accurate traveler details for ticketing and validation");
        lblFormSub.setFont(UITheme.FONT_SMALL);
        lblFormSub.setForeground(UITheme.TEXT_SECONDARY);

        formHeader.add(lblFormTitle);
        formHeader.add(lblFormSub);
        formCard.add(formHeader, BorderLayout.NORTH);

        // Form Grid: Symmetrical 2 rows x 2 columns
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 20, 16));
        fieldsPanel.setOpaque(false);

        // Row 1, Col 1: Passenger Name
        String defaultName = Session.username != null ? Session.username : "";
        txtName = new ModernTextField(defaultName, 15);
        txtName.setPlaceholder("e.g. John Doe");
        fieldsPanel.add(createFieldGroup("Passenger Full Name", txtName));

        // Row 1, Col 2: Passenger Age
        txtAge = new ModernTextField("28", 10);
        txtAge.setPlaceholder("e.g. 28");
        fieldsPanel.add(createFieldGroup("Passenger Age", txtAge));

        // Row 2, Col 1: Gender Dropdown
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setFont(UITheme.FONT_BODY);
        cbGender.setBackground(Color.WHITE);
        cbGender.setPreferredSize(new Dimension(cbGender.getPreferredSize().width, 38));
        fieldsPanel.add(createFieldGroup("Gender", cbGender));

        // Row 2, Col 2: Number of Seats
        txtSeats = new ModernTextField("1", 10);
        txtSeats.setPlaceholder("1");
        fieldsPanel.add(createFieldGroup("Number of Seats to Book", txtSeats));

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // 3. Live Fare Calculation Box (South inside form card)
        JPanel fareBox = new JPanel(new BorderLayout(16, 0));
        fareBox.setBackground(new Color(248, 250, 252));
        fareBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(14, 20, 14, 20)
        ));

        // Left breakdown
        JPanel fareBreakdownPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        fareBreakdownPanel.setOpaque(false);

        JLabel lblCalcHeader = new JLabel("FARE CALCULATION");
        lblCalcHeader.setFont(UITheme.FONT_SMALL_BOLD);
        lblCalcHeader.setForeground(UITheme.TEXT_MUTED);

        lblFareCalculation = new JLabel("1 seat(s) × " + CURRENCY_FMT.format(fare));
        lblFareCalculation.setFont(UITheme.FONT_BODY_BOLD);
        lblFareCalculation.setForeground(UITheme.TEXT_SECONDARY);

        fareBreakdownPanel.add(lblCalcHeader);
        fareBreakdownPanel.add(lblFareCalculation);
        fareBox.add(fareBreakdownPanel, BorderLayout.WEST);

        // Right Total
        JPanel fareTotalPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        fareTotalPanel.setOpaque(false);

        JLabel lblTotalHeader = new JLabel("TOTAL AMOUNT PAYABLE", SwingConstants.RIGHT);
        lblTotalHeader.setFont(UITheme.FONT_SMALL_BOLD);
        lblTotalHeader.setForeground(UITheme.TEXT_MUTED);

        lblTotalFare = new JLabel("Total: " + CURRENCY_FMT.format(fare), SwingConstants.RIGHT);
        lblTotalFare.setFont(UITheme.FONT_TITLE);
        lblTotalFare.setForeground(UITheme.PRIMARY);

        fareTotalPanel.add(lblTotalHeader);
        fareTotalPanel.add(lblTotalFare);
        fareBox.add(fareTotalPanel, BorderLayout.EAST);

        formCard.add(fareBox, BorderLayout.SOUTH);
        mainContent.add(formCard, BorderLayout.CENTER);

        // 4. Action Buttons (South)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        actionPanel.setOpaque(false);

        btnBack = new ModernButton("Back to Search", ModernButton.Style.SECONDARY);
        btnBack.setPreferredSize(new Dimension(150, 42));

        btnConfirm = new ModernButton("Confirm Booking", UITheme.getIcon("check", 16, Color.WHITE), ModernButton.Style.SUCCESS);
        btnConfirm.setPreferredSize(new Dimension(190, 42));

        actionPanel.add(btnBack);
        actionPanel.add(btnConfirm);
        mainContent.add(actionPanel, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        // Document listener on seats to update fare in real time
        txtSeats.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateFareCalculation(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateFareCalculation(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateFareCalculation(); }
        });

        // Button Actions
        btnConfirm.addActionListener(e -> performBooking());
        btnBack.addActionListener(e -> {
            new TrainSearchFrame().setVisible(true);
            dispose();
        });
    }

    private JPanel createFieldGroup(String labelText, JComponent input) {
        JPanel group = new JPanel(new BorderLayout(0, 6));
        group.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);

        group.add(lbl, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }

    private void updateFareCalculation() {
        String seatStr = txtSeats.getText().trim();
        try {
            int seats = Integer.parseInt(seatStr);
            if (seats > 0) {
                double total = seats * fare;
                lblFareCalculation.setText(seats + " seat(s) × " + CURRENCY_FMT.format(fare));
                lblTotalFare.setText(CURRENCY_FMT.format(total));
                return;
            }
        } catch (Exception ignored) {}

        lblFareCalculation.setText("Enter valid seat count");
        lblTotalFare.setText("₹ 0.00");
    }

    private void performBooking() {
        String name = txtName.getText().trim();
        String ageStr = txtAge.getText().trim();
        String gender = (String) cbGender.getSelectedItem();
        String seatStr = txtSeats.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty() || seatStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all passenger details.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int age;
        int seatsToBook;
        try {
            age = Integer.parseInt(ageStr);
            seatsToBook = Integer.parseInt(seatStr);
            if (seatsToBook <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Please enter at least 1 seat to book.",
                        "Invalid Seat Count",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Age and Seats must be valid numeric numbers.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (seatsToBook > availableSeats) {
            JOptionPane.showMessageDialog(this,
                    "Only " + availableSeats + " seats are available for this train.\nPlease reduce the seat count.",
                    "Insufficient Seats",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String pnr = "PNR" + (1000 + new Random().nextInt(9000));
        int userId = Session.userId > 0 ? Session.userId : 2;
        double totalCost = seatsToBook * fare;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // start transaction

            // 1. Insert booking with accurate gender
            String sql1 = "INSERT INTO Booking (pnr_number, user_id, schedule_id, passenger_name, passenger_age, passenger_gender, seats_booked, booking_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'CONFIRMED')";
            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                ps1.setString(1, pnr);
                ps1.setInt(2, userId);
                ps1.setInt(3, scheduleId);
                ps1.setString(4, name);
                ps1.setInt(5, age);
                ps1.setString(6, gender);
                ps1.setInt(7, seatsToBook);
                ps1.executeUpdate();
            }

            // 2. Reduce available seats in Schedule
            String sql2 = "UPDATE Schedule SET available_seats = available_seats - ? WHERE schedule_id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps2.setInt(1, seatsToBook);
                ps2.setInt(2, scheduleId);
                ps2.executeUpdate();
            }

            // Commit transaction
            conn.commit();

            JOptionPane.showMessageDialog(this,
                    "<html><body style='width: 280px;'>"
                            + "<h3 style='color: #059669; margin: 0;'>Booking Confirmed!</h3>"
                            + "<p>Your train reservation is successful.</p>"
                            + "<table cellpadding='3'>"
                            + "<tr><td><b>PNR Number:</b></td><td style='color:#1E40AF; font-size:14px; font-weight:bold;'>" + pnr + "</td></tr>"
                            + "<tr><td><b>Passenger:</b></td><td>" + name + " (" + gender + ", " + age + " yrs)</td></tr>"
                            + "<tr><td><b>Train:</b></td><td>" + trainName + "</td></tr>"
                            + "<tr><td><b>Seats Booked:</b></td><td>" + seatsToBook + "</td></tr>"
                            + "<tr><td><b>Total Fare:</b></td><td>" + CURRENCY_FMT.format(totalCost) + "</td></tr>"
                            + "</table>"
                            + "</body></html>",
                    "Ticket Reserved",
                    JOptionPane.INFORMATION_MESSAGE);

            new MyBookingsFrame().setVisible(true);
            dispose();

        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rex) {
                    rex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Booking failed due to an error: " + ex.getMessage(),
                    "Transaction Failed",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception cex) {
                    cex.printStackTrace();
                }
            }
        }
    }
}
