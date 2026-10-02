package modelo;

import java.awt.Color;

public enum TipoNave {
    // nombre, dificultad, sleep, cooldown, color
    EXPLORADOR("Explorador", "Fácil", 10, 2000, new Color(0, 200, 255)),
    CAZA_ESTELAR("Caza Estelar", "Normal", 20, 1000, new Color(80, 220, 120)),
    ACORAZADO("Acorazado", "Difícil", 40, 300, new Color(255, 140, 0));

    private final String nombre;
    private final String dificultad;
    private final int sleepMovimiento;
    private final int cooldownDisparo;
    private final Color color;

    TipoNave(String nombre, String dificultad, int sleepMovimiento, int cooldownDisparo, Color color) {
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.sleepMovimiento = sleepMovimiento;
        this.cooldownDisparo = cooldownDisparo;
        this.color = color;
    }

    public String getNombre() { return nombre; }
    public String getDificultad() { return dificultad; }
    public int getSleepMovimiento() { return sleepMovimiento; }
    public int getCooldownDisparo() { return cooldownDisparo; }
    public Color getColor() { return color; }

    @Override
    public String toString() { return nombre + " (" + dificultad + ")"; }
}