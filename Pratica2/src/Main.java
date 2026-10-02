import juego.VentanaJuego;
import modelo.Piloto;
import modelo.TipoNave;

public class Main {
    public static void main(String[] args) {
        Piloto p = new Piloto("Prueba", TipoNave.ACORAZADO);
        new VentanaJuego(p);
    }
}

