package ui;

import datos.Datos;
import modelo.Partida;
import org.jfree.chart.ChartPanel;
import reportes.GraficaTop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaTop extends JFrame {

    public VentanaTop() {
        super("Top de puntajes e historial");
        setSize(800, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Top 10", crearPanelTop());
        pestanas.addTab("Historial", crearPanelHistorial());
        add(pestanas, BorderLayout.CENTER);
    }

    private JPanel crearPanelTop() {
        Partida[] top = Datos.partidas.getTop(10);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new ChartPanel(GraficaTop.crear(top)), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(crearTabla(top));
        scroll.setPreferredSize(new Dimension(700, 190));
        panel.add(scroll, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelHistorial() {
        int n = Datos.partidas.getTotal();
        Partida[] todas = new Partida[n];
        for (int i = 0; i < n; i++) {
            todas[i] = Datos.partidas.get(i);
        }
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(crearTabla(todas)), BorderLayout.CENTER);
        return panel;
    }

    private JTable crearTabla(Partida[] datos) {
        String[] columnas = {"#", "Piloto", "Nave", "Puntaje", "Fecha"};
        Object[][] filas = new Object[datos.length][5];
        for (int i = 0; i < datos.length; i++) {
            filas[i][0] = i + 1;
            filas[i][1] = datos[i].getPiloto();
            filas[i][2] = datos[i].getNave();
            filas[i][3] = datos[i].getPuntaje();
            filas[i][4] = datos[i].getFecha();
        }
        return new JTable(new DefaultTableModel(filas, columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });
    }
}