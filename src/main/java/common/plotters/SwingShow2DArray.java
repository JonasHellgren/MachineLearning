package common.plotters;

import common.list_arrays.ArrayUtil;
import common.math.MathUtils;
import lombok.Builder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.IntStream;

@Builder
public class SwingShow2DArray {

    public static final int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;
    public static final String FORMAT = "%.2f";
    public static final String FONT_NAME = "Serif";

    @Builder
    record WidthAndHeight(int width, int height, int margin, int marginTitle, int nCols, int nRows) {
        public static final double RATIO_FONT_SIZE_HEIGHT = 0.5;
        int usableWidth() {return  width - margin;}
        int usableHeight() {return height - margin;}
        int cellWidth() {return usableWidth() / nCols;}
        int cellHeight() {return usableHeight() / nRows;}
        int fontSize() { return (int) (cellHeight()*RATIO_FONT_SIZE_HEIGHT);}
    }

    @Builder
    record TextXY(int col, int row, Graphics2D g2d, String text, WidthAndHeight wh) {
        FontMetrics metrics() {return g2d.getFontMetrics();}
        int textX() {
            int stringWidth = Objects.isNull(text)?0:metrics().stringWidth(text);
            return  getX() + (wh.cellWidth() - stringWidth) / 2;}

        private int getX() {
            return col * wh.cellWidth() + wh.margin();
        }

        int textY() {
            return  getY() + ((wh.cellHeight() - metrics().getHeight()) / 2) + metrics().getAscent();}

        private int getY() {
            return wh.marginTitle+(wh.nRows - 1 - row) * wh.cellHeight();
        }
    }

    record ImageCreator(int width, int height, int imageType) {
        BufferedImage image() {return new BufferedImage(width, height, IMAGE_TYPE); }
        Graphics2D graphics2D(BufferedImage im) {return im.createGraphics();}
    }

    public static final int MIN_DARKNESS = 100;
    @Builder.Default
    int width=500;
    @Builder.Default
    int height=200;
    @Builder.Default
    boolean isLabels=true;
    String[] xLabels;
    String[] yLabels;
    @Builder.Default
    int margin = 50; // Margin for labels
    @Builder.Default
    int marginTitle = 50; // Margin for title


    public void showMap(Double[][] data,String title) {
        var image=createHeatMapImage(data,title);
        displayImage(image);
    }

    public void showMap(String[][] data,String title) {
        var image=createHeatMapImage(data,title);
        displayImage(image);
    }

    private   BufferedImage createHeatMapImage(Double[][] data,String title) {
        var ic=new ImageCreator(width,height,IMAGE_TYPE);
        var image=ic.image();
        var g2d=ic.graphics2D(image);
        var wh= getWidthAndHeight(data[0].length, data.length);
        drawMap(data, g2d,wh);
        drawLabels(g2d, wh);
        drawTitle(title, g2d,wh);
        return image;
    }

    private   BufferedImage createHeatMapImage(String[][] data,String title) {
        var ic=new ImageCreator(width,height,IMAGE_TYPE);
        var image=ic.image();
        var g2d=ic.graphics2D(image);
        var wh= getWidthAndHeight(data[0].length, data.length);
        drawMap(data, g2d, wh);
        drawLabels(g2d, wh);
        drawTitle(title, g2d,wh);
        return image;
    }

    WidthAndHeight getWidthAndHeight(int nCols, int nRows) {
        return WidthAndHeight.builder()
                .width(width).height(height).margin(margin).marginTitle(marginTitle)
                .nCols(nCols).nRows(nRows)
                .build();
    }

    private void drawMap(String[][] data,Graphics2D g2d,  WidthAndHeight wh) {
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                String text = data[row][col];
                if (!Objects.isNull(text)) {
                    printText(g2d, wh, row, col, text);
                }
            }
        }
    }

    private void drawMap(Double[][] data, Graphics2D g2d,  WidthAndHeight wh) {
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                Double value = data[row][col];
                if (!Objects.isNull(value)) {
                    String text = String.format(FORMAT, value);
                    printText(g2d, wh, row, col, text);
                }
            }
        }
    }

    private static void printText(Graphics2D g2d, WidthAndHeight wh, int row, int col, String text) {
        TextXY xy = TextXY.builder()
                .col(col).row(row).g2d(g2d).text(text).wh(wh).build();
        setG2d(g2d, wh);
        g2d.drawString(text, xy.textX(), xy.textY());
    }

    private static void setG2d(Graphics2D g2d, WidthAndHeight wh) {
        g2d.setColor(Color.BLACK);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, wh.fontSize()));
    }
    private void drawTitle(String title, Graphics2D g2d, WidthAndHeight wh) {
        g2d.setFont(new Font(FONT_NAME, Font.BOLD, wh.fontSize()));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int titleX = (width - titleMetrics.stringWidth(title)) / 2;
        int titleY = titleMetrics.getAscent();
        g2d.drawString(title, titleX, titleY);
    }

    private void drawLabels(Graphics2D g2d, WidthAndHeight wh) {
        if (!isLabels) {
            return;
        }
        drawXlabels(g2d, wh);
        drawYlabels(g2d, wh);
    }

    private void drawYlabels(Graphics2D g2d, WidthAndHeight wh) {
        if (yLabels==null) {
            yLabels= IntStream.range(0, wh.nRows).mapToObj(Integer::toString).toArray(String[]::new);
        }

        for (int i = 0; i < yLabels.length; i++) {
            String label = yLabels[i];
            // Adjust for y-axis inversion
            g2d.drawString(label, 5, (yLabels.length - 1 - i) * wh.cellHeight() + wh.cellHeight() / 2 + g2d.getFontMetrics().getAscent() / 2);
        }
    }

    private void drawXlabels(Graphics2D g2d, WidthAndHeight wh) {
        if (xLabels==null) {
            xLabels=  IntStream.range(0, wh.nCols).mapToObj(Integer::toString).toArray(String[]::new);
        }
        for (int i = 0; i < xLabels.length; i++) {
            String label = xLabels[i];
            g2d.drawString(label, i * wh.cellWidth() + wh.cellWidth() / 2 + margin - g2d.getFontMetrics().stringWidth(label) / 2, wh.usableHeight() + 20);
        }
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
