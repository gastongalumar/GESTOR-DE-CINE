package Clases;

import javax.swing.*;
import java.awt.*;

public class PanelLeyenda extends JPanel {

    public PanelLeyenda() {
        setBackground(new Color(20, 20, 20));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));

        agregarItemLeyenda(new Color(120, 120, 120), "Disponible");
        agregarItemLeyenda(new Color(40, 80, 180), "Seleccionado");
        agregarItemLeyenda(new Color(180, 40, 40), "Ocupado");
    }

    private void agregarItemLeyenda(Color color, String texto) {
        JPanel panelItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panelItem.setBackground(new Color(20, 20, 20));

        JButton ejemplo = crearBotonEjemplo(color);
        JLabel label = crearLabelTexto(texto);

        panelItem.add(ejemplo);
        panelItem.add(label);
        add(panelItem);
    }

    private JButton crearBotonEjemplo(Color color) {
        return new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(color);
                g2.fillRoundRect(2, 2, w - 4, h - 4, 8, 8);
                g2.setColor(color.brighter());
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(2, 2, w - 4, h - 4, 8, 8);
            }
        };
    }

    private JLabel crearLabelTexto(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }
}