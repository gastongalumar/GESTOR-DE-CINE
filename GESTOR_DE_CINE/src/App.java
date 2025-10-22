
import Clases.SelectorAsientos;
import ManejoJSON.JSONUtiles;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            new SelectorAsientos();
//        });

//
        SwingUtilities.invokeLater(() -> {
            try {
                // Establecer el look and feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Crear y mostrar la ventana principal
            SelectorAsientos selector = new SelectorAsientos();
            selector.setVisible(true);
        });
    }
}