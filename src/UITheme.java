import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;

/**
 * UITheme - Centralized modern design system for the Railway Reservation System.
 * Built using 100% pure Java Swing and Java2D without any external libraries.
 */
public class UITheme {

    // --- Color Palette ---
    public static final Color PRIMARY        = new Color(30, 64, 175);   // Modern Indigo / Royal Blue (#1E40AF)
    public static final Color PRIMARY_HOVER  = new Color(29, 78, 216);   // #1D4ED8
    public static final Color PRIMARY_LIGHT  = new Color(239, 246, 255); // #EFF6FF
    public static final Color ACCENT         = new Color(14, 165, 233);  // Sky Blue (#0EA5E9)
    public static final Color ACCENT_HOVER   = new Color(2, 132, 199);

    public static final Color DARK_NAVY      = new Color(15, 23, 42);    // Slate 900 (#0F172A)
    public static final Color DARK_SLATE     = new Color(30, 41, 59);    // Slate 800 (#1E293B)
    public static final Color BG_PAGE        = new Color(241, 245, 249); // Slate 100 (#F1F5F9)
    public static final Color BG_CARD        = Color.WHITE;

    public static final Color BORDER         = new Color(226, 232, 240); // Slate 200 (#E2E8F0)
    public static final Color BORDER_LIGHT   = new Color(241, 245, 249);
    public static final Color BORDER_FOCUS   = new Color(59, 130, 246);  // Blue 500

    public static final Color TEXT_DARK      = new Color(15, 23, 42);    // High contrast primary text
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105);   // Slate 600
    public static final Color TEXT_MUTED     = new Color(148, 163, 184); // Slate 400

    // Status / Feedback Colors
    public static final Color SUCCESS        = new Color(16, 185, 129);  // Emerald 500
    public static final Color SUCCESS_HOVER  = new Color(5, 150, 105);
    public static final Color SUCCESS_BG     = new Color(236, 253, 245); // Emerald 50
    public static final Color SUCCESS_TEXT   = new Color(6, 95, 70);     // Emerald 800

    public static final Color DANGER         = new Color(239, 68, 68);   // Red 500
    public static final Color DANGER_HOVER   = new Color(220, 38, 38);
    public static final Color DANGER_BG      = new Color(254, 242, 242); // Red 50
    public static final Color DANGER_TEXT    = new Color(153, 27, 27);   // Red 800

    public static final Color WARNING        = new Color(245, 158, 11);  // Amber 500
    public static final Color WARNING_BG     = new Color(254, 252, 232); // Amber 50
    public static final Color WARNING_TEXT   = new Color(146, 64, 14);   // Amber 800

    // --- Typography ---
    private static final String FONT_FAMILY = getBestFontFamily();

    public static final Font FONT_HERO       = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font FONT_TITLE      = new Font(FONT_FAMILY, Font.BOLD, 20);
    public static final Font FONT_SUBTITLE   = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Font FONT_HEADER     = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_BODY       = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD  = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_SMALL      = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font(FONT_FAMILY, Font.BOLD, 11);
    public static final Font FONT_MONO       = new Font("Monospaced", Font.BOLD, 13);

    private static String getBestFontFamily() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();
        String[] preferred = {"Segoe UI", "Inter", "SF Pro Display", "Ubuntu", "Roboto", "DejaVu Sans", "Arial"};
        for (String pref : preferred) {
            for (String f : fonts) {
                if (f.equalsIgnoreCase(pref)) {
                    return f;
                }
            }
        }
        return Font.SANS_SERIF;
    }

    /**
     * Initializes global look-and-feel defaults and anti-aliasing rendering hints.
     */
    public static void initLookAndFeel() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            // Prefer Nimbus for smooth modern component bases
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored2) {}
        }

        // Apply clean font defaults
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("Button.font", FONT_BODY_BOLD);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("PasswordField.font", FONT_BODY);
        UIManager.put("ComboBox.font", FONT_BODY);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("TableHeader.font", FONT_HEADER);
        UIManager.put("TabbedPane.font", FONT_BODY_BOLD);
        UIManager.put("TitledBorder.font", FONT_HEADER);
        UIManager.put("OptionPane.messageFont", FONT_BODY);
        UIManager.put("OptionPane.buttonFont", FONT_BODY_BOLD);
        UIManager.put("ToolTip.font", FONT_SMALL);

        // Window background
        UIManager.put("Panel.background", BG_PAGE);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("Panel.opaque", true);
    }

    /**
     * Applies standard 2D rendering hints for smooth antialiased graphics.
     */
    public static void setRenderingHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /**
     * Creates a modern rounded Card Panel with optional padding and border.
     */
    public static JPanel createCardPanel(int padding) {
        return new RoundedPanel(16, BG_CARD, BORDER, padding);
    }

    public static JPanel createCardPanel(int padding, Color bgColor) {
        return new RoundedPanel(16, bgColor, BORDER, padding);
    }

    /**
     * Rounded panel implementation with subtle border.
     */
    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;
        private final Color borderColor;

        public RoundedPanel(int radius, Color bgColor, Color borderColor, int padding) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false);
            if (padding > 0) {
                setBorder(new EmptyBorder(padding, padding, padding, padding));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            setRenderingHints(g2);

            int w = getWidth() - 1;
            int h = getHeight() - 1;

            // Fill background
            g2.setColor(bgColor != null ? bgColor : BG_CARD);
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            // Draw border
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w, h, radius, radius);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Custom vector Icon implementation that paints icons at any size without image files.
     */
    public static class VectorIcon implements Icon {
        private final String name;
        private final int size;
        private final Color color;

        public VectorIcon(String name, int size, Color color) {
            this.name = name;
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() { return size; }
        @Override
        public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            setRenderingHints(g2);
            g2.setColor(color != null ? color : TEXT_DARK);
            g2.translate(x, y);

            float s = size / 24.0f; // normalize from standard 24x24 viewport
            g2.scale(s, s);

            drawVector(g2, name);
            g2.dispose();
        }

        private void drawVector(Graphics2D g2, String name) {
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (name.toLowerCase()) {
                case "train":
                    // Train / Locomotive silhouette
                    g2.drawRoundRect(4, 3, 16, 17, 4, 4);
                    // Windshield
                    g2.fillRect(7, 6, 10, 5);
                    // Headlights
                    g2.fillOval(7, 14, 3, 3);
                    g2.fillOval(14, 14, 3, 3);
                    // Wheels / Track
                    g2.drawLine(2, 22, 22, 22);
                    g2.drawLine(6, 20, 4, 22);
                    g2.drawLine(18, 20, 20, 22);
                    break;

                case "user":
                    // Person head & shoulders
                    g2.drawOval(8, 4, 8, 8);
                    g2.drawArc(4, 14, 16, 12, 0, 180);
                    break;

                case "lock":
                    // Lock body & shackle
                    g2.drawRoundRect(5, 10, 14, 11, 3, 3);
                    g2.drawArc(7, 4, 10, 10, 0, 180);
                    g2.fillOval(11, 14, 2, 3);
                    break;

                case "search":
                    // Magnifying glass
                    g2.drawOval(4, 4, 12, 12);
                    g2.drawLine(13, 13, 20, 20);
                    break;

                case "calendar":
                    // Calendar card
                    g2.drawRoundRect(4, 5, 16, 15, 3, 3);
                    g2.drawLine(4, 9, 20, 9);
                    g2.drawLine(8, 3, 8, 6);
                    g2.drawLine(16, 3, 16, 6);
                    g2.fillRect(8, 12, 2, 2);
                    g2.fillRect(12, 12, 2, 2);
                    g2.fillRect(16, 12, 2, 2);
                    break;

                case "ticket":
                    // Ticket with notch
                    Path2D ticket = new Path2D.Float();
                    ticket.moveTo(3, 7);
                    ticket.lineTo(21, 7);
                    ticket.lineTo(21, 11);
                    ticket.curveTo(19, 11, 19, 13, 21, 13);
                    ticket.lineTo(21, 17);
                    ticket.lineTo(3, 17);
                    ticket.lineTo(3, 13);
                    ticket.curveTo(5, 13, 5, 11, 3, 11);
                    ticket.closePath();
                    g2.draw(ticket);
                    g2.drawLine(10, 7, 10, 17);
                    break;

                case "check":
                    // Checkmark
                    g2.drawLine(4, 12, 9, 17);
                    g2.drawLine(9, 17, 20, 6);
                    break;

                case "trash":
                    // Trash bin
                    g2.drawLine(4, 7, 20, 7);
                    g2.drawLine(9, 4, 15, 4);
                    g2.drawRoundRect(6, 7, 12, 14, 2, 2);
                    g2.drawLine(10, 10, 10, 17);
                    g2.drawLine(14, 10, 14, 17);
                    break;

                case "plus":
                    // Plus sign
                    g2.drawLine(12, 5, 12, 19);
                    g2.drawLine(5, 12, 19, 12);
                    break;

                case "refresh":
                    // Circular refresh arrow
                    g2.drawArc(4, 4, 16, 16, 45, 270);
                    g2.drawLine(18, 5, 18, 10);
                    g2.drawLine(18, 10, 13, 10);
                    break;

                case "logout":
                    // Logout door and arrow
                    g2.drawRect(4, 4, 8, 16);
                    g2.drawLine(12, 12, 21, 12);
                    g2.drawLine(18, 9, 21, 12);
                    g2.drawLine(18, 15, 21, 12);
                    break;

                case "swap":
                    // Opposing arrows
                    g2.drawLine(5, 9, 19, 9);
                    g2.drawLine(16, 6, 19, 9);
                    g2.drawLine(16, 12, 19, 9);
                    g2.drawLine(19, 15, 5, 15);
                    g2.drawLine(8, 12, 5, 15);
                    g2.drawLine(8, 18, 5, 15);
                    break;

                case "shield":
                    // Admin Shield
                    Path2D shield = new Path2D.Float();
                    shield.moveTo(12, 3);
                    shield.lineTo(20, 6);
                    shield.lineTo(20, 12);
                    shield.curveTo(20, 18, 16, 21, 12, 22);
                    shield.curveTo(8, 21, 4, 18, 4, 12);
                    shield.lineTo(4, 6);
                    shield.closePath();
                    g2.draw(shield);
                    g2.drawLine(12, 7, 12, 17);
                    break;

                case "chart":
                    // Bar chart
                    g2.drawLine(4, 20, 20, 20);
                    g2.fillRect(6, 14, 3, 6);
                    g2.fillRect(11, 8, 3, 12);
                    g2.fillRect(16, 4, 3, 16);
                    break;

                default:
                    g2.fillOval(8, 8, 8, 8);
                    break;
            }
        }
    }

    public static Icon getIcon(String name, int size, Color color) {
        return new VectorIcon(name, size, color);
    }
}
