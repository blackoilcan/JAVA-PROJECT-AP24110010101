import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * MyBookingsFrame - Modern booking history interface with status badges,
 * instant ticket cancellation (with seat restoration trigger), and search navigation.
 */
public class MyBookingsFrame extends JFrame {
    private ModernTable table;
    private DefaultTableModel model;
    private ModernButton btnCancel, btnBack, btnRefresh;
    private JLabel lblTotalBookings;

    public MyBookingsFrame() {
        setTitle("RailConnect - My Bookings");
        setSize(1150, 680);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_PAGE);

        initUI();
        loadBookings();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        AppHeader header = new AppHeader("My Bookings", () -> {
            Session.logout();
            new LoginFrame().setVisible(true);
            dispose();
        });

        // "Search Trains" button in header
        ModernButton btnSearchHeader = new ModernButton("Search Trains", UITheme.getIcon("search", 14, UITheme.PRIMARY), ModernButton.Style.OUTLINE);
        btnSearchHeader.setFont(UITheme.FONT_SMALL_BOLD);
        btnSearchHeader.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnSearchHeader.addActionListener(e -> {
            new TrainSearchFrame().setVisible(true);
            dispose();
        });
        header.addActionButton(btnSearchHeader);

        add(header, BorderLayout.NORTH);

        // Center Content
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Table Card
        JPanel cardPanel = UITheme.createCardPanel(20);
        cardPanel.setLayout(new BorderLayout(0, 14));

        // Top info bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);

        JPanel textGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        textGroup.setOpaque(false);

        JLabel lblTitle = new JLabel("Passenger Booking History");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Manage your active reservations, view PNR details, or cancel tickets");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        textGroup.add(lblTitle);
        textGroup.add(lblSub);
        titleBar.add(textGroup, BorderLayout.WEST);

        lblTotalBookings = new JLabel("0 Bookings");
        lblTotalBookings.setFont(UITheme.FONT_BODY_BOLD);
        lblTotalBookings.setForeground(UITheme.PRIMARY);
        titleBar.add(lblTotalBookings, BorderLayout.EAST);

        cardPanel.add(titleBar, BorderLayout.NORTH);

        // Table definition
        String[] cols = {"Booking ID", "PNR", "Passenger", "Train", "From", "To", "Departure Date", "Seats", "Total Fare", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(model);
        table.setCenterAlignedColumn(0); // Booking ID
        table.setCenterAlignedColumn(1); // PNR
        table.setCenterAlignedColumn(6); // Date
        table.setCenterAlignedColumn(7); // Seats
        table.setCurrencyColumn(8);      // Total Fare
        table.setStatusBadgeColumn(9);   // Status Badge (CONFIRMED / CANCELLED)

        // Optimized column widths
        table.setColumnWidths(new int[]{75, 95, 120, 140, 115, 115, 105, 60, 105, 115});

        cardPanel.add(ModernTable.wrap(table), BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomActions = new JPanel(new BorderLayout());
        bottomActions.setOpaque(false);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftActions.setOpaque(false);

        btnRefresh = new ModernButton("Refresh", UITheme.getIcon("refresh", 14, UITheme.TEXT_DARK), ModernButton.Style.SECONDARY);
        leftActions.add(btnRefresh);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);

        btnCancel = new ModernButton("Cancel Selected Ticket", UITheme.getIcon("trash", 15, Color.WHITE), ModernButton.Style.DANGER);
        btnBack = new ModernButton("Book Another Train", UITheme.getIcon("train", 15, Color.WHITE), ModernButton.Style.PRIMARY);

        rightActions.add(btnCancel);
        rightActions.add(btnBack);

        bottomActions.add(leftActions, BorderLayout.WEST);
        bottomActions.add(rightActions, BorderLayout.EAST);

        cardPanel.add(bottomActions, BorderLayout.SOUTH);
        mainContent.add(cardPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // --- LISTENERS ---
        btnRefresh.addActionListener(e -> loadBookings());

        btnCancel.addActionListener(e -> cancelTicket());

        btnBack.addActionListener(e -> {
            new TrainSearchFrame().setVisible(true);
            dispose();
        });
    }

    private void cancelTicket() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking from the table to cancel.",
                    "No Booking Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String pnr = model.getValueAt(row, 1).toString();
        String status = model.getValueAt(row, 9).toString();

        if ("CANCELLED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "This ticket (PNR: " + pnr + ") has already been cancelled.",
                    "Already Cancelled",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel ticket for PNR " + pnr + "?\nYour seats will be released back to the schedule.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Insert into Cancellation table (DB trigger restores seats)
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Cancellation (booking_id) VALUES (?)")) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Ticket " + pnr + " has been cancelled successfully.\nSeats have been returned to available inventory.",
                    "Cancellation Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            loadBookings();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cancelling ticket: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBookings() {
        model.setRowCount(0);
        int userId = Session.userId > 0 ? Session.userId : 2;

        String sql = "SELECT booking_id, pnr_number, passenger_name, train_name, source_station, " +
                "destination_station, departure_date, seats_booked, total_fare, booking_status " +
                "FROM booking_summary_view WHERE user_id = ? " +
                "ORDER BY booking_id DESC";

        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                count++;
                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getDate(7),
                        rs.getInt(8),
                        rs.getDouble(9),
                        rs.getString(10)
                });
            }
            lblTotalBookings.setText(count + (count == 1 ? " Booking" : " Bookings"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading bookings: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
