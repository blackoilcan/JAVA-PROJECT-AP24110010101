import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * ModernPasswordField - Clean rounded password input with placeholder and focus highlight.
 */
public class ModernPasswordField extends JPasswordField {
    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 8;

    public ModernPasswordField(int columns) {
        super(columns);
        init();
    }

    public ModernPasswordField(String placeholder) {
        super();
        this.placeholder = placeholder;
        init();
    }

    private void init() {
        setFont(UITheme.FONT_BODY);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setCaretColor(UITheme.PRIMARY);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UITheme.setRenderingHints(g2);

        int w = getWidth() - 1;
        int h = getHeight() - 1;

        // Background
        g2.setColor(isEnabled() ? UITheme.BG_CARD : new Color(241, 245, 249));
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Border
        if (isFocused) {
            g2.setColor(UITheme.BORDER_FOCUS);
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(UITheme.BORDER);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Placeholder
        if (getPassword().length == 0 && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(UITheme.TEXT_MUTED);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 12, y);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
