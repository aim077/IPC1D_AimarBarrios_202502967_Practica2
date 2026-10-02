package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Asteroide extends ObjetoEspacial {
    public Asteroide(int y) {
        super(LIMITE_DERECHO, y, 36, 36, 4, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillOval(x, y, ancho, alto);
    }
}