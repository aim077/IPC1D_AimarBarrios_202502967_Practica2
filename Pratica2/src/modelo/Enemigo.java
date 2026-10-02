package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Enemigo extends ObjetoEspacial {
    public Enemigo(int y, int velocidad) {
        super(LIMITE_DERECHO, y, 40, 30, velocidad, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Color.RED);
        int[] xs = {x, x + ancho, x + ancho};
        int[] ys = {y + alto / 2, y, y + alto};
        g.fillPolygon(xs, ys, 3);
    }
}