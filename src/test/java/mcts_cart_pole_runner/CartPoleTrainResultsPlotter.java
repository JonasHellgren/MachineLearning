package mcts_cart_pole_runner;

import lombok.SneakyThrows;
import monte_carlo_tree_search.domains.models_battery_cell.EnvironmentCell;
import monte_carlo_tree_search.domains.models_battery_cell.PlotPanel;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CartPoleTrainResultsPlotter {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 200;
    private final String frameTitle;

    public CartPoleTrainResultsPlotter(String frameTitle) {
        this.frameTitle=frameTitle;
    }

    @SneakyThrows
    public void plot(java.util.List<Double> learningErrors, java.util.List<Double> returns) {

        JPanel redPanel = new JPanel();
        redPanel.setBackground(Color.RED);

        PlotPanel plotPanel1 = new PlotPanel("Error","Step","Error");
        XYSeries series1 = defineDataSeries("Error",learningErrors);
        plotPanel1.setChartDataFromSeries(series1);

        PlotPanel plotPanel2 = new PlotPanel("Return","Step","Return");
        XYSeries series2 = defineDataSeries("Return",returns);
        plotPanel2.setChartDataFromSeries(series2);


        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 2));
        frame.setTitle(frameTitle);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
        frame.add(plotPanel1);
        frame.add(plotPanel2);
        frame.setVisible(true);
    }

    private static XYSeries defineDataSeries(String name, List<Double> doubleList) {
        XYSeries series = new XYSeries(name);
        int x=0;
        for (Double value:doubleList) {
            series.add(x++, value);
        }
        return series;
    }

}
