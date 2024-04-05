package common.plotters;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class PlotPanel extends JPanel  {
    private static final int WIDTH1 = 300;
    private static final int HEIGHT1 = 200;
    private final int MARGIN=5;
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
        XYPlot plot = (XYPlot) ch.getPlot();
        plot.setRangeGridlinePaint(Color.darkGray);
        AbstractRenderer r1 = (AbstractRenderer) plot.getRenderer(0);
        r1.setSeriesPaint(0, Color.black);
        ChartPanel cp = new ChartPanel(ch);
        cp.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        cp.setPreferredSize(new Dimension(WIDTH1, HEIGHT1));
        add(cp);
    }


    public void setChartDataFromSeries(XYSeries series) {
        this.dataset = new XYSeriesCollection(series);
        XYPlot plot = (XYPlot) ch.getPlot();
        plot.setDataset(this.dataset);
    }

    private JFreeChart createChart(XYDataset dataset) {

        return ChartFactory.createXYLineChart(
                title,
                xlabel,
                ylabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

}
