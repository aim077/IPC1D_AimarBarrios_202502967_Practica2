package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Quaffle extends ObjetoEspacial {
    public Quaffle(int y) {
        super(LIMITE_DERECHO, y, 26, 26, 4, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(new Color(60, 180, 90));
        g.fillRoundRect(x, y, ancho, alto, 8, 8);
        g.setColor(new Color(30, 110, 55));
        g.drawRoundRect(x, y, ancho, alto, 8, 8);
        g.setColor(Color.WHITE);
        g.fillRect(x + 4, y + alto / 2 - 2, ancho - 8, 4);
    }
}
