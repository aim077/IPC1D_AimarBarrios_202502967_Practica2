package modelo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Snitch extends ObjetoEspacial {
    public Snitch(int y) {
        super(LIMITE_DERECHO, y, 26, 26, 7, -1);
    }

    @Override
    public void dibujar(Graphics2D g) {
        long t = System.currentTimeMillis();
        int pulso = (int) (6 + 4 * Math.sin(t / 100.0));   // el halo late
        int aleteo = ((t / 80) % 2 == 0) ? 0 : 7;

        // halo brillante
        g.setColor(new Color(255, 230, 0, 70));
        g.fillOval(x - pulso, y - pulso, ancho + pulso * 2, alto + pulso * 2);

        // cuerpo dorado
        g.setColor(new Color(255, 200, 0));
        g.fillOval(x, y, ancho, alto);
        g.setColor(new Color(255, 120, 0));
        g.drawOval(x, y, ancho, alto);
        g.setColor(Color.WHITE);
        g.fillOval(x + 6, y + 5, 7, 7);
    }
}