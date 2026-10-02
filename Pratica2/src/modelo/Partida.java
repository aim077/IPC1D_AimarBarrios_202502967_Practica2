package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Partida {
    private final String piloto;
    private final String nave;
    private final int puntaje;
    private final String fecha;

    public Partida(String piloto, String nave, int puntaje) {
        this.piloto = piloto;
        this.nave = nave;
        this.puntaje = puntaje;
        this.fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getPiloto() { return piloto; }
    public String getNave() { return nave; }
    public int getPuntaje() { return puntaje; }
    public String getFecha() { return fecha; }
}