package monte_carlo_tree_search.domains.cart_pole;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_battery_cell.PlotPanel;
import org.jfree.data.xy.XYSeries;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TwoPanelsPlotter {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 230;

    PlotPanel plotPanel1;
    PlotPanel plotPanel2;

    public TwoPanelsPlotter(String plotLeftTitle, String plotRightTitle, String xAxisTitle) {

        JPanel redPanel = new JPanel();
        redPanel.setBackground(Color.RED);

        plotPanel1 = new PlotPanel("",xAxisTitle,plotLeftTitle);
        plotPanel2 = new PlotPanel("",xAxisTitle,plotRightTitle);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 2));
        frame.setTitle(plotLeftTitle);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
        frame.add(plotPanel1);
        frame.add(plotPanel2);
        frame.setVisible(true);
    }

    @SneakyThrows
    public void plot(java.util.List<Double> learningErrors, java.util.List<Double> returns) {
        XYSeries series1 = defineDataSeries(learningErrors);
        plotPanel1.setChartDataFromSeries(series1);

        XYSeries series2 = defineDataSeries(returns);
        plotPanel2.setChartDataFromSeries(series2);
    }

    private static XYSeries defineDataSeries(List<Double> doubleList) {
        XYSeries series = new XYSeries("");
        int x=0;
        for (Double value:doubleList) {
            series.add(x++, value);
        }
        return series;
    }

}
