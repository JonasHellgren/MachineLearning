package swing;

import javafx.geometry.Pos;

import javax.swing.*;
import java.awt.*;

public class PanelMountainCar extends JPanel {

    ScaleLinear xScaler;
    ScaleLinear yScaler;
    LineData roadData;
    public Position2D carPosition;
    double carRadius;


    public PanelMountainCar(ScaleLinear xScaler,
                            ScaleLinear yScaler,
                            LineData roadData,
                            Position2D carPosition,
                            double carRadius) {
        this.xScaler = xScaler;
        this.yScaler = yScaler;
        this.roadData = roadData;
        this.carPosition=carPosition;
        this.carRadius=carRadius;
    }

    public void drawPlot(Graphics2D g2d) {
        plotLine(g2d,roadData.xData,roadData.yData);
        plotCar(g2d,carPosition.x, carPosition.y);
    }

    public void setCarPosition(double x, double y) {
        carPosition.x=x;   carPosition.y=y;
    }

    private void plotLine(Graphics2D g2d,double[] line1x,double[] line1y) {
            ////flip because swing defines y=0 as upper in panel
            g2d.drawPolyline(xScaler.calcOut(line1x),yScaler.calcOut(line1y), line1x.length);
    }

    private void plotCar(Graphics2D g2d,double x,double y) {
        //g2d.setColor('r');

        g2d.drawOval((int) (xScaler.calcOut(x)+-1*xScaler.scale(carRadius)/2),
                (int) (yScaler.calcOut(y)+-1*xScaler.scale(carRadius)/2),
                xScaler.scale(carRadius),yScaler.scale(carRadius));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);  //cleans the screen
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawPlot(g2d);

    }

}
