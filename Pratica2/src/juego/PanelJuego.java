package juego;

import datos.Datos;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class PanelJuego extends JPanel implements KeyListener {
    public static final int ANCHO = 900;
    public static final int ALTO = 500;

    private static final int NAVE_W = 70;
    private static final int NAVE_H = 36;
    private static final int PASO = 5;

    private final Piloto piloto;
    private final TipoNave nave;
    private final Random rnd = new Random();

    // Vector de objetos en pantalla 
    private final ObjetoEspacial[] objetos = new ObjetoEspacial[200];

    // Vectores del fondo de estrellas
    private final int[] estrellaX = new int[60];
    private final int[] estrellaY = new int[60];
    private final int[] estrellaVel = new int[60];

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

        for (int i = 0; i < estrellaX.length; i++) {
            estrellaX[i] = rnd.nextInt(ANCHO);
            estrellaY[i] = rnd.nextInt(ALTO);
            estrellaVel[i] = 1 + rnd.nextInt(4);
        }
    }

    // -- HILOS -
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
                if (arriba && naveY > 30) naveY -= PASO;
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
            int y = 35 + rnd.nextInt(ALTO - 80);
            int r = rnd.nextInt(100);
            if (r < 45) {            // 45% enemigo
                agregar(new Enemigo(y, 3 + rnd.nextInt(4)));
            } else if (r < 70) {     // 25% asteroide
                agregar(new Asteroide(y));
            } else if (r < 88) {     // 18% quaffle
                agregar(new Quaffle(y));
            } else {                 // 12% snitch
                agregar(new Snitch(y));
            }
            dormir(700);
        }
    }

    // Ciclo principal:  colisiones y repintado
    private void hiloJuego() {
        while (enJuego) {
            moverEstrellas();
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
    private void moverEstrellas() {
        for (int i = 0; i < estrellaX.length; i++) {
            estrellaX[i] -= estrellaVel[i];
            if (estrellaX[i] < 0) {
                estrellaX[i] = ANCHO;
                estrellaY[i] = rnd.nextInt(ALTO);
            }
        }
    }

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
        Datos.partidas.agregar(new Partida(piloto.getNombre(), piloto.getNave().getNombre(), puntaje));
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "Fin del juego\nPiloto: " + piloto.getNombre() + "\nPuntaje: " + puntaje);
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
        });
    }

    // DIBUJO 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo degradado
        g2.setPaint(new GradientPaint(0, 0, new Color(5, 5, 25), 0, ALTO, new Color(25, 10, 55)));
        g2.fillRect(0, 0, ANCHO, ALTO);

        // Estrellas
        for (int i = 0; i < estrellaX.length; i++) {
            int b = 60 + estrellaVel[i] * 45;
            g2.setColor(new Color(b, b, b));
            int t = estrellaVel[i] > 2 ? 2 : 1;
            g2.fillRect(estrellaX[i], estrellaY[i], t, t);
        }

        // Nave (parpadea si es invulnerable, se pone morada si está bloqueada)
        long ahora = System.currentTimeMillis();
        boolean parpadeo = ahora < invulnerableHasta && (ahora / 100) % 2 == 0;
        if (!parpadeo) {
            Dibujo.dibujarNave(g2, nave, naveX, naveY, NAVE_W, NAVE_H, ahora < bloqueadoHasta);
        }

        // Objetos del juego
        synchronized (objetos) {
            for (int i = 0; i < objetos.length; i++) {
                if (objetos[i] != null && objetos[i].isActivo()) {
                    objetos[i].dibujar(g2);
                }
            }
        }

        // HUD
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, ANCHO, 28);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("Piloto: " + piloto.getNombre() + "  |  Nave: " + nave.getNombre(), 10, 19);
        g2.drawString("Puntaje: " + puntaje, 520, 19);
        for (int i = 0; i < vidas; i++) {   // una bolita roja por vida
            g2.setColor(Color.RED);
            g2.fillOval(700 + i * 25, 8, 14, 14);
        }
        if (ahora < bloqueadoHasta) {
            g2.setColor(Color.MAGENTA);
            g2.drawString("¡NAVE BLOQUEADA!", ANCHO / 2 - 60, 50);
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