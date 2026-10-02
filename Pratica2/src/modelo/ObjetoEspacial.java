package modelo;

import java.awt.Graphics2D;
import java.awt.Rectangle;

// Cada objeto es un hilo que actualiza su propia posición (x, y)
public abstract class ObjetoEspacial implements Runnable {
    public static final int LIMITE_DERECHO = 900;

    protected volatile int x;
    protected volatile int y;
    protected final int ancho;
    protected final int alto;
    protected final int velocidad;
    protected final int direccion; // -1 = izquierda, 1 = derecha
    protected volatile boolean activo = true;

    public ObjetoEspacial(int x, int y, int ancho, int alto, int velocidad, int direccion) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = velocidad;
        this.direccion = direccion;
    }

    public void iniciar() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (activo) {
            x += velocidad * direccion;
            // Si sale de la pantalla, el hilo termina
            if (x + ancho < 0 || x > LIMITE_DERECHO) {
                activo = false;
            }
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                activo = false;
            }
        }
    }

    public Rectangle getRect() { return new Rectangle(x, y, ancho, alto); }
    public void destruir() { activo = false; }
    public boolean isActivo() { return activo; }

    public abstract void dibujar(Graphics2D g);
}