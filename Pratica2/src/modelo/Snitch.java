package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Snitch extends ObjetoEspacial {
    public Snitch(int y) {
        super(LIMITE_DERECHO, y, 20, 20, 8, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.fillOval(x, y, ancho, alto);
    }
}