package reportes;

import modelo.Partida;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class GraficaTop {

    public static JFreeChart crear(Partida[] top) {
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        for (int i = 0; i < top.length; i++) {
            // El número evita que dos partidas del mismo piloto se sobrescriban
            datos.addValue(top[i].getPuntaje(), "Puntaje",
                    (i + 1) + ". " + top[i].getPiloto());
        }
        return ChartFactory.createBarChart(
                "Top de mejores puntajes", "Piloto", "Puntos",
                datos, PlotOrientation.VERTICAL, false, true, false);
    }
}