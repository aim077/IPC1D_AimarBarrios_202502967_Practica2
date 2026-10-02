package modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Dibujo {

    // Polígono con puntos en fracciones 
    public static void poli(Graphics2D g, Color c, int x, int y, int w, int h, double... p) {
        int n = p.length / 2;
        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = x + (int) (p[2 * i] * w);
            ys[i] = y + (int) (p[2 * i + 1] * h);
        }
        g.setColor(c);
        g.fillPolygon(xs, ys, n);
    }

    // Rectángulo redondeado con medidas en fracciones del rectángulo x, y, w, h
    private static void rect(Graphics2D g, Color c, int x, int y, int w, int h,
                             double fx, double fy, double fw, double fh) {
        g.setColor(c);
        g.fillRoundRect(x + (int) (fx * w), y + (int) (fy * h),
                Math.max(2, (int) (fw * w)), Math.max(2, (int) (fh * h)), 4, 4);
    }

    // Nave del jugador
    public static void dibujarNave(Graphics2D g, TipoNave tipo, int x, int y, int w, int h,
                                   boolean bloqueada) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color base = bloqueada ? Color.MAGENTA : tipo.getColor();
        Color oscuro = base.darker().darker();
        

        // grosor del cuerpo según la nave
        double gr = 0.10;
        if (tipo == TipoNave.CAZA_ESTELAR) gr = 0.14;
        if (tipo == TipoNave.ACORAZADO) gr = 0.19;

        // llama del motor
        double largo = ((System.currentTimeMillis() / 70) % 2 == 0) ? 0.30 : 0.18;
        poli(g, new Color(255, 170, 30), x, y, w, h, 0.04, 0.42, -largo, 0.5, 0.04, 0.58);

        // alerón trasero
        rect(g, oscuro, x, y, w, h, 0.00, 0.06, 0.09, 0.88);
        rect(g, base, x, y, w, h, 0.00, 0.06, 0.03, 0.88);

      
        // pontones laterales
        rect(g, oscuro, x, y, w, h, 0.22, 0.5 - gr - 0.10, 0.30, 0.12);
        rect(g, oscuro, x, y, w, h, 0.22, 0.5 + gr - 0.02, 0.30, 0.12);

        // cuerpo alargado con la nariz a la derecha
        poli(g, base, x, y, w, h,
                0.08, 0.5 - gr, 0.55, 0.5 - gr * 0.7, 1.00, 0.46,
                1.00, 0.54, 0.55, 0.5 + gr * 0.7, 0.08, 0.5 + gr);

        // franja blanca
        g.setColor(new Color(255, 255, 255, 190));
        g.fillRect(x + (int) (0.14 * w), y + (int) (0.47 * h), (int) (0.5 * w), Math.max(2, (int) (0.06 * h)));

        // alerón delantero
        rect(g, oscuro, x, y, w, h, 0.90, 0.12, 0.07, 0.76);

        // extras de cada nave
        switch (tipo) {
            case EXPLORADOR: // alerón trasero más alto
                rect(g, base, x, y, w, h, 0.02, 0.00, 0.10, 0.08);
                rect(g, base, x, y, w, h, 0.02, 0.92, 0.10, 0.08);
                break;
            case CAZA_ESTELAR: // doble alerón delantero
                rect(g, base, x, y, w, h, 0.80, 0.20, 0.05, 0.60);
                break;
            case ACORAZADO: // cañones al frente
                rect(g, Color.LIGHT_GRAY, x, y, w, h, 0.60, 0.5 - gr - 0.04, 0.38, 0.06);
                rect(g, Color.LIGHT_GRAY, x, y, w, h, 0.60, 0.5 + gr - 0.02, 0.38, 0.06);
                break;
        }

        // cabina del piloto
        g.setColor(new Color(190, 235, 255));
        g.fillOval(x + (int) (0.40 * w), y + (int) (0.40 * h), (int) (0.18 * w), (int) (0.20 * h));
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x + (int) (0.40 * w), y + (int) (0.40 * h), (int) (0.18 * w), (int) (0.20 * h));
    }
}