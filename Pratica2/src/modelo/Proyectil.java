package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Proyectil extends ObjetoEspacial {
    public Proyectil(int x, int y) {
        super(x, y, 14, 4, 12, 1); // Dirección derecha
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(new Color(255, 255, 0, 90));            // brillo
        g.fillRoundRect(x - 2, y - 2, ancho + 4, alto + 4, 6, 6);
        g.setColor(new Color(255, 255, 200));              // núcleo
        g.fillRoundRect(x, y, ancho, alto, 4, 4);
    }
}
