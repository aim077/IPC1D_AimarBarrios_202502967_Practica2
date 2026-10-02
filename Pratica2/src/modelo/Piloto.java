package modelo;

public class Piloto {
    private final String nombre;
    private final TipoNave nave;
    private int partidasJugadas;
    private int mejorPuntaje;

    public Piloto(String nombre, TipoNave nave) {
        this.nombre = nombre;
        this.nave = nave;
    }

    public void registrarPartida(int puntaje) {
        partidasJugadas++;
        if (puntaje > mejorPuntaje) {
            mejorPuntaje = puntaje;
        }
    }

    public String getNombre() { return nombre; }
    public TipoNave getNave() { return nave; }
    public int getPartidasJugadas() { return partidasJugadas; }
    public int getMejorPuntaje() { return mejorPuntaje; }
}