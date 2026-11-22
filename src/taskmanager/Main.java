package taskmanager;

import taskmanager.ui.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point.
 *
 * @author Your Name
 */
public class Main {
    /**
     * Launches the GUI on the Swing event thread.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}