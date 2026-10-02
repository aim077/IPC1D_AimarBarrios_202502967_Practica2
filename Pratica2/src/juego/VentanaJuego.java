package juego;

import modelo.Piloto;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaJuego extends JFrame {
    public VentanaJuego(Piloto piloto, JFrame menu) {
        super("Quetzal Space Defender");
        PanelJuego panel = new PanelJuego(piloto);
        add(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.detener(); // para los hilos si cierran a mitad de partida
            }

            @Override
            public void windowClosed(WindowEvent e) {
                menu.setVisible(true); // vuelve al menú
            }
        });
        setVisible(true);
        panel.requestFocusInWindow();
        panel.iniciar();
    }
}