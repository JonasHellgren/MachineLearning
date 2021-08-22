package java_ai_gym.swing;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class PanelMountainCarAnimation extends JPanel {


    ScaleLinear xScaler;
    ScaleLinear yScaler;
    LineData roadData;
    public Position2D carPosition;
    double velocity;
    public JLabel  label;

    double CAR_RADIUS;

    public PanelMountainCarAnimation(ScaleLinear xScaler,
                                     ScaleLinear yScaler,
                                     LineData roadData,
                                     Position2D carPosition,
                                     double CAR_RADIUS) {
        this.xScaler = xScaler;
        this.yScaler = yScaler;
        this.roadData = roadData;
        this.carPosition=carPosition;
        this.CAR_RADIUS = CAR_RADIUS;
    }

    public void drawPlot(Graphics2D g2d) {
        plotLine(g2d,roadData.xData,roadData.yData);
        plotCar(g2d,carPosition.x, carPosition.y);
    }

    public void setCarStates(double x, double y,double velocity) {
        carPosition.x=x;   carPosition.y=y; this.velocity = velocity;
    }


    private void plotLine(Graphics2D g2d, double[] line1x, double[] line1y) {
            ////flip because swing defines y=0 as upper in panel
            g2d.drawPolyline(xScaler.calcOut(line1x),yScaler.calcOut(line1y), line1x.length);
    }

    private void plotCar(Graphics2D g2d,double x,double y) {
        //g2d.setColor('r');

        g2d.drawOval((int) (xScaler.calcOut(x)+-1*xScaler.scale(CAR_RADIUS)/2),
                (int) (yScaler.calcOut(y)+-1*xScaler.scale(CAR_RADIUS)/2),
                xScaler.scale(CAR_RADIUS),yScaler.scale(CAR_RADIUS));
    }

    private void textCarStates(Graphics2D g2d,double x,double y, double velocity)  {
        label.setText("pos x:"+new DecimalFormat("#.##").format(x)+
                ", pos y:"+new DecimalFormat("#.##").format(y)+
                ", velocity:"+new DecimalFormat("#.##").format(velocity));

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);  //cleans the screen
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawPlot(g2d);
        textCarStates(g2d,carPosition.x, carPosition.y,velocity);

    }

}
