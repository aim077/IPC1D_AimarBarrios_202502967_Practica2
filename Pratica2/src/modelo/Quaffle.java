package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Quaffle extends ObjetoEspacial {
    public Quaffle(int y) {
        super(LIMITE_DERECHO, y, 26, 26, 4, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, ancho, alto);
    }
}
