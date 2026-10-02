package modelo;

public enum TipoNave {
    // nombre, dificultad, movimiento que tendran , cooldown(tiempo de espera) de disparo
    EXPLORADOR("Explorador", "Fácil", 10, 2000),
    CAZA_ESTELAR("Caza Estelar", "Normal", 20, 1000),
    ACORAZADO("Acorazado", "Difícil", 40, 300);

    private final String nombre;
    private final String dificultad;
    private final int sleepMovimiento;
    private final int cooldownDisparo;

    TipoNave(String nombre, String dificultad, int sleepMovimiento, int cooldownDisparo) {
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.sleepMovimiento = sleepMovimiento;
        this.cooldownDisparo = cooldownDisparo;
    }

    public String getNombre() { return nombre; }
    public String getDificultad() { return dificultad; }
    public int getSleepMovimiento() { return sleepMovimiento; }
    public int getCooldownDisparo() { return cooldownDisparo; }

    @Override
    public String toString() { return nombre + " (" + dificultad + ")"; }
}