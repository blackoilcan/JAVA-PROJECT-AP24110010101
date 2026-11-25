import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * LoginFrame - Modern, responsive login screen with split hero layout and sleek credentials card.
 */
public class LoginFrame extends JFrame {
    private ModernTextField txtUser;
    private ModernPasswordField txtPass;
    private ModernButton btnLogin, btnSignUp;
    private JCheckBox chkShowPassword;

    public LoginFrame() {
        setTitle("RailConnect - Railway Reservation System");
        setSize(880, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_PAGE);

        initUI();
    }

    private void initUI() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UITheme.BG_PAGE);

        // --- LEFT HERO BANNER ---
        JPanel heroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                UITheme.setRenderingHints(g2);
                int w = getWidth();
                int h = getHeight();

                // Gradient background: Dark navy to royal blue
                GradientPaint gp = new GradientPaint(0, 0, UITheme.DARK_NAVY, w, h, UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // Subtle circular decorative accents in background
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-60, -60, 260, 260);
                g2.fillOval(w - 180, h - 180, 280, 280);

                g2.dispose();
            }
        };
        heroPanel.setPreferredSize(new Dimension(380, 540));
        heroPanel.setLayout(new BorderLayout());
        heroPanel.setBorder(new EmptyBorder(40, 36, 40, 36));

        // Hero Top / Content
        JPanel heroContent = new JPanel();
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setOpaque(false);

        JLabel logoIcon = new JLabel(UITheme.getIcon("train", 48, UITheme.ACCENT));
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBrand = new JLabel("RailConnect");
        lblBrand.setFont(UITheme.FONT_HERO);
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTag = new JLabel("Fast & Secure Train Ticketing");
        lblTag.setFont(UITheme.FONT_SUBTITLE);
        lblTag.setForeground(UITheme.ACCENT);
        lblTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><body style='width: 240px; color: #94A3B8; font-size: 11px;'>"
                + "Experience seamless train reservations, live seat checking, instant PNR generation, and smart cancellations."
                + "</body></html>");
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        heroContent.add(logoIcon);
        heroContent.add(Box.createVerticalStrut(16));
        heroContent.add(lblBrand);
        heroContent.add(Box.createVerticalStrut(4));
        heroContent.add(lblTag);
        heroContent.add(Box.createVerticalStrut(14));
        heroContent.add(lblDesc);
        heroContent.add(Box.createVerticalStrut(30));

        // Feature list
        heroContent.add(createFeatureItem("Instant PNR Generation"));
        heroContent.add(Box.createVerticalStrut(12));
        heroContent.add(createFeatureItem("Real-time Available Seats"));
        heroContent.add(Box.createVerticalStrut(12));
        heroContent.add(createFeatureItem("Admin Fleet & Analytics Control"));

        heroPanel.add(heroContent, BorderLayout.CENTER);

        // Hero Footer
        JLabel lblProject = new JLabel("Railway System  •  Group 5 End Sem");
        lblProject.setFont(UITheme.FONT_SMALL);
        lblProject.setForeground(new Color(255, 255, 255, 120));
        heroPanel.add(lblProject, BorderLayout.SOUTH);

        mainContainer.add(heroPanel, BorderLayout.WEST);

        // --- RIGHT LOGIN CARD ---
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(UITheme.BG_PAGE);
        rightWrapper.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel loginCard = UITheme.createCardPanel(32);
        loginCard.setPreferredSize(new Dimension(380, 440));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));

        JLabel lblWelcome = new JLabel("Welcome Back");
        lblWelcome.setFont(UITheme.FONT_TITLE);
        lblWelcome.setForeground(UITheme.TEXT_DARK);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Sign in with your account to continue");
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginCard.add(lblWelcome);
        loginCard.add(Box.createVerticalStrut(4));
        loginCard.add(lblSub);
        loginCard.add(Box.createVerticalStrut(24));

        // Username Field
        JLabel lblUserHeader = new JLabel("Username");
        lblUserHeader.setFont(UITheme.FONT_BODY_BOLD);
        lblUserHeader.setForeground(UITheme.TEXT_DARK);
        lblUserHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUser = new ModernTextField("Enter your username");
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginCard.add(lblUserHeader);
        loginCard.add(Box.createVerticalStrut(6));
        loginCard.add(txtUser);
        loginCard.add(Box.createVerticalStrut(14));

        // Password Field
        JLabel lblPassHeader = new JLabel("Password");
        lblPassHeader.setFont(UITheme.FONT_BODY_BOLD);
        lblPassHeader.setForeground(UITheme.TEXT_DARK);
        lblPassHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPass = new ModernPasswordField("Enter your password");
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginCard.add(lblPassHeader);
        loginCard.add(Box.createVerticalStrut(6));
        loginCard.add(txtPass);
        loginCard.add(Box.createVerticalStrut(8));

        // Show Password checkbox
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(UITheme.FONT_SMALL);
        chkShowPassword.setForeground(UITheme.TEXT_SECONDARY);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPass.setEchoChar((char) 0);
            } else {
                txtPass.setEchoChar('•');
            }
        });
        loginCard.add(chkShowPassword);
        loginCard.add(Box.createVerticalStrut(18));

        // Login Button
        btnLogin = new ModernButton("Sign In", UITheme.getIcon("check", 16, Color.WHITE), ModernButton.Style.PRIMARY);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginCard.add(btnLogin);
        loginCard.add(Box.createVerticalStrut(10));

        // Sign Up Button
        btnSignUp = new ModernButton("Create New Account", ModernButton.Style.OUTLINE);
        btnSignUp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSignUp.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginCard.add(btnSignUp);

        // Hint for evaluators
        loginCard.add(Box.createVerticalGlue());
        JLabel lblHint = new JLabel("<html><center style='color:#94A3B8; font-size:10px;'>"
                + "Demo Accounts: <b>admin</b> / admin123  •  <b>john</b> / pass123"
                + "</center></html>");
        lblHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(lblHint);

        rightWrapper.add(loginCard);
        mainContainer.add(rightWrapper, BorderLayout.CENTER);

        setContentPane(mainContainer);

        // Enter key on input triggers login
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUser.addKeyListener(enterListener);
        txtPass.addKeyListener(enterListener);
        btnLogin.addActionListener(e -> performLogin());

        // Sign Up redirect
        btnSignUp.addActionListener(e -> {
            new SignUpFrame().setVisible(true);
            dispose();
        });
    }

    private JPanel createFeatureItem(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel(UITheme.getIcon("check", 14, UITheme.SUCCESS));
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_BODY);
        label.setForeground(new Color(241, 245, 249));

        p.add(icon);
        p.add(label);
        return p;
    }

    private void performLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?")) {
            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Session.userId = rs.getInt("user_id");
                Session.username = rs.getString("username");
                Session.role = rs.getString("role");

                JOptionPane.showMessageDialog(this,
                        "Login successful! Welcome, " + Session.username + ".",
                        "Welcome",
                        JOptionPane.INFORMATION_MESSAGE);

                if ("Admin".equalsIgnoreCase(Session.role)) {
                    new AdminPanelFrame().setVisible(true);
                } else {
                    new TrainSearchFrame().setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password. Please try again.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Database connection error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
