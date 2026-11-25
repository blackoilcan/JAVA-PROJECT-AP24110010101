import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * ModernTable - Styled JTable with comfortable row height, alternating row colors,
 * rounded headers, and built-in pill badge renderers for Status, Seats, and Fares.
 */
public class ModernTable extends JTable {

    private static final DecimalFormat CURRENCY_FMT = new DecimalFormat("₹ #,##0.00");

    public ModernTable(DefaultTableModel model) {
        super(model);
        init();
    }

    private void init() {
        setRowHeight(38);
        setFont(UITheme.FONT_BODY);
        setShowGrid(true);
        setGridColor(new Color(241, 245, 249));
        setSelectionBackground(UITheme.PRIMARY_LIGHT);
        setSelectionForeground(UITheme.PRIMARY);
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Header styling
        JTableHeader header = getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setFont(UITheme.FONT_HEADER);
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(new Color(248, 250, 252));
                lbl.setForeground(UITheme.TEXT_SECONDARY);
                lbl.setFont(UITheme.FONT_HEADER);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });

        // Default cell renderer with padding and alternating row colors
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(new EmptyBorder(6, 12, 6, 12));
                lbl.setFont(UITheme.FONT_BODY);

                if (isSelected) {
                    lbl.setBackground(UITheme.PRIMARY_LIGHT);
                    lbl.setForeground(UITheme.PRIMARY);
                } else {
                    lbl.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                    lbl.setForeground(UITheme.TEXT_DARK);
                }
                return lbl;
            }
        });
    }

    /**
     * Sets preferred widths for all columns.
     */
    public void setColumnWidths(int[] widths) {
        TableColumnModel colModel = getColumnModel();
        for (int i = 0; i < widths.length && i < colModel.getColumnCount(); i++) {
            colModel.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    /**
     * Applies centered text alignment to specific column index.
     */
    public void setCenterAlignedColumn(int colIndex) {
        if (colIndex >= 0 && colIndex < getColumnCount()) {
            getColumnModel().getColumn(colIndex).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setBorder(new EmptyBorder(6, 8, 6, 8));
                    if (isSelected) {
                        lbl.setBackground(UITheme.PRIMARY_LIGHT);
                        lbl.setForeground(UITheme.PRIMARY);
                    } else {
                        lbl.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                        lbl.setForeground(UITheme.TEXT_DARK);
                    }
                    return lbl;
                }
            });
        }
    }

    /**
     * Applies currency formatting (₹ #,##0.00) to specified column index.
     */
    public void setCurrencyColumn(int colIndex) {
        if (colIndex >= 0 && colIndex < getColumnCount()) {
            getColumnModel().getColumn(colIndex).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setBorder(new EmptyBorder(6, 12, 6, 12));
                    lbl.setFont(UITheme.FONT_BODY_BOLD);

                    if (value instanceof Number) {
                        lbl.setText(CURRENCY_FMT.format(((Number) value).doubleValue()));
                    } else if (value != null) {
                        try {
                            double val = Double.parseDouble(value.toString());
                            lbl.setText(CURRENCY_FMT.format(val));
                        } catch (Exception e) {
                            lbl.setText(value.toString());
                        }
                    }

                    if (isSelected) {
                        lbl.setBackground(UITheme.PRIMARY_LIGHT);
                        lbl.setForeground(UITheme.PRIMARY);
                    } else {
                        lbl.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                        lbl.setForeground(UITheme.TEXT_DARK);
                    }
                    return lbl;
                }
            });
        }
    }

    /**
     * Applies pill badge rendering for Status column (CONFIRMED / CANCELLED).
     */
    public void setStatusBadgeColumn(int colIndex) {
        if (colIndex >= 0 && colIndex < getColumnCount()) {
            getColumnModel().getColumn(colIndex).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                String status = value != null ? value.toString().trim() : "";
                boolean isCancelled = "CANCELLED".equalsIgnoreCase(status);

                Color bg = isCancelled ? UITheme.DANGER_BG : UITheme.SUCCESS_BG;
                Color fg = isCancelled ? UITheme.DANGER_TEXT : UITheme.SUCCESS_TEXT;
                Color border = isCancelled ? new Color(254, 202, 202) : new Color(167, 243, 208);

                PillBadgePanel panel = new PillBadgePanel(status, bg, fg, border);
                panel.setContainerBackground(isSelected ? UITheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
                return panel;
            });
        }
    }

    /**
     * Applies seat availability indicator badge.
     */
    public void setSeatsBadgeColumn(int colIndex) {
        if (colIndex >= 0 && colIndex < getColumnCount()) {
            getColumnModel().getColumn(colIndex).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
                int seats = 0;
                if (value instanceof Number) {
                    seats = ((Number) value).intValue();
                } else if (value != null) {
                    try { seats = Integer.parseInt(value.toString()); } catch (Exception ignored) {}
                }

                String text;
                Color bg;
                Color fg;
                Color border;

                if (seats <= 0) {
                    text = "Sold Out";
                    bg = UITheme.DANGER_BG;
                    fg = UITheme.DANGER_TEXT;
                    border = new Color(254, 202, 202);
                } else if (seats < 25) {
                    text = seats + " Left (Fast)";
                    bg = UITheme.WARNING_BG;
                    fg = UITheme.WARNING_TEXT;
                    border = new Color(253, 230, 138);
                } else {
                    text = seats + " Available";
                    bg = UITheme.SUCCESS_BG;
                    fg = UITheme.SUCCESS_TEXT;
                    border = new Color(167, 243, 208);
                }

                PillBadgePanel panel = new PillBadgePanel(text, bg, fg, border);
                panel.setContainerBackground(isSelected ? UITheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
                return panel;
            });
        }
    }

    /**
     * Pill badge panel used inside table cells.
     */
    private static class PillBadgePanel extends JPanel {
        private final String text;
        private final Color badgeBg;
        private final Color badgeFg;
        private final Color badgeBorder;

        public PillBadgePanel(String text, Color bg, Color fg, Color border) {
            this.text = text;
            this.badgeBg = bg;
            this.badgeFg = fg;
            this.badgeBorder = border;
            setOpaque(true);
        }

        public void setContainerBackground(Color bg) {
            setBackground(bg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.setRenderingHints(g2);

            int w = getWidth();
            int h = getHeight();

            Font font = UITheme.FONT_SMALL_BOLD;
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int badgeW = Math.min(w - 6, textW + 18);
            int badgeH = 24;
            int x = (w - badgeW) / 2;
            int y = (h - badgeH) / 2;

            // Draw pill background
            g2.setColor(badgeBg);
            g2.fillRoundRect(x, y, badgeW, badgeH, 12, 12);

            // Draw pill border
            if (badgeBorder != null) {
                g2.setColor(badgeBorder);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x, y, badgeW, badgeH, 12, 12);
            }

            // Draw text
            g2.setColor(badgeFg);
            int textX = x + (badgeW - textW) / 2;
            int textY = y + (badgeH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, textX, textY);

            g2.dispose();
        }
    }

    /**
     * Wrap table in a clean modern JScrollPane.
     */
    public static JScrollPane wrap(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);
        return scroll;
    }
}
