package monte_carlo_tree_search.domains.models_battery_cell;

import lombok.SneakyThrows;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CellResultsPlotter {

    private static final int WIDTH = 550;
    private static final int HEIGHT = 500;
    private final int timeOut;
    private final String frameTitle;

    public CellResultsPlotter(String frameTitle, int timeOut ) {
        this.frameTitle=frameTitle;
        this.timeOut=timeOut;
    }

    @SneakyThrows
    public void plot(List<EnvironmentCell.CellResults> resultsList) {

        JPanel redPanel = new JPanel();
        redPanel.setBackground(Color.RED);

        PlotPanel plotPanel1 = new PlotPanel("Current","Time (s)","Current");
        XYSeries series1 = defineDataSeries1(resultsList);
        plotPanel1.setChartDataFromSeries(series1);

        PlotPanel plotPanel2 = new PlotPanel("SoC","Time (s)","Current");
        XYSeries series2 = defineDataSeries2(resultsList);
        plotPanel2.setChartDataFromSeries(series2);

        PlotPanel plotPanel3 = new PlotPanel("Power","Time (s)","Power");
        XYSeries series3 = defineDataSeries3(resultsList);
        plotPanel3.setChartDataFromSeries(series3);

        PlotPanel plotPanel4 = new PlotPanel("Temp","Time (s)","Temp");
        XYSeries series4 = defineDataSeries4(resultsList);
        plotPanel4.setChartDataFromSeries(series4);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 2));
        frame.setTitle(frameTitle);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);
        frame.add(plotPanel1);
        frame.add(plotPanel2);
        frame.add(plotPanel3);
        frame.add(plotPanel4);

        frame.setVisible(true);

        TimeUnit.MILLISECONDS.sleep(timeOut);


    }

    private static XYSeries defineDataSeries1(List<EnvironmentCell.CellResults> resultsList) {
        XYSeries series = new XYSeries("Current");
        for (EnvironmentCell.CellResults result:resultsList) {
            series.add(result.newTime, result.current);
        }
        return series;
    }

    private static XYSeries defineDataSeries2(List<EnvironmentCell.CellResults> resultsList) {
        XYSeries series = new XYSeries("SOC");
        for (EnvironmentCell.CellResults result:resultsList) {
            series.add(result.newTime, result.newSoC);
        }
        return series;
    }

    private static XYSeries defineDataSeries3(List<EnvironmentCell.CellResults> resultsList) {
        XYSeries series = new XYSeries("Power");
        for (EnvironmentCell.CellResults result:resultsList) {
            series.add(result.newTime, result.power);
        }
        return series;
    }

    private static XYSeries defineDataSeries4(List<EnvironmentCell.CellResults> resultsList) {
        XYSeries series = new XYSeries("Temp");
        for (EnvironmentCell.CellResults result:resultsList) {
            series.add(result.newTime, result.newTemperature);
        }
        return series;
    }

}
