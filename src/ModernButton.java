import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ModernButton - Antialiased rounded button with responsive hover and active states.
 */
public class ModernButton extends JButton {

    public enum Style {
        PRIMARY, SECONDARY, SUCCESS, DANGER, ACCENT, OUTLINE
    }

    private final Style style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 10;

    public ModernButton(String text, Style style) {
        this(text, null, style);
    }

    public ModernButton(String text, Icon icon, Style style) {
        super(text, icon);
        this.style = style;
        init();
    }

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    private void init() {
        setFont(UITheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(9, 18, 9, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.setRenderingHints(g2);

        int w = getWidth();
        int h = getHeight();

        Color bg;
        Color fg;
        Color border = null;

        if (!isEnabled()) {
            bg = new Color(226, 232, 240);
            fg = UITheme.TEXT_MUTED;
        } else {
            switch (style) {
                case SECONDARY:
                    bg = isPressed ? new Color(226, 232, 240) : (isHovered ? new Color(241, 245, 249) : Color.WHITE);
                    fg = UITheme.TEXT_DARK;
                    border = UITheme.BORDER;
                    break;
                case OUTLINE:
                    bg = isPressed ? UITheme.PRIMARY_LIGHT : (isHovered ? UITheme.PRIMARY_LIGHT : Color.WHITE);
                    fg = UITheme.PRIMARY;
                    border = UITheme.PRIMARY;
                    break;
                case SUCCESS:
                    bg = isPressed ? UITheme.SUCCESS_HOVER.darker() : (isHovered ? UITheme.SUCCESS_HOVER : UITheme.SUCCESS);
                    fg = Color.WHITE;
                    break;
                case DANGER:
                    bg = isPressed ? UITheme.DANGER_HOVER.darker() : (isHovered ? UITheme.DANGER_HOVER : UITheme.DANGER);
                    fg = Color.WHITE;
                    break;
                case ACCENT:
                    bg = isPressed ? UITheme.ACCENT_HOVER.darker() : (isHovered ? UITheme.ACCENT_HOVER : UITheme.ACCENT);
                    fg = Color.WHITE;
                    break;
                case PRIMARY:
                default:
                    bg = isPressed ? UITheme.PRIMARY_HOVER.darker() : (isHovered ? UITheme.PRIMARY_HOVER : UITheme.PRIMARY);
                    fg = Color.WHITE;
                    break;
            }
        }

        // Draw background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Draw border if needed
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        setForeground(fg);
        g2.dispose();
        super.paintComponent(g);
    }
}
