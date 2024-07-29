package safe_rl.environments.trading_electricity;

import com.beust.jcommander.internal.Lists;
import common.list_arrays.ListUtils;
import lombok.AllArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import safe_rl.domain.agent.aggregates.DisCoMemory;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class TradingMemoryPlotter<V> {

    public static final int WEIGHT = 150;
    public static final int HEIGHT = 200;
    public static final int FONT_SIZE = 18;
    public static final String FONT_NAME = "SansSerif";
    DisCoMemory<VariablesTrading> memory;
    String yLabel;
    int tEnd;


    public void plot() {
        // Create datasets and charts for each series
        JFrame frame = new JFrame(yLabel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        List<Double> socs = ListUtils.doublesStartEndStep(0.0, 1.0, 0.1);
        List<Double> valueSpace = getValueSpace(socs);
        for (int ti = 0; ti <= tEnd; ti++) {
            ChartPanel panel = getChartPanel(socs, valueSpace, ti);
            frame.add(panel);
        }
        displayFrame(frame);
    }

    ChartPanel getChartPanel(List<Double> socs, List<Double> valueSpace, int ti) {
        var series = new XYSeries("Time=" + ti);
        defineDataInSeries(socs, ti, series);
        JFreeChart chart = createChart(valueSpace, series);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(WEIGHT, HEIGHT));
        return panel;
    }

    static void displayFrame(JFrame frame) {
        frame.setVisible(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    /**
     *  Extract all values to get min max
     */
    List<Double> getValueSpace(List<Double> socs) {
        List<Double> valueSpace = Lists.newArrayList();
        for (int ti = 0; ti <= tEnd; ti++) {
            for (Double soc : socs) {
                StateTrading state = StateTrading.of(VariablesTrading.newTimeSoc(ti, soc));
                valueSpace.add(memory.read(state));
            }
        }
        return valueSpace;
    }

    JFreeChart createChart(List<Double> values, XYSeries series) {
        JFreeChart chart = getChart(series);
        chart.removeLegend();
        XYPlot plot = chart.getXYPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(Collections.min(values), Collections.max(values)); // Setting the y-axis range from 0.0 to 5.0
        // Adjusting font size of the axis labels
        Font axisLabelFont = new Font(FONT_NAME, Font.BOLD, FONT_SIZE);
        plot.getDomainAxis().setLabelFont(axisLabelFont); // X-axis
        plot.getRangeAxis().setLabelFont(axisLabelFont); // Y-axis
        // Adjusting font size of the tick labels
        Font tickLabelFont = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE);
        plot.getDomainAxis().setTickLabelFont(tickLabelFont); // X-axis
        plot.getRangeAxis().setTickLabelFont(tickLabelFont); // Y-axis
        return chart;
    }

    private void defineDataInSeries(List<Double> socs, int ti, XYSeries series) {
        for (Double soc : socs) {
            StateTrading state = StateTrading.of(VariablesTrading.newTimeSoc(ti, soc));
            series.add((double) soc, memory.read(state));
        }
    }

    JFreeChart getChart(XYSeries series) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return ChartFactory.createXYLineChart(
                series.getKey().toString(),  // Chart title
                "soc",                         // X-Axis label
                yLabel,                         // Y-Axis label
                dataset,                     // Dataset
                PlotOrientation.VERTICAL,
                true,                        // Show legend
                true,                        // Use tooltips
                false                        // Configure chart to generate URLs?
        );
    }


}
