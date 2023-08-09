package plotters;

import lombok.Builder;
import lombok.NonNull;
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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PlotterMultiplePanelsPairs {

    public static final Color COLOR_POINTS = Color.BLACK;
    public static final int NOF_ROWS = 1;
    public static final int WIDTH = 500, HEIGHT = 300;
    public static final String EMPTY_TITLE = "";
    public static final boolean LEGEND = false;

    @Builder
    public record Settings(
            @NonNull Integer width,
            @NonNull Integer height,
            @NonNull List<String> titleList,
            @NonNull List<String> xLabelList,
            @NonNull List<String> yLabelList
    ) {
        public static Settings newWithXYLabels(String xLabel, String yLabel) {
                   return Settings.builder()
                           .width(WIDTH).height(HEIGHT)
                           .titleList(List.of(EMPTY_TITLE))
                           .xLabelList(List.of(xLabel)).yLabelList(List.of(yLabel))
                           .build();
               }
    }

    JFrame frame;
    Settings settings;

    public PlotterMultiplePanelsPairs(String xLabel, String yLabel) {
        this(Settings.newWithXYLabels(xLabel,yLabel));
    }

    public PlotterMultiplePanelsPairs(Settings settings) {
        this.settings=settings;
        frame = new JFrame("");
    }

    public void plot(List<List<Pair<Double,Double>>> listOfDataPairs) {
        int nofTitles = settings.titleList().size();
        checkArguments(listOfDataPairs, nofTitles);

        List<JFreeChart> charts=new ArrayList<>();
        for(List<Pair<Double,Double>> dataPairs:listOfDataPairs) {
            int index = (nofTitles == 1) ? 0 : listOfDataPairs.indexOf(dataPairs);
            JFreeChart chart = createChart(dataPairs, index);
            charts.add(chart);
            setColorDataPoints(chart);
        }
        setChartsInFrame(charts, frame);
    }

    private void checkArguments(List<List<Pair<Double, Double>>> listOfDataPairs, int nofTitles) {
        int nofXlabels = settings.xLabelList().size();
        int nofYlabels = settings.yLabelList().size();
        int nofDataPairLists= listOfDataPairs.size();
        List<Integer> nofList=List.of(nofTitles,nofXlabels,nofYlabels,nofDataPairLists);

        int maxInList = nofList.stream().mapToInt(Integer::intValue).max().getAsInt();
        int minInList = nofList.stream().mapToInt(Integer::intValue).min().getAsInt();

        if (maxInList != minInList && nofTitles !=1) {
            throw  new IllegalArgumentException("Length of data list not aligned with title/label lists");
        }
    }

    private static void setColorDataPoints(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        int seriesIndex = 0;
        renderer.setSeriesPaint(seriesIndex, COLOR_POINTS);
    }

    @NotNull
    private  JFreeChart createChart(List<Pair<Double,Double>> dataPairs, int index) {
        XYSeries series = new XYSeries(EMPTY_TITLE);
        for (Pair<Double,Double> pair:dataPairs) {
            series.add(pair.getLeft(),pair.getRight());
        }

        XYDataset dataset = new XYSeriesCollection(series);

        return ChartFactory.createXYLineChart(
                settings.titleList.get(index),
                settings.xLabelList.get(index),settings.yLabelList.get(index),
                dataset,
                PlotOrientation.VERTICAL,
                LEGEND, true, false
        );
    }

    private  void setChartsInFrame(List<JFreeChart> charts, JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().removeAll();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(NOF_ROWS, charts.size()));

        for (JFreeChart chart:charts) {
            ChartPanel chartPanel = new ChartPanel(chart);
            panel.add(chartPanel);
        }

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(settings.width, settings.height);
        frame.setLocationRelativeTo(null);
        BufferedImage blankImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        frame.setIconImage(blankImage); // Remove the default upper-left icon
        frame.setVisible(true);
    }

}
