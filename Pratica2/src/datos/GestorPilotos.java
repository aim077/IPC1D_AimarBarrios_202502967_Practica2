package datos;

import modelo.Piloto;
import modelo.TipoNave;

public class GestorPilotos {
    private Piloto[] pilotos = new Piloto[5];
    private int total = 0;

    // Devuelve null si todo salió bien; si no, el mensaje de error
    public String crear(String nombre, TipoNave nave) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre no puede estar vacío.";
        }
        nombre = nombre.trim();
        if (nombre.length() < 3 || nombre.length() > 15) {
            return "El nombre debe tener entre 3 y 15 caracteres.";
        }
        if (!nombre.matches("[A-Za-z0-9_]+")) {
            return "Solo se permiten letras, números y guion bajo (sin espacios ni tildes).";
        }
        if (nave == null) {
            return "Debes seleccionar un modelo de nave.";
        }
        if (buscar(nombre) != null) {
            return "Ya existe un piloto con ese nombre.";
        }
        if (total == pilotos.length) {
            ampliar();
        }
        pilotos[total] = new Piloto(nombre, nave);
        total++;
        return null;
    }

    public Piloto buscar(String nombre) {
        for (int i = 0; i < total; i++) {
            if (pilotos[i].getNombre().equalsIgnoreCase(nombre)) {
                return pilotos[i];
            }
        }
        return null;
    }

    // Cuando el vector se llena, se crea uno del doble de tamaño y se copian los datos
    private void ampliar() {
        Piloto[] nuevo = new Piloto[pilotos.length * 2];
        for (int i = 0; i < total; i++) {
            nuevo[i] = pilotos[i];
        }
        pilotos = nuevo;
    }

    public Piloto get(int i) { return pilotos[i]; }
    public int getTotal() { return total; }
}