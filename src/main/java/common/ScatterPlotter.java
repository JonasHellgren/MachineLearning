package common;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ScatterPlotter {
    private static final int WIDTH_PANEL = 500, HEIGHT = 400;
    public static final String TITLE = "";
    public static final Color COLOR_POINTS = Color.BLACK;
    String xAxisTitle,  yAxisTitle;
    JFrame frame;

    public ScatterPlotter(String xAxisTitle, String yAxisTitle) {

        this.xAxisTitle=xAxisTitle; this.yAxisTitle=yAxisTitle;
        frame = new JFrame("");
    }

    public void plot(List<Pair<Double,Double>> dataPairs) {
        JFreeChart chart = getjFreeChart(dataPairs);
        setColorDataPoints(chart);
        setChartInFrame(chart, frame);
    }

    private static void setColorDataPoints(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, COLOR_POINTS);
    }

    @NotNull
    private  JFreeChart getjFreeChart(List<Pair<Double,Double>> dataPairs) {
        XYSeries series = new XYSeries(TITLE);
        for (Pair<Double,Double> pair:dataPairs) {
            series.add(pair.getLeft(),pair.getRight());
        }

        XYDataset dataset = new XYSeriesCollection(series);

        return ChartFactory.createScatterPlot(
                "",
                xAxisTitle,yAxisTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    private static void setChartInFrame(JFreeChart chart, JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setSize(WIDTH_PANEL, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
