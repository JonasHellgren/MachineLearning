package black_jack.result_drawer;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Setter
public class GridPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color LINE_COLOR = Color.BLACK;
    private static final int NOF_EXTRA_COLS = 2;
    private static final int NOF_EXTRA_ROWS = 2;
    private static final Color TEXT_COLOR = Color.BLUE;
    private static final int RBG_MAX = 255;
    private static final double RELATIVE_FONT_SIZE_TEXT = 0.6;
    private static final double RELATIVE_FONT_SIZE_NUMBERS = 0.4;

    private static final float DEFAULT_RELATIVE_FRAME_SIZE = 0.5f;
    private static final int DEFAULT_NOF_DECIMALS = 1;
    private static final boolean DEFAULT_SHOW_TEXT_FLAG = true;
    private static final double TEXT_WH_RATIO = 0.6;


    List<Integer> xSet, ySet;
    String xLabel, yLabel;

    private final Color[][] gridColor; /* gridColor[r][c] is the color for cell in row r, column c;
	                                   if it  is null, the Cell has the panel's background color.*/

    private final Double[][] gridNumbers;
    private final int nofDecimals;
    private boolean textCellValues;

    private final int nofRows; // Number of rows of cells.
    private final int nofColumns; // Number of columns of cells.
    int cellSize;

    public GridPanel(List<Integer> xSet,
                     List<Integer> ySet,
                     String xLabel,
                     String yLabel) {
        this(xSet,
                ySet,
                xLabel,
                yLabel,
                new Double[ySet.size()][xSet.size()],
                new Color[ySet.size()][xSet.size()],
                DEFAULT_RELATIVE_FRAME_SIZE,
                DEFAULT_NOF_DECIMALS,
                DEFAULT_SHOW_TEXT_FLAG);
    }

    public GridPanel(List<Integer> xSet,
                     List<Integer> ySet,
                     String xLabel,
                     String yLabel,
                     Double[][] gridNumbers,
                     Color[][] gridColor,
                     float relativeFrameSize,
                     int nofDecimals,
                     boolean textCellValues) {

        this.xSet = xSet;
        this.ySet = ySet;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.gridNumbers = gridNumbers;
        this.gridColor = gridColor;
        this.nofDecimals = nofDecimals;
        this.textCellValues = textCellValues;



        this.nofRows = ySet.size();
        this.nofColumns = xSet.size();
        this.cellSize = defineAndSetCellSize(relativeFrameSize);
        setPreferredSize(new Dimension(cellSize * (nofColumns + NOF_EXTRA_COLS),
                cellSize * (nofRows + NOF_EXTRA_ROWS)));
        setBackground(BACKGROUND_COLOR);
    }


    public void setColorsAtCells() {
        double MIN_VALUE_BACKUP = -1d;
        double MAX_VALUE_BACKUP = 1d;
        List<Double> doubleList = Arrays.stream(gridNumbers).flatMap(Arrays::stream).collect(Collectors.toList());
        double minValue = doubleList.stream().mapToDouble(Double::doubleValue).min().orElse(MIN_VALUE_BACKUP);
        double maxValue = doubleList.stream().mapToDouble(Double::doubleValue).max().orElse(MAX_VALUE_BACKUP);

        for (int y : ySet) {
            for (int x : xSet) {
                double value = gridNumbers[getRowIdx(y)][getColIdx(x)];
                double strength = (value - minValue) / (maxValue - minValue); //normalization
                int rgb = Math.min((int) (strength * RBG_MAX), RBG_MAX);
                setColorAtCell(x, y, new Color(rgb, rgb, rgb));
            }
        }
    }

    public void setColorAtCell(int x, int y, Color color) {
        int colIdx = getColIdx(x);
        int rowIdx = getRowIdx(y);
        this.gridColor[rowIdx][colIdx] = color;
    }

    public void setNumbersAtCell(int x, int y, Double num) {
        int colIdx = getColIdx(x);
        int rowIdx = getRowIdx(y);
        this.gridNumbers[nofRows - rowIdx - 1][colIdx] = num;
    }

    private int defineAndSetCellSize(float relativeFrameSize) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float preferedFrameWidth = screenSize.width * relativeFrameSize;
        float preferedFrameHeight = screenSize.height * relativeFrameSize;
        int sqSizeWidth = Math.round(preferedFrameWidth / (float) (nofColumns + NOF_EXTRA_COLS));
        int sqSizeHeight = Math.round(preferedFrameHeight / (float) (nofRows + NOF_EXTRA_ROWS));
        return Math.min(sqSizeWidth, sqSizeHeight);
    }

    private int getRowIdx(int y) {
        int rowIdx = ySet.indexOf(y);
        if (rowIdx == -1) {
            throw new IllegalArgumentException("Non defined y value, y =" + y);
        }
        return rowIdx;
    }

    private int getColIdx(int x) {
        int colIdx = xSet.indexOf(x);
        if (colIdx == -1) {
            throw new IllegalArgumentException("Non defined x value, x = " + x);
        }
        return colIdx;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        fillCellsWithColor(g);

        if (textCellValues) {
            fillCellsWithText(g);
        }
        drawHorizontalLines(g);
        drawVerticalLines(g);
        textXticks(g);
        textYticks(g);
        textXlabel(g);
        textYlabel(g);
    }

    private void drawVerticalLines(Graphics g) {
        for (int col = 0; col < nofColumns; col++) {
            int x = getXpos(col);
            g.drawLine(x, 0, x, (nofRows * cellSize));
        }
    }

    private void drawHorizontalLines(Graphics g) {
        g.setColor(LINE_COLOR);
        for (int row = 1; row < nofRows + 1; row++) {
            int y = getYPos(row);
            int xOffset =  NOF_EXTRA_COLS * cellSize;
            g.drawLine(xOffset, y, xOffset + (nofColumns * cellSize), y);
        }
    }


    private void fillCellsWithColor(Graphics g) {
        g.setColor(TEXT_COLOR);
        for (int row = 0; row < nofRows; row++) {
            for (int col = 0; col < nofColumns; col++) {
                fillCellWithColor(g, row, col);
            }
        }
    }

    private void fillCellWithColor(Graphics g, int row, int col) {
        if (gridColor[row][col] != null) {
            int x1 = getXpos(col);
            int y1 = getYPos(row);
            int x2 = getXpos(col + 1);
            int y2 = getYPos(row + 1);
            g.setColor(gridColor[row][col]);
            g.fillRect(x1, y1, (x2 - x1), (y2 - y1));
        }
    }

    private void fillCellsWithText(Graphics g) {
        setTextPropertiesNumbers(g);
        for (int row = 0; row < nofRows; row++) {
            for (int col = 0; col < nofColumns; col++) {
                Double value = gridNumbers[row][col];
                if (value != null) {
                    int x = getXpos(col);
                    int y = getYPos(row + 1);  //lower left corner of Cell
                    BigDecimal bd = BigDecimal.valueOf(value).setScale(nofDecimals, RoundingMode.HALF_DOWN);
                    g.drawString(bd.toString(), x, y);
                }
            }
        }
    }


    private int getXpos(int col) {
        return (NOF_EXTRA_COLS + col) * cellSize;
    }

    private int getYPos(int row) {
        return (row * cellSize);
    }

    private void textXticks(Graphics g) {
        setTextPropertiesNumbers(g);
        for (int col = 0; col < nofColumns; col++) {
            int x = getXpos(col);
            g.drawString(xSet.get(col).toString(), x,  (nofRows + 1) * cellSize);
        }
    }

    private void textYticks(Graphics g) {
        setTextPropertiesNumbers(g);
        for (int row = 0; row < nofRows; row++) {
            int y =  (nofRows - row) * cellSize;
            g.drawString(ySet.get(row).toString(),  cellSize, y);
        }
    }

    private void setTextPropertiesNumbers(Graphics g) {
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("TimesRoman", Font.PLAIN, (int) (cellSize * RELATIVE_FONT_SIZE_NUMBERS)));
    }


    private void setTextPropertiesText(Graphics g) {
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("TimesRoman", Font.BOLD, (int) (cellSize * RELATIVE_FONT_SIZE_TEXT)));
    }

    private void textXlabel(Graphics g) {
        setTextPropertiesText(g);
        int y = getYPos(nofRows + NOF_EXTRA_ROWS);
        g.drawString(xLabel, (NOF_EXTRA_COLS) * cellSize, y);
    }

    //https://kodejava.org/how-do-i-draw-a-vertical-text-in-java-2d/
    private void textYlabel(Graphics g) {
        setTextPropertiesText(g);

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform at = new AffineTransform();
        for (int charIdx = 0; charIdx < yLabel.length(); charIdx++) {
            int y = (int) ((nofRows) * cellSize -
                    charIdx * cellSize * RELATIVE_FONT_SIZE_TEXT* TEXT_WH_RATIO);  //charIdx=0 => y = nofRaws*cellHeight
            at.setToRotation(Math.toRadians(-90), (cellSize), y);
            g2.setTransform(at);
            g2.drawString(yLabel.substring(charIdx, charIdx + 1), (cellSize), y);
        }
    }

}
