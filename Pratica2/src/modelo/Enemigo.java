package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Enemigo extends ObjetoEspacial {
    public Enemigo(int y, int velocidad) {
        super(LIMITE_DERECHO, y, 40, 30, velocidad, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        Dibujo.poli(g, new Color(220, 40, 60), x, y, ancho, alto,
            0.0, 0.5, 0.35, 0.0, 1.0, 0.15, 0.8, 0.5, 1.0, 0.85, 0.35, 1.0);
        g.setColor(Color.YELLOW);
        g.fillOval(x + ancho * 3 / 10, y + alto * 4 / 10, 7, 7);
    }
}