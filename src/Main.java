import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize modern theme, anti-aliasing hints and styling
        UITheme.initLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
