package reportes;

import datos.Datos;
import modelo.Partida;
import modelo.Piloto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ReporteHTML {

    public static void generar(File carpeta) throws IOException {
        Partida[] top = Datos.partidas.getTop(10);

        // 1. La gráfica se exporta como imagen
        BufferedImage img = GraficaTop.crear(top).createBufferedImage(800, 450);
        ImageIO.write(img, "png", new File(carpeta, "grafica_top.png"));

        // 2. El HTML (UTF-8 para que salgan bien las tildes)
        File archivo = new File(carpeta, "reporte_quetzal.html");
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), "UTF-8"))) {

            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"es\"><head><meta charset=\"UTF-8\">");
            pw.println("<title>Reporte Quetzal Space Defender</title>");
            pw.println("<style>");
            pw.println("body{font-family:Arial,sans-serif;margin:30px;}");
            pw.println("h1{color:#1a237e;} h2{color:#283593;}");
            pw.println("table{border-collapse:collapse;width:100%;margin-bottom:25px;}");
            pw.println("th{background:#1a237e;color:white;padding:8px;}");
            pw.println("td{border:1px solid #999;padding:6px;text-align:center;}");
            pw.println("img{max-width:100%;}");
            pw.println("@media print{body{margin:10px;}}");
            pw.println("</style></head><body>");

            pw.println("<h1>Quetzal Space Defender - Reporte</h1>");
            pw.println("<h2>Top de puntajes</h2>");
            pw.println("<img src=\"grafica_top.png\" alt=\"Gráfica del top\">");
            escribirTablaPartidas(pw, top);

            pw.println("<h2>Historial de partidas</h2>");
            int n = Datos.partidas.getTotal();
            Partida[] todas = new Partida[n];
            for (int i = 0; i < n; i++) {
                todas[i] = Datos.partidas.get(i);
            }
            escribirTablaPartidas(pw, todas);

            pw.println("<h2>Pilotos registrados</h2>");
            pw.println("<table><tr><th>Piloto</th><th>Nave</th><th>Dificultad</th>"
                    + "<th>Partidas</th><th>Mejor puntaje</th></tr>");
            for (int i = 0; i < Datos.pilotos.getTotal(); i++) {
                Piloto p = Datos.pilotos.get(i);
                pw.println("<tr><td>" + p.getNombre() + "</td><td>" + p.getNave().getNombre()
                        + "</td><td>" + p.getNave().getDificultad() + "</td><td>"
                        + p.getPartidasJugadas() + "</td><td>" + p.getMejorPuntaje() + "</td></tr>");
            }
            pw.println("</table>");
            pw.println("</body></html>");
        }
    }

    private static void escribirTablaPartidas(PrintWriter pw, Partida[] datos) {
        pw.println("<table><tr><th>#</th><th>Piloto</th><th>Nave</th>"
                + "<th>Puntaje</th><th>Fecha</th></tr>");
        for (int i = 0; i < datos.length; i++) {
            pw.println("<tr><td>" + (i + 1) + "</td><td>" + datos[i].getPiloto()
                    + "</td><td>" + datos[i].getNave() + "</td><td>" + datos[i].getPuntaje()
                    + "</td><td>" + datos[i].getFecha() + "</td></tr>");
        }
        pw.println("</table>");
    }
}