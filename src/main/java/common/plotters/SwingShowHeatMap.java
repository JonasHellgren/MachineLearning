package common.plotters;

import common.MathUtils;
import lombok.Builder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.IntStream;

@Builder
public class SwingShowHeatMap {

    public static final int MIN_DARKNESS = 100;
    @Builder.Default
    int width=300;
    @Builder.Default
    int height=200;
    @Builder.Default
    boolean isLabels=true;
    String[] xLabels;
    String[] yLabels;
    @Builder.Default
    int margin = 50; // Margin for labels


    public void showMap(double[][] data,String title) {
        var image=createHeatMapImage(data,title);
        displayImage(image);
    }

    private   BufferedImage createHeatMapImage(double[][] data,String title) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        int usableWidth = width - margin;
        int usableHeight = height - margin;
        int cellWidth = usableWidth / data[0].length;
        int cellHeight = usableHeight / data.length;

        drawMap(data, g2d, cellWidth, cellHeight);
        drawLabels(data,g2d, usableHeight, cellWidth, cellHeight);
        drawTitle(title, g2d);

        g2d.dispose();
        return image;
    }

    private void drawMap(double[][] data, Graphics2D g2d, int cellWidth, int cellHeight) {
        // Calculate min and max values for color scaling
        double min = Arrays.stream(data).flatMapToDouble(Arrays::stream).min().orElse(0);
        double max = Arrays.stream(data).flatMapToDouble(Arrays::stream).max().orElse(1);

        // Draw heatmap cells with y-axis inverted
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                double value = data[row][col];
                Color color = calculateColor(value, min, max);
                g2d.setColor(color);
                // Invert y-axis for cell drawing
                int x = col * cellWidth + margin;
                int y = (data.length - 1 - row) * cellHeight;
                g2d.fillRect(x, y, cellWidth, cellHeight);

                // Draw value in cell
                g2d.setColor(Color.BLACK);
                String text = String.format("%.1f", value);
                FontMetrics metrics = g2d.getFontMetrics();
                int textX = x + (cellWidth - metrics.stringWidth(text)) / 2;
                int textY = y + ((cellHeight - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(text, textX, textY);
            }
        }
    }

    private void drawTitle(String title, Graphics2D g2d) {
        g2d.setFont(new Font("Serif", Font.BOLD, 20));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int titleX = (width - titleMetrics.stringWidth(title)) / 2;
        int titleY = titleMetrics.getAscent();
        g2d.drawString(title, titleX, titleY);
    }

    private void drawLabels(double[][] data, Graphics2D g2d, int usableHeight, int cellWidth, int cellHeight) {
        // Draw x-labels

        if (!isLabels) {
            return;
        }

        if (xLabels==null) {
            xLabels=  IntStream.range(0,data[0].length).mapToObj(Integer::toString).toArray(String[]::new);
        }
        for (int i = 0; i < xLabels.length; i++) {
            String label = xLabels[i];
            g2d.drawString(label, i * cellWidth + cellWidth / 2 + margin - g2d.getFontMetrics().stringWidth(label) / 2, usableHeight + 20);
        }

        // Draw y-labels with inversion in mind
        if (yLabels==null) {
            yLabels= IntStream.range(0,data.length).mapToObj(Integer::toString).toArray(String[]::new);
        }

        for (int i = 0; i < yLabels.length; i++) {
            String label = yLabels[i];
            // Adjust for y-axis inversion
            g2d.drawString(label, 5, (yLabels.length - 1 - i) * cellHeight + cellHeight / 2 + g2d.getFontMetrics().getAscent() / 2);
        }
    }

    private  Color calculateColor(double value, double min, double max) {
        double ratio = (value - min) / (max - min);
        int r= MathUtils.clip((int) (ratio*255)+ MIN_DARKNESS,0,255);
        return new Color(r,r,r);
    }

    private void displayImage(BufferedImage image) {
        SwingUtilities.invokeLater(() -> {
            ImageIcon icon = new ImageIcon(image);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new JLabel(icon), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}
