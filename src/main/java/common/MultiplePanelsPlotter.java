package common;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.domains.models_battery_cell.PlotPanel;
import org.jfree.data.xy.XYSeries;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Log
public class MultiplePanelsPlotter {

    private static final int WIDTH_PANEL = 300;
    private static final int HEIGHT = 220;

    int nofPanels;
    List<PlotPanel> panels;

    public MultiplePanelsPlotter(List<String> titleList, String xAxisTitle) {
        nofPanels=titleList.size();
        panels=new ArrayList<>();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, nofPanels));
        frame.setTitle("");
        frame.setSize(WIDTH_PANEL*nofPanels, HEIGHT);
        frame.setVisible(true);
        for (int i = 0; i < nofPanels ; i++) {
            PlotPanel panel=new PlotPanel("",xAxisTitle,titleList.get(i));
            panels.add(panel);
            frame.add(panel);
        }
        frame.setVisible(true);
    }

    @SneakyThrows
    public void plot(List<List<Double>> listOfTrajectories) {

        if (listOfTrajectories.size()!=nofPanels) {
            log.warning("Nof trajectories does not match nof plot titles");
        } else
        {
            for (int i = 0; i < nofPanels ; i++) {
                XYSeries series1 = defineDataSeries(listOfTrajectories.get(i));
                panels.get(i).setChartDataFromSeries(series1);

            }
        }
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
