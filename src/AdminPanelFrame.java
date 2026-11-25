import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * AdminPanelFrame - Modern, professional administrative control center with KPI metric cards,
 * fleet management, booking monitor, and analytics reports.
 */
public class AdminPanelFrame extends JFrame {
    private ModernTextField txtNo, txtName, txtSeats;
    private ModernButton btnAdd, btnDelete, btnRefreshBookings, btnRefreshReport;
    private ModernTable trainsTable, bookingsTable, reportTable;
    private DefaultTableModel trainsModel, bookingsModel, reportModel;

    // KPI Stat labels
    private JLabel lblTotalTrains, lblTotalBookings, lblTotalSeatsBooked;

    public AdminPanelFrame() {
        setTitle("RailConnect - Administrator Command Center");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_PAGE);

        initUI();

        // Initial data load
        loadTrains();
        loadAllBookings();
        loadReport();
        loadKPIs();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        AppHeader header = new AppHeader("Admin Control Panel", () -> {
            Session.logout();
            new LoginFrame().setVisible(true);
            dispose();
        });
        add(header, BorderLayout.NORTH);

        // Center Wrapper
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(16, 24, 16, 24));

        // --- KPI STAT METRIC CARDS ---
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);

        lblTotalTrains = new JLabel("0");
        lblTotalBookings = new JLabel("0");
        lblTotalSeatsBooked = new JLabel("0");

        statsRow.add(createKPICard("Total Fleet Trains", lblTotalTrains, "train", UITheme.PRIMARY, "Active in system"));
        statsRow.add(createKPICard("Total Bookings", lblTotalBookings, "ticket", UITheme.ACCENT, "All passengers"));
        statsRow.add(createKPICard("Total Seats Booked", lblTotalSeatsBooked, "chart", UITheme.SUCCESS, "Cumulative volume"));

        mainContent.add(statsRow, BorderLayout.NORTH);

        // --- TABS SECTION ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY_BOLD);

        // TAB 1: Manage Trains
        JPanel trainPanel = new JPanel(new BorderLayout(0, 14));
        trainPanel.setOpaque(false);
        trainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Add Train Card
        JPanel addCard = UITheme.createCardPanel(16);
        addCard.setLayout(new BorderLayout(0, 10));

        JLabel lblAddTitle = new JLabel("Add New Train to Fleet");
        lblAddTitle.setFont(UITheme.FONT_SUBTITLE);
        lblAddTitle.setForeground(UITheme.TEXT_DARK);
        addCard.add(lblAddTitle, BorderLayout.NORTH);

        JPanel addInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        addInputs.setOpaque(false);

        JLabel lblNo = new JLabel("Train Number:");
        lblNo.setFont(UITheme.FONT_BODY_BOLD);
        txtNo = new ModernTextField("e.g. 12952");
        txtNo.setPreferredSize(new Dimension(140, 38));

        JLabel lblName = new JLabel("Train Name:");
        lblName.setFont(UITheme.FONT_BODY_BOLD);
        txtName = new ModernTextField("e.g. Rajdhani Express");
        txtName.setPreferredSize(new Dimension(200, 38));

        JLabel lblSeats = new JLabel("Total Capacity:");
        lblSeats.setFont(UITheme.FONT_BODY_BOLD);
        txtSeats = new ModernTextField("100");
        txtSeats.setPreferredSize(new Dimension(100, 38));

        btnAdd = new ModernButton("Add Train", UITheme.getIcon("plus", 14, Color.WHITE), ModernButton.Style.SUCCESS);
        btnAdd.setPreferredSize(new Dimension(130, 38));

        addInputs.add(lblNo);
        addInputs.add(txtNo);
        addInputs.add(lblName);
        addInputs.add(txtName);
        addInputs.add(lblSeats);
        addInputs.add(txtSeats);
        addInputs.add(btnAdd);

        addCard.add(addInputs, BorderLayout.CENTER);
        trainPanel.add(addCard, BorderLayout.NORTH);

        // Trains Table Card
        JPanel trainsTableCard = UITheme.createCardPanel(16);
        trainsTableCard.setLayout(new BorderLayout(0, 10));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel lblListTitle = new JLabel("Current Fleet Registry");
        lblListTitle.setFont(UITheme.FONT_SUBTITLE);
        btnDelete = new ModernButton("Delete Train", UITheme.getIcon("trash", 14, Color.WHITE), ModernButton.Style.DANGER);
        btnDelete.setPreferredSize(new Dimension(140, 36));

        tableHeader.add(lblListTitle, BorderLayout.WEST);
        tableHeader.add(btnDelete, BorderLayout.EAST);
        trainsTableCard.add(tableHeader, BorderLayout.NORTH);

        trainsModel = new DefaultTableModel(new String[]{"Train ID", "Train Number", "Train Name", "Total Seats Capacity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        trainsTable = new ModernTable(trainsModel);
        trainsTable.setCenterAlignedColumn(0);
        trainsTable.setCenterAlignedColumn(1);
        trainsTable.setCenterAlignedColumn(3);
        trainsTable.setColumnWidths(new int[]{90, 130, 250, 140});

        trainsTableCard.add(ModernTable.wrap(trainsTable), BorderLayout.CENTER);
        trainPanel.add(trainsTableCard, BorderLayout.CENTER);

        tabs.addTab("  Fleet Management  ", trainPanel);

        // TAB 2: All Bookings
        JPanel bookingsPanel = new JPanel(new BorderLayout(0, 14));
        bookingsPanel.setOpaque(false);
        bookingsPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel bookingsCard = UITheme.createCardPanel(16);
        bookingsCard.setLayout(new BorderLayout(0, 10));

        JPanel bookingsHeader = new JPanel(new BorderLayout());
        bookingsHeader.setOpaque(false);

        JLabel lblAllBookings = new JLabel("All System Bookings Across Users");
        lblAllBookings.setFont(UITheme.FONT_SUBTITLE);

        btnRefreshBookings = new ModernButton("Refresh Bookings", UITheme.getIcon("refresh", 14, UITheme.TEXT_DARK), ModernButton.Style.SECONDARY);
        btnRefreshBookings.setPreferredSize(new Dimension(170, 36));

        bookingsHeader.add(lblAllBookings, BorderLayout.WEST);
        bookingsHeader.add(btnRefreshBookings, BorderLayout.EAST);
        bookingsCard.add(bookingsHeader, BorderLayout.NORTH);

        bookingsModel = new DefaultTableModel(new String[]{"ID", "PNR", "User", "Passenger", "Train", "From", "To", "Seats", "Total Fare", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingsTable = new ModernTable(bookingsModel);
        bookingsTable.setCenterAlignedColumn(0);
        bookingsTable.setCenterAlignedColumn(1);
        bookingsTable.setCenterAlignedColumn(7);
        bookingsTable.setCurrencyColumn(8);
        bookingsTable.setStatusBadgeColumn(9);
        bookingsTable.setColumnWidths(new int[]{60, 90, 90, 110, 130, 100, 100, 60, 100, 110});

        bookingsCard.add(ModernTable.wrap(bookingsTable), BorderLayout.CENTER);
        bookingsPanel.add(bookingsCard, BorderLayout.CENTER);

        tabs.addTab("  All Passenger Bookings  ", bookingsPanel);

        // TAB 3: Report (Bookings per train)
        JPanel reportPanel = new JPanel(new BorderLayout(0, 14));
        reportPanel.setOpaque(false);
        reportPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel reportCard = UITheme.createCardPanel(16);
        reportCard.setLayout(new BorderLayout(0, 10));

        JPanel reportHeader = new JPanel(new BorderLayout());
        reportHeader.setOpaque(false);

        JLabel lblReportTitle = new JLabel("Train Performance Analytics (Grouped Aggregation)");
        lblReportTitle.setFont(UITheme.FONT_SUBTITLE);

        btnRefreshReport = new ModernButton("Refresh Report", UITheme.getIcon("refresh", 14, UITheme.TEXT_DARK), ModernButton.Style.SECONDARY);
        btnRefreshReport.setPreferredSize(new Dimension(160, 36));

        reportHeader.add(lblReportTitle, BorderLayout.WEST);
        reportHeader.add(btnRefreshReport, BorderLayout.EAST);
        reportCard.add(reportHeader, BorderLayout.NORTH);

        reportModel = new DefaultTableModel(new String[]{"Train Number", "Train Name", "Total Bookings Count", "Total Seats Booked"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reportTable = new ModernTable(reportModel);
        reportTable.setCenterAlignedColumn(0);
        reportTable.setCenterAlignedColumn(2);
        reportTable.setCenterAlignedColumn(3);
        reportTable.setColumnWidths(new int[]{120, 220, 150, 150});

        reportCard.add(ModernTable.wrap(reportTable), BorderLayout.CENTER);
        reportPanel.add(reportCard, BorderLayout.CENTER);

        tabs.addTab("  Analytics & Reports  ", reportPanel);

        mainContent.add(tabs, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // --- BUTTON ACTIONS ---
        btnAdd.addActionListener(e -> performAddTrain());
        btnDelete.addActionListener(e -> performDeleteTrain());
        btnRefreshBookings.addActionListener(e -> {
            loadAllBookings();
            loadKPIs();
        });
        btnRefreshReport.addActionListener(e -> {
            loadReport();
            loadKPIs();
        });
    }

    private JPanel createKPICard(String title, JLabel valueLabel, String iconName, Color accentColor, String subtext) {
        JPanel card = UITheme.createCardPanel(16);
        card.setLayout(new BorderLayout(12, 0));

        JLabel iconLabel = new JLabel(UITheme.getIcon(iconName, 32, accentColor));
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SMALL_BOLD);
        lblTitle.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(UITheme.FONT_HERO);
        valueLabel.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        textPanel.add(lblTitle);
        textPanel.add(valueLabel);
        textPanel.add(lblSub);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void performAddTrain() {
        String no = txtNo.getText().trim();
        String name = txtName.getText().trim();
        String seats = txtSeats.getText().trim();

        if (no.isEmpty() || name.isEmpty() || seats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all train fields (Train Number, Name, Seats).",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int seatCount;
        try {
            seatCount = Integer.parseInt(seats);
            if (seatCount <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Seat capacity must be greater than zero.",
                        "Invalid Capacity",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Total seats must be a valid integer number.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Train (train_number, train_name, total_seats) VALUES (?, ?, ?)")) {
            ps.setString(1, no);
            ps.setString(2, name);
            ps.setInt(3, seatCount);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Train '" + name + "' (#" + no + ") successfully added to fleet!",
                    "Train Added",
                    JOptionPane.INFORMATION_MESSAGE);

            txtNo.setText("");
            txtName.setText("");
            txtSeats.setText("");
            loadTrains();
            loadKPIs();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding train: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDeleteTrain() {
        int row = trainsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a train from the fleet registry to delete.",
                    "No Train Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int trainId = Integer.parseInt(trainsModel.getValueAt(row, 0).toString());
        String trainNo = trainsModel.getValueAt(row, 1).toString();
        String trainName = trainsModel.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete train '" + trainName + "' (#" + trainNo + ")?\nAll associated schedules and bookings will be removed.",
                "Confirm Fleet Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Train WHERE train_id = ?")) {
            ps.setInt(1, trainId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Train deleted successfully from fleet.",
                    "Train Deleted",
                    JOptionPane.INFORMATION_MESSAGE);

            loadTrains();
            loadAllBookings();
            loadReport();
            loadKPIs();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting train: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTrains() {
        trainsModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT train_id, train_number, train_name, total_seats FROM Train ORDER BY train_id ASC")) {
            while (rs.next()) {
                trainsModel.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading trains: " + ex.getMessage());
        }
    }

    private void loadAllBookings() {
        bookingsModel.setRowCount(0);
        String sql = "SELECT booking_id, pnr_number, username, passenger_name, train_name, source_station, destination_station, seats_booked, total_fare, booking_status " +
                "FROM booking_summary_view ORDER BY booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                bookingsModel.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getString(10)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading all bookings: " + ex.getMessage());
        }
    }

    private void loadReport() {
        reportModel.setRowCount(0);
        String sql = "SELECT t.train_number, t.train_name, COUNT(b.booking_id) AS total_bookings, " +
                "COALESCE(SUM(b.seats_booked), 0) AS total_seats " +
                "FROM Train t " +
                "LEFT JOIN Schedule s ON t.train_id = s.train_id " +
                "LEFT JOIN Booking b ON s.schedule_id = b.schedule_id " +
                "GROUP BY t.train_id, t.train_number, t.train_name " +
                "ORDER BY total_bookings DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                reportModel.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading report: " + ex.getMessage());
        }
    }

    private void loadKPIs() {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            // 1. Train count
            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM Train");
            if (rs1.next()) {
                lblTotalTrains.setText(String.valueOf(rs1.getInt(1)));
            }

            // 2. Booking count
            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM Booking");
            if (rs2.next()) {
                lblTotalBookings.setText(String.valueOf(rs2.getInt(1)));
            }

            // 3. Seats booked sum
            ResultSet rs3 = st.executeQuery("SELECT COALESCE(SUM(seats_booked), 0) FROM Booking WHERE booking_status = 'CONFIRMED'");
            if (rs3.next()) {
                lblTotalSeatsBooked.setText(String.valueOf(rs3.getInt(1)));
            }
        } catch (Exception ignored) {}
    }
}
