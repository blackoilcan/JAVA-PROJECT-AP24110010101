import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * TrainSearchFrame - Modern, responsive train schedule search and booking interface.
 */
public class TrainSearchFrame extends JFrame {
    private ModernTextField txtFrom, txtTo;
    private ModernButton btnSearch, btnClear, btnSwap, btnBook;
    private ModernTable table;
    private DefaultTableModel model;
    private JLabel lblResultCount;
    private JLabel lblSelectedTrainInfo;

    public TrainSearchFrame() {
        setTitle("RailConnect - Train Search & Reservation");
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_PAGE);

        initUI();
        loadTrains("", "");
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- TOP NAVIGATION HEADER ---
        AppHeader header = new AppHeader("Train Search", () -> {
            Session.logout();
            new LoginFrame().setVisible(true);
            dispose();
        });

        // "My Bookings" button in header
        ModernButton btnMyBookings = new ModernButton("My Bookings", UITheme.getIcon("ticket", 14, UITheme.PRIMARY), ModernButton.Style.OUTLINE);
        btnMyBookings.setFont(UITheme.FONT_SMALL_BOLD);
        btnMyBookings.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnMyBookings.addActionListener(e -> {
            new MyBookingsFrame().setVisible(true);
            dispose();
        });
        header.addActionButton(btnMyBookings);

        add(header, BorderLayout.NORTH);

        // --- CENTER MAIN CONTAINER ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(16, 24, 16, 24));

        // 1. Search Bar Card
        JPanel searchCard = UITheme.createCardPanel(16);
        searchCard.setLayout(new BorderLayout(0, 12));

        // Search Header
        JPanel searchHeader = new JPanel(new BorderLayout());
        searchHeader.setOpaque(false);
        JLabel lblSearchTitle = new JLabel("Search Available Trains");
        lblSearchTitle.setFont(UITheme.FONT_SUBTITLE);
        lblSearchTitle.setForeground(UITheme.TEXT_DARK);
        JLabel lblSearchSub = new JLabel("Enter source and destination stations to check live schedules");
        lblSearchSub.setFont(UITheme.FONT_SMALL);
        lblSearchSub.setForeground(UITheme.TEXT_SECONDARY);
        searchHeader.add(lblSearchTitle, BorderLayout.NORTH);
        searchHeader.add(lblSearchSub, BorderLayout.SOUTH);
        searchCard.add(searchHeader, BorderLayout.NORTH);

        // Search Controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        controlsPanel.setOpaque(false);

        JLabel lblFrom = new JLabel("From:");
        lblFrom.setFont(UITheme.FONT_BODY_BOLD);
        txtFrom = new ModernTextField("e.g. New Delhi");
        txtFrom.setPreferredSize(new Dimension(180, 38));

        btnSwap = new ModernButton("", UITheme.getIcon("swap", 16, UITheme.TEXT_SECONDARY), ModernButton.Style.SECONDARY);
        btnSwap.setToolTipText("Swap Origin & Destination");
        btnSwap.setPreferredSize(new Dimension(38, 38));
        btnSwap.addActionListener(e -> {
            String temp = txtFrom.getText();
            txtFrom.setText(txtTo.getText());
            txtTo.setText(temp);
        });

        JLabel lblTo = new JLabel("To:");
        lblTo.setFont(UITheme.FONT_BODY_BOLD);
        txtTo = new ModernTextField("e.g. Mumbai CSMT");
        txtTo.setPreferredSize(new Dimension(180, 38));

        btnSearch = new ModernButton("Search Trains", UITheme.getIcon("search", 15, Color.WHITE), ModernButton.Style.PRIMARY);
        btnSearch.setPreferredSize(new Dimension(145, 38));

        btnClear = new ModernButton("View All", UITheme.getIcon("refresh", 14, UITheme.TEXT_DARK), ModernButton.Style.SECONDARY);
        btnClear.setPreferredSize(new Dimension(110, 38));

        controlsPanel.add(lblFrom);
        controlsPanel.add(txtFrom);
        controlsPanel.add(btnSwap);
        controlsPanel.add(lblTo);
        controlsPanel.add(txtTo);
        controlsPanel.add(btnSearch);
        controlsPanel.add(btnClear);

        // Quick station filter chips
        JPanel quickChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        quickChips.setOpaque(false);
        JLabel lblQuick = new JLabel("Popular Stations: ");
        lblQuick.setFont(UITheme.FONT_SMALL_BOLD);
        lblQuick.setForeground(UITheme.TEXT_MUTED);
        quickChips.add(lblQuick);

        String[] popular = {"Delhi", "Mumbai", "Howrah", "Chennai", "Bengaluru"};
        for (String city : popular) {
            JButton chip = new JButton(city);
            chip.setFont(UITheme.FONT_SMALL);
            chip.setForeground(UITheme.PRIMARY);
            chip.setBackground(UITheme.PRIMARY_LIGHT);
            chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
                    new EmptyBorder(3, 8, 3, 8)
            ));
            chip.setFocusPainted(false);
            chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
            chip.addActionListener(ev -> {
                txtFrom.setText(city);
                loadTrains(city, txtTo.getText().trim());
            });
            quickChips.add(chip);
        }

        JPanel searchBody = new JPanel(new BorderLayout(0, 8));
        searchBody.setOpaque(false);
        searchBody.add(controlsPanel, BorderLayout.CENTER);
        searchBody.add(quickChips, BorderLayout.SOUTH);
        searchCard.add(searchBody, BorderLayout.CENTER);

        mainContent.add(searchCard, BorderLayout.NORTH);

        // 2. Results Table Card
        JPanel tableCard = UITheme.createCardPanel(16);
        tableCard.setLayout(new BorderLayout(0, 10));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);

        JLabel lblTableTitle = new JLabel("Available Trains & Schedules");
        lblTableTitle.setFont(UITheme.FONT_SUBTITLE);
        lblTableTitle.setForeground(UITheme.TEXT_DARK);

        lblResultCount = new JLabel("0 trains found");
        lblResultCount.setFont(UITheme.FONT_BODY_BOLD);
        lblResultCount.setForeground(UITheme.PRIMARY);

        tableHeaderPanel.add(lblTableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(lblResultCount, BorderLayout.EAST);
        tableCard.add(tableHeaderPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"Schedule ID", "Train No", "Train Name", "From Station", "To Station", "Departure Date", "Time", "Seats Status", "Fare (INR)"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(model);
        table.setCenterAlignedColumn(0); // Schedule ID
        table.setCenterAlignedColumn(1); // Train No
        table.setCenterAlignedColumn(5); // Date
        table.setCenterAlignedColumn(6); // Time
        table.setSeatsBadgeColumn(7);    // Available Seats Pill Badge
        table.setCurrencyColumn(8);      // Fare formatted

        // Optimized column widths
        table.setColumnWidths(new int[]{80, 85, 160, 130, 130, 105, 85, 125, 100});

        tableCard.add(ModernTable.wrap(table), BorderLayout.CENTER);
        mainContent.add(tableCard, BorderLayout.CENTER);

        // 3. Bottom Action Bar Card
        JPanel bottomCard = UITheme.createCardPanel(14);
        bottomCard.setLayout(new BorderLayout(16, 0));

        lblSelectedTrainInfo = new JLabel("Select a train from the table above to book tickets");
        lblSelectedTrainInfo.setFont(UITheme.FONT_BODY);
        lblSelectedTrainInfo.setForeground(UITheme.TEXT_SECONDARY);

        btnBook = new ModernButton("Proceed to Book Ticket", UITheme.getIcon("ticket", 16, Color.WHITE), ModernButton.Style.PRIMARY);
        btnBook.setEnabled(false);
        btnBook.setPreferredSize(new Dimension(210, 40));

        bottomCard.add(lblSelectedTrainInfo, BorderLayout.WEST);
        bottomCard.add(btnBook, BorderLayout.EAST);

        mainContent.add(bottomCard, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        // --- LISTENERS ---
        btnSearch.addActionListener(e -> loadTrains(txtFrom.getText().trim(), txtTo.getText().trim()));
        btnClear.addActionListener(e -> {
            txtFrom.setText("");
            txtTo.setText("");
            loadTrains("", "");
        });

        // Table Selection changes
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectionState();
            }
        });

        // Double click on row books ticket
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    proceedToBooking();
                }
            }
        });

        btnBook.addActionListener(e -> proceedToBooking());
    }

    private void updateSelectionState() {
        int row = table.getSelectedRow();
        if (row != -1) {
            String trainNo = model.getValueAt(row, 1).toString();
            String trainName = model.getValueAt(row, 2).toString();
            String from = model.getValueAt(row, 3).toString();
            String to = model.getValueAt(row, 4).toString();
            String seats = model.getValueAt(row, 7).toString();
            String fare = model.getValueAt(row, 8).toString();

            lblSelectedTrainInfo.setText("<html>Selected: <b>" + trainNo + " - " + trainName + "</b> ("
                    + from + " ➔ " + to + ") | Fare: <b>₹ " + fare + "</b></html>");
            btnBook.setEnabled(true);
        } else {
            lblSelectedTrainInfo.setText("Select a train from the table above to book tickets");
            btnBook.setEnabled(false);
        }
    }

    private void proceedToBooking() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a train first.",
                    "No Train Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int scheduleId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String trainName = model.getValueAt(row, 2).toString();
        int seats = Integer.parseInt(model.getValueAt(row, 7).toString());
        double fare = Double.parseDouble(model.getValueAt(row, 8).toString());

        if (seats <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Sorry, this train has no available seats remaining.",
                    "Train Fully Booked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        new BookingFrame(scheduleId, trainName, fare, seats).setVisible(true);
        dispose();
    }

    private void loadTrains(String from, String to) {
        model.setRowCount(0);
        String sql = "SELECT s.schedule_id, t.train_number, t.train_name, src.station_name, dst.station_name, " +
                "s.departure_date, s.departure_time, s.available_seats, s.fare " +
                "FROM Schedule s " +
                "JOIN Train t ON s.train_id = t.train_id " +
                "JOIN Station src ON s.source_station_id = src.station_id " +
                "JOIN Station dst ON s.destination_station_id = dst.station_id " +
                "WHERE src.station_name ILIKE ? AND dst.station_name ILIKE ? " +
                "ORDER BY s.departure_date, s.departure_time";

        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + from + "%");
            ps.setString(2, "%" + to + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                count++;
                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getDate(6),
                        rs.getTime(7),
                        rs.getInt(8),
                        rs.getDouble(9)
                });
            }
            lblResultCount.setText(count + " trains found");
            updateSelectionState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading train schedules: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
