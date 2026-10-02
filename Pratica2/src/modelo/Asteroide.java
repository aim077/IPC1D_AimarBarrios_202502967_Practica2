package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Asteroide extends ObjetoEspacial {
    public Asteroide(int y) {
        super(LIMITE_DERECHO, y, 36, 36, 4, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        Dibujo.poli(g, new Color(135, 115, 100), x, y, ancho, alto,
            0.1, 0.3, 0.35, 0.0, 0.7, 0.05, 1.0, 0.35, 0.9, 0.75, 0.6, 1.0, 0.2, 0.9, 0.0, 0.6);
        g.setColor(new Color(95, 80, 70)); 
        g.fillOval(x + 8, y + 8, 9, 9);
        g.fillOval(x + 20, y + 18, 7, 7);
        g.fillOval(x + 10, y + 24, 5, 5);
    }
}