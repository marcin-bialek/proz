package pl.edu.pw.stud.bialek2.marcin.proz;

import java.awt.Color;
import javax.swing.SwingUtilities;


public final class App {
    public static final Color BACKGROUND_COLOR = new Color(30, 31, 38);
    public static final Color PRIMARY_COLOR = new Color(40, 54, 85);
    public static final Color SECONDARY_COLOR = new Color(77, 100, 141);
    public static final Color ACCENT_COLOR = new Color(208, 225, 249);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomeWindow();
        });
    }
}

