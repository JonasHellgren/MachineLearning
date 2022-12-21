package monte_carlo_tree_search.domains.models_battery_cell;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class PlotPanel extends JPanel  {
    private final int MARGIN=15;
    private final Color BACKGROUND_COLOR=Color.GRAY;
    String title, xlabel, ylabel;
    XYDataset dataset;
    JFreeChart ch;

    public PlotPanel(String title, String xlabel, String ylabel) {
        this.title=title;
        this.xlabel=xlabel;
        this.ylabel=ylabel;

        this.dataset = new XYSeriesCollection();
        addChart(this.dataset);
    }

    public void addChart(XYDataset dataset) {
        ch = createChart(dataset);
        ChartPanel cp = new ChartPanel(ch);
        cp.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        cp.setBackground(BACKGROUND_COLOR);
        add(cp);
    }


    public void setChartDataFromSeries(XYSeries series) {
        this.dataset = new XYSeriesCollection(series);
        XYPlot plot = (XYPlot) ch.getPlot();
        plot.setDataset(this.dataset);
    }

    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart plotChart = ChartFactory.createXYLineChart(
                title,
                xlabel,
                ylabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        return plotChart;
    }

}
