import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AppHeader - Consistent, clean navigation header for all main authenticated views.
 */
public class AppHeader extends JPanel {

    private final JPanel rightActionsPanel;

    public AppHeader(String currentScreenTitle, Runnable onLogout) {
        setLayout(new BorderLayout());
        setBackground(UITheme.DARK_NAVY);
        setBorder(new EmptyBorder(12, 20, 12, 20));

        // Left Branding Section
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        // Train Logo Icon
        JLabel logoLabel = new JLabel(UITheme.getIcon("train", 30, UITheme.ACCENT));
        leftPanel.add(logoLabel);

        // Title and subtitle
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);

        JLabel lblBrand = new JLabel("RAILCONNECT  •  " + (currentScreenTitle != null ? currentScreenTitle.toUpperCase() : "PORTAL"));
        lblBrand.setFont(UITheme.FONT_SUBTITLE);
        lblBrand.setForeground(Color.WHITE);

        JLabel lblTagline = new JLabel("National Railway Reservation & Fleet Management System");
        lblTagline.setFont(UITheme.FONT_SMALL);
        lblTagline.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblBrand);
        titlePanel.add(lblTagline);
        leftPanel.add(titlePanel);
        add(leftPanel, BorderLayout.WEST);

        // Right Section: User chip & action buttons
        rightActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActionsPanel.setOpaque(false);

        // User badge chip
        if (Session.username != null && !Session.username.isEmpty()) {
            JPanel userChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            userChip.setOpaque(false);

            String roleDisplay = Session.role != null ? Session.role : "User";
            boolean isAdmin = "Admin".equalsIgnoreCase(roleDisplay);

            JLabel userIcon = new JLabel(UITheme.getIcon(isAdmin ? "shield" : "user", 16, isAdmin ? UITheme.WARNING : UITheme.ACCENT));
            JLabel lblUser = new JLabel(Session.username + " (" + roleDisplay + ")");
            lblUser.setFont(UITheme.FONT_BODY_BOLD);
            lblUser.setForeground(Color.WHITE);

            userChip.add(userIcon);
            userChip.add(lblUser);
            rightActionsPanel.add(userChip);
        }

        // Logout Button
        if (onLogout != null) {
            ModernButton btnLogout = new ModernButton("Logout", UITheme.getIcon("logout", 14, Color.WHITE), ModernButton.Style.DANGER);
            btnLogout.setBorder(new EmptyBorder(6, 12, 6, 12));
            btnLogout.setFont(UITheme.FONT_SMALL_BOLD);
            btnLogout.addActionListener(e -> onLogout.run());
            rightActionsPanel.add(btnLogout);
        }

        add(rightActionsPanel, BorderLayout.EAST);
    }

    /**
     * Adds an action button to the header (placed before logout).
     */
    public void addActionButton(ModernButton button) {
        // Insert right before the logout button (which is last)
        int count = rightActionsPanel.getComponentCount();
        if (count > 0) {
            rightActionsPanel.add(button, count - 1);
        } else {
            rightActionsPanel.add(button);
        }
        rightActionsPanel.revalidate();
        rightActionsPanel.repaint();
    }
}
