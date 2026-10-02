package datos;

import modelo.Partida;

public class GestorPartidas {
    private Partida[] partidas = new Partida[10];
    private int total = 0;

    // synchronized porque la partida termina en un hilo distinto al de la interfaz
    public synchronized void agregar(Partida p) {
        if (total == partidas.length) {
            Partida[] nuevo = new Partida[partidas.length * 2];
            for (int i = 0; i < total; i++) {
                nuevo[i] = partidas[i];
            }
            partidas = nuevo;
        }
        partidas[total] = p;
        total++;
    }

    public synchronized Partida get(int i) { return partidas[i]; }
    public synchronized int getTotal() { return total; }

    // Devuelve las n mejores partidas ordenadas de mayor a menor (burbuja)
    public synchronized Partida[] getTop(int n) {
        Partida[] copia = new Partida[total];
        for (int i = 0; i < total; i++) {
            copia[i] = partidas[i];
        }
        for (int i = 0; i < copia.length - 1; i++) {
            for (int j = 0; j < copia.length - 1 - i; j++) {
                if (copia[j].getPuntaje() < copia[j + 1].getPuntaje()) {
                    Partida aux = copia[j];
                    copia[j] = copia[j + 1];
                    copia[j + 1] = aux;
                }
            }
        }
        int tam = Math.min(n, total);
        Partida[] top = new Partida[tam];
        for (int i = 0; i < tam; i++) {
            top[i] = copia[i];
        }
        return top;
    }
}