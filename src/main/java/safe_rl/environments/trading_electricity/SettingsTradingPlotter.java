package safe_rl.environments.trading_electricity;

import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

public class SettingsTradingPlotter {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;
    public static final int N_PANELS = 2;
    public static final int SIZE_FONT = 12;
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, SIZE_FONT);
    public static final Font FONT_PLAIN = new Font("SansSerif", Font.PLAIN, SIZE_FONT);

    public void plot(SettingsTrading settings) {
        var textPanel = getTextPanel(settings);
        var plotPanel = getChartPanel(settings);
        createAndShowFrame(textPanel, plotPanel);
    }

    private static void createAndShowFrame(JPanel leftPanel, ChartPanel chartPanel) {
        JFrame frame = new JFrame("Trade data");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new GridLayout(1, N_PANELS));
        frame.add(leftPanel);
        frame.add(chartPanel);
        frame.setVisible(true);
    }

    @NotNull
    private static JPanel getTextPanel(SettingsTrading settings) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(createLabel("Energy batt. (kWh):" + settings.energyBatt()));
        leftPanel.add(createLabel("Power batt. max (kW):"+ settings.powerBattMax()));
        leftPanel.add(createLabel("Price batt. (Euro):"+ settings.priceBattery()));
        leftPanel.add(createLabel("nCyclesLifetime. (-):"+ settings.nCyclesLifetime()));
        leftPanel.add(createLabel("socTerminalMin (%):"+ settings.socTerminalMin()*100));
        leftPanel.add(createLabel("PowerCapacity Fcr. (kW):"+ settings.powerCapacityFcr()));
        leftPanel.add(createLabel("priceFCR. (Euro/kW/h):"+ settings.priceFCR()));
        leftPanel.add(createLabel("stdActivationFCR. (-):"+ settings.stdActivationFCR()));
        leftPanel.add(createLabel("dt (hour):"+ settings.dt()));
        return leftPanel;
    }

    @NotNull
    private static ChartPanel getChartPanel(SettingsTrading settings) {
        XYSeries series = new XYSeries("Price FCR");
        IntStream.range(0, settings.priceTraj().length)
                .forEach(i -> series.add(i * settings.dt(), settings.priceTraj()[i]));
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "", "Time", "Price (E/kWh/h)", dataset);
        chart.getLegend().setVisible(false);
        setFontSize(chart);
        return new ChartPanel(chart);
    }

    @NotNull
    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_PLAIN);
        return label;
    }

    private static void setFontSize(JFreeChart chart) {
        Font labelFont = FONT_PLAIN;
        chart.getTitle().setFont(FONT_BOLD);
        chart.getLegend().setItemFont(labelFont);
        chart.getLegend().setFrame(BlockBorder.NONE);
        NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
        xAxis.setLabelFont(labelFont);
        NumberAxis yAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        yAxis.setLabelFont(labelFont);
    }

}
