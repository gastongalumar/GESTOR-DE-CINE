
import Clases.SelectorAsientos;
import ManejoJSON.JSONUtiles;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new SelectorAsientos().setVisible(true);
        });
    }
}