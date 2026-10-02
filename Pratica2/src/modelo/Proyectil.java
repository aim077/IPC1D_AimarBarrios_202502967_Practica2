package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Proyectil extends ObjetoEspacial {
    public Proyectil(int x, int y) {
        super(x, y, 14, 4, 12, 1); // Dirección derecha
    }

    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, ancho, alto);
    }
}
