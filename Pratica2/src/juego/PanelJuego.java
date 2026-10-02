package juego;

import modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class PanelJuego extends JPanel implements KeyListener {
    public static final int ANCHO = 900;
    public static final int ALTO = 500;

    private static final int NAVE_W = 50;
    private static final int NAVE_H = 30;
    private static final int PASO = 5;

    private final Piloto piloto;
    private final TipoNave nave;
    private final Random rnd = new Random();

    // Vector de objetos en pantalla (sin ArrayList, como pide la práctica)
    private final ObjetoEspacial[] objetos = new ObjetoEspacial[200];

    private volatile int naveX = 40;
    private volatile int naveY = ALTO / 2;
    private volatile boolean arriba, abajo, izquierda, derecha;

    private volatile boolean enJuego = true;
    private volatile boolean cancelado = false;
    private volatile int puntaje = 0;
    private volatile int vidas = 3;
    private volatile long bloqueadoHasta = 0;
    private volatile long invulnerableHasta = 0;
    private volatile long ultimoDisparo = 0;

    public PanelJuego(Piloto piloto) {
        this.piloto = piloto;
        this.nave = piloto.getNave();
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    // ---------- HILOS ----------
    public void iniciar() {
        new Thread(this::hiloMovimientoNave).start();
        new Thread(this::hiloGenerador).start();
        new Thread(this::hiloJuego).start();
    }

    public void detener() {
        cancelado = true;
        enJuego = false;
    }

    // Mueve la nave; el sleep depende del tipo de nave
    private void hiloMovimientoNave() {
        while (enJuego) {
            if (System.currentTimeMillis() >= bloqueadoHasta) {
                if (arriba && naveY > 0) naveY -= PASO;
                if (abajo && naveY < ALTO - NAVE_H) naveY += PASO;
                if (izquierda && naveX > 0) naveX -= PASO;
                if (derecha && naveX < ANCHO / 2) naveX += PASO;
            }
            dormir(nave.getSleepMovimiento());
        }
    }

    // Genera objetos en el borde derecho
    private void hiloGenerador() {
        while (enJuego) {
            int y = rnd.nextInt(ALTO - 40);
            int r = rnd.nextInt(100);
            if (r < 50) {
                agregar(new Enemigo(y, 3 + rnd.nextInt(4)));
            } else if (r < 75) {
                agregar(new Asteroide(y));
            } else if (r < 95) {
                agregar(new Quaffle(y));
            } else {
                agregar(new Snitch(y));
            }
            dormir(700);
        }
    }

    // Ciclo principal: colisiones y repintado
    private void hiloJuego() {
        while (enJuego) {
            revisarColisiones();
            repaint();
            dormir(16);
        }
        finalizar();
    }

    private void dormir(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            enJuego = false;
        }
    }

    // ---------- LÓGICA ----------
    private void agregar(ObjetoEspacial o) {
        synchronized (objetos) {
            for (int i = 0; i < objetos.length; i++) {
                if (objetos[i] == null || !objetos[i].isActivo()) {
                    objetos[i] = o;
                    o.iniciar();
                    return;
                }
            }
        }
    }

    private void disparar() {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoDisparo >= nave.getCooldownDisparo()) {
            ultimoDisparo = ahora;
            agregar(new Proyectil(naveX + NAVE_W, naveY + NAVE_H / 2 - 2));
        }
    }

    private void revisarColisiones() {
        Rectangle rNave = new Rectangle(naveX, naveY, NAVE_W, NAVE_H);
        synchronized (objetos) {
            for (int i = 0; i < objetos.length; i++) {
                ObjetoEspacial a = objetos[i];
                if (a == null || !a.isActivo()) continue;

                if (a instanceof Proyectil) {
                    // El proyectil contra enemigos y asteroides
                    for (int j = 0; j < objetos.length; j++) {
                        ObjetoEspacial b = objetos[j];
                        if (b == null || !b.isActivo()) continue;
                        if (!a.getRect().intersects(b.getRect())) continue;
                        if (b instanceof Enemigo) {
                            a.destruir();
                            b.destruir();
                            puntaje += 5;
                            break;
                        } else if (b instanceof Asteroide) {
                            a.destruir(); // el asteroide absorbe el disparo
                            break;
                        }
                    }
                } else if (a.getRect().intersects(rNave)) {
                    procesarChoque(a);
                }
            }
        }
    }

    private void procesarChoque(ObjetoEspacial a) {
        a.destruir();
        if (a instanceof Enemigo) {
            perderVida();
        } else if (a instanceof Asteroide) {
            bloqueadoHasta = System.currentTimeMillis() + 2000; // bloqueo 2 seg
        } else if (a instanceof Quaffle) {
            puntaje += 10;
        } else if (a instanceof Snitch) {
            puntaje += 150;
            for (int i = 0; i < objetos.length; i++) {
                if (objetos[i] instanceof Enemigo && objetos[i].isActivo()) {
                    objetos[i].destruir();
                }
            }
        }
    }

    private void perderVida() {
        long ahora = System.currentTimeMillis();
        if (ahora < invulnerableHasta) return;
        vidas--;
        invulnerableHasta = ahora + 1000;
        if (vidas <= 0) {
            enJuego = false;
        }
    }

    private void finalizar() {
        if (cancelado) return;
        piloto.registrarPartida(puntaje);
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "Fin del juego\nPiloto: " + piloto.getNombre() + "\nPuntaje: " + puntaje);
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
        });
    }

    // ---------- DIBUJO ----------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Nave (parpadea si es invulnerable)
        boolean parpadeo = System.currentTimeMillis() < invulnerableHasta
                && (System.currentTimeMillis() / 100) % 2 == 0;
        if (!parpadeo) {
            g2.setColor(System.currentTimeMillis() < bloqueadoHasta ? Color.MAGENTA : Color.CYAN);
            int[] xs = {naveX, naveX, naveX + NAVE_W};
            int[] ys = {naveY, naveY + NAVE_H, naveY + NAVE_H / 2};
            g2.fillPolygon(xs, ys, 3);
        }

        synchronized (objetos) {
            for (int i = 0; i < objetos.length; i++) {
                if (objetos[i] != null && objetos[i].isActivo()) {
                    objetos[i].dibujar(g2);
                }
            }
        }

        // HUD
        g2.setColor(Color.WHITE);
        g2.drawString("Piloto: " + piloto.getNombre() + " | Nave: " + nave.getNombre(), 10, 20);
        g2.drawString("Puntaje: " + puntaje + "   Vidas: " + vidas, 10, 38);
        if (System.currentTimeMillis() < bloqueadoHasta) {
            g2.setColor(Color.MAGENTA);
            g2.drawString("¡NAVE BLOQUEADA!", ANCHO / 2 - 50, 20);
        }
    }

    // ---------- TECLADO ----------
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: case KeyEvent.VK_W: arriba = true; break;
            case KeyEvent.VK_DOWN: case KeyEvent.VK_S: abajo = true; break;
            case KeyEvent.VK_LEFT: case KeyEvent.VK_A: izquierda = true; break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: derecha = true; break;
            case KeyEvent.VK_SPACE: disparar(); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: case KeyEvent.VK_W: arriba = false; break;
            case KeyEvent.VK_DOWN: case KeyEvent.VK_S: abajo = false; break;
            case KeyEvent.VK_LEFT: case KeyEvent.VK_A: izquierda = false; break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: derecha = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }
}