package common;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.jfree.data.xy.XYSeries;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.twelvemonkeys.image.ImageUtil.getHeight;
import static com.twelvemonkeys.image.ImageUtil.getWidth;

@Log
public class MultiplePanelsPlotter {

    private static final int WIDTH_PANEL = 300;
    private static final int HEIGHT = 220;

    int nofPanels;
    List<PlotPanel> panels;
    JFrame frame;

    public MultiplePanelsPlotter(List<String> titleList, String xAxisTitle) {
        nofPanels=titleList.size();
        panels=new ArrayList<>();

        frame = new JFrame();
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

    @SneakyThrows
    public void saveImage(String fileName) {
        try
        {
            BufferedImage image = new BufferedImage(WIDTH_PANEL, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            frame.paint(graphics2D);
            ImageIO.write(image,"jpeg", new File(fileName+".jpeg"));
        }
        catch(Exception exception)
        {
            throw new IOException("Unable to save image, file = "+fileName);
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
