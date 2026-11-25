import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * SignUpFrame - Modern registration screen with a clean two-column form card and visual feedback.
 */
public class SignUpFrame extends JFrame {
    private ModernTextField txtName, txtAge, txtPhone, txtEmail, txtUser;
    private ModernPasswordField txtPass;
    private JComboBox<String> cbGender;
    private JCheckBox chkShowPassword;
    private ModernButton btnRegister, btnBack;

    public SignUpFrame() {
        setTitle("RailConnect - Passenger Registration");
        setSize(920, 620);
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

                GradientPaint gp = new GradientPaint(0, 0, UITheme.DARK_NAVY, w, h, UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-40, -40, 240, 240);
                g2.fillOval(w - 160, h - 160, 260, 260);
                g2.dispose();
            }
        };
        heroPanel.setPreferredSize(new Dimension(340, 620));
        heroPanel.setLayout(new BorderLayout());
        heroPanel.setBorder(new EmptyBorder(40, 32, 40, 32));

        JPanel heroContent = new JPanel();
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setOpaque(false);

        JLabel logoIcon = new JLabel(UITheme.getIcon("train", 44, UITheme.ACCENT));
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHeroTitle = new JLabel("Join RailConnect");
        lblHeroTitle.setFont(UITheme.FONT_HERO);
        lblHeroTitle.setForeground(Color.WHITE);
        lblHeroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHeroSub = new JLabel("<html><body style='width: 220px; color: #94A3B8; font-size: 11px;'>"
                + "Create your personal account to browse train schedules, book passenger tickets, and track your booking history effortlessly."
                + "</body></html>");
        lblHeroSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        heroContent.add(logoIcon);
        heroContent.add(Box.createVerticalStrut(16));
        heroContent.add(lblHeroTitle);
        heroContent.add(Box.createVerticalStrut(12));
        heroContent.add(lblHeroSub);
        heroContent.add(Box.createVerticalStrut(32));

        heroContent.add(createFeatureItem("Instant Account Activation"));
        heroContent.add(Box.createVerticalStrut(12));
        heroContent.add(createFeatureItem("Direct Seat Allocation"));
        heroContent.add(Box.createVerticalStrut(12));
        heroContent.add(createFeatureItem("Manage & Cancel Bookings"));

        heroPanel.add(heroContent, BorderLayout.CENTER);

        JLabel lblFooter = new JLabel("Railway System  •  Group 5 End Sem");
        lblFooter.setFont(UITheme.FONT_SMALL);
        lblFooter.setForeground(new Color(255, 255, 255, 120));
        heroPanel.add(lblFooter, BorderLayout.SOUTH);

        mainContainer.add(heroPanel, BorderLayout.WEST);

        // --- RIGHT FORM CARD ---
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(UITheme.BG_PAGE);
        rightWrapper.setBorder(new EmptyBorder(24, 32, 24, 32));

        JPanel formCard = UITheme.createCardPanel(28);
        formCard.setPreferredSize(new Dimension(480, 520));
        formCard.setLayout(new BorderLayout(0, 16));

        // Form Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);

        JLabel lblFormTitle = new JLabel("Passenger Registration");
        lblFormTitle.setFont(UITheme.FONT_TITLE);
        lblFormTitle.setForeground(UITheme.TEXT_DARK);

        JLabel lblFormSubtitle = new JLabel("Enter your accurate information to create an account");
        lblFormSubtitle.setFont(UITheme.FONT_BODY);
        lblFormSubtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(lblFormTitle);
        titlePanel.add(lblFormSubtitle);
        formCard.add(titlePanel, BorderLayout.NORTH);

        // Fields Grid (2 Columns)
        JPanel fieldsGrid = new JPanel(new GridLayout(4, 2, 14, 12));
        fieldsGrid.setOpaque(false);

        // Full Name
        txtName = new ModernTextField("John Doe");
        fieldsGrid.add(createFieldGroup("Full Name", txtName));

        // Age
        txtAge = new ModernTextField("25");
        fieldsGrid.add(createFieldGroup("Age", txtAge));

        // Gender
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setFont(UITheme.FONT_BODY);
        cbGender.setBackground(Color.WHITE);
        fieldsGrid.add(createFieldGroup("Gender", cbGender));

        // Phone
        txtPhone = new ModernTextField("9876543210");
        fieldsGrid.add(createFieldGroup("Phone Number", txtPhone));

        // Email
        txtEmail = new ModernTextField("john@example.com");
        fieldsGrid.add(createFieldGroup("Email Address", txtEmail));

        // Username
        txtUser = new ModernTextField("john_doe");
        fieldsGrid.add(createFieldGroup("Username", txtUser));

        // Password
        txtPass = new ModernPasswordField("Enter strong password");
        fieldsGrid.add(createFieldGroup("Password", txtPass));

        // Show password toggle
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(UITheme.FONT_SMALL);
        chkShowPassword.setForeground(UITheme.TEXT_SECONDARY);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPass.setEchoChar((char) 0);
            } else {
                txtPass.setEchoChar('•');
            }
        });
        JPanel chkPanel = new JPanel(new BorderLayout());
        chkPanel.setOpaque(false);
        chkPanel.add(chkShowPassword, BorderLayout.SOUTH);
        fieldsGrid.add(chkPanel);

        formCard.add(fieldsGrid, BorderLayout.CENTER);

        // Bottom Actions
        JPanel actionsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        actionsPanel.setOpaque(false);

        btnRegister = new ModernButton("Create Passenger Account", UITheme.getIcon("check", 16, Color.WHITE), ModernButton.Style.PRIMARY);
        btnRegister.setPreferredSize(new Dimension(0, 42));

        btnBack = new ModernButton("Already have an account? Back to Login", ModernButton.Style.OUTLINE);
        btnBack.setPreferredSize(new Dimension(0, 38));

        actionsPanel.add(btnRegister);
        actionsPanel.add(btnBack);
        formCard.add(actionsPanel, BorderLayout.SOUTH);

        rightWrapper.add(formCard);
        mainContainer.add(rightWrapper, BorderLayout.CENTER);

        setContentPane(mainContainer);

        // Actions
        btnRegister.addActionListener(e -> performRegister());
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
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

    private JPanel createFieldGroup(String labelText, JComponent inputComponent) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_DARK);

        group.add(lbl, BorderLayout.NORTH);
        group.add(inputComponent, BorderLayout.CENTER);
        return group;
    }

    private void performRegister() {
        String name = txtName.getText().trim();
        String ageStr = txtAge.getText().trim();
        String gender = (String) cbGender.getSelectedItem();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty() || email.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all the required registration fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 120) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid age between 1 and 120.",
                        "Invalid Age",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Age must be a numeric integer value.",
                    "Invalid Age",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Insert into Users table
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Users (full_name, age, gender, phone, email, username, password, role) VALUES (?, ?, ?, ?, ?, ?, ?, 'Passenger')")) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, user);
            ps.setString(7, pass);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Registration successful!\nYou can now sign in with your credentials.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            new LoginFrame().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Registration failed: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
