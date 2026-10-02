package ui;

import datos.Datos;
import datos.GestorPilotos;
import juego.VentanaJuego;
import modelo.Piloto;
import modelo.TipoNave;

import javax.swing.*;
import java.awt.*;

public class VentanaMenu extends JFrame {

    public VentanaMenu() {
        super("Quetzal Space Defender");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 360);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panel.setBackground(Color.BLACK);

        JLabel titulo = new JLabel("QUETZAL SPACE DEFENDER", SwingConstants.CENTER);
        titulo.setForeground(Color.CYAN);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnJugar = new JButton("Jugar");
        JButton btnCrear = new JButton("Crear Piloto");
        JButton btnTop = new JButton("Top de Puntajes");
        JButton btnSalir = new JButton("Salir");

        btnJugar.addActionListener(e -> jugar());
        btnCrear.addActionListener(e -> crearPiloto());
        btnTop.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Próximamente")); // se cambia en el commit 7
        btnSalir.addActionListener(e -> System.exit(0));

        panel.add(titulo);
        panel.add(btnJugar);
        panel.add(btnCrear);
        panel.add(btnTop);
        panel.add(btnSalir);
        add(panel);
    }

    private void jugar() {
        GestorPilotos g = Datos.pilotos;
        if (g.getTotal() == 0) {
            JOptionPane.showMessageDialog(this, "Primero debes crear un piloto.",
                    "Sin pilotos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JComboBox<String> cmbPilotos = new JComboBox<>();
        for (int i = 0; i < g.getTotal(); i++) {
            cmbPilotos.addItem(g.get(i).getNombre() + " - " + g.get(i).getNave().getNombre());
        }
        int op = JOptionPane.showConfirmDialog(this, cmbPilotos, "Elige tu piloto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) return;

        Piloto elegido = g.get(cmbPilotos.getSelectedIndex());
        setVisible(false);
        new VentanaJuego(elegido, this);
    }

    private void crearPiloto() {
        JTextField txtNombre = new JTextField(15);
        JComboBox<TipoNave> cmbNave = new JComboBox<>(TipoNave.values());

        JPanel form = new JPanel(new GridLayout(4, 1, 5, 5));
        form.add(new JLabel("Nombre del piloto:"));
        form.add(txtNombre);
        form.add(new JLabel("Modelo de nave (define la dificultad):"));
        form.add(cmbNave);

        // Se repite hasta que los datos sean válidos o el usuario cancele
        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form, "Crear piloto",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (op != JOptionPane.OK_OPTION) return;

            String error = Datos.pilotos.crear(txtNombre.getText(),
                    (TipoNave) cmbNave.getSelectedItem());
            if (error == null) {
                JOptionPane.showMessageDialog(this, "Piloto creado correctamente.");
                return;
            }
            JOptionPane.showMessageDialog(this, error, "Datos inválidos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
