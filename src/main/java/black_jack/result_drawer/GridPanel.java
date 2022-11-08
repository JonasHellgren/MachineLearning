package black_jack.result_drawer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

//@Builder
public class GridPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color LINE_COLOR = Color.BLACK;
    private static final int NOF_EXTRA_COLS = 2;
    private static final int NOF_EXTRA_ROWS = 2;
    private static final Color TEXT_COLOR = Color.BLUE;
    private static final float DEFAULT_RELATIVE_FRAME_SIZE = 0.5f;

    List<Integer> xSet,ySet;
    String xLabel, yLabel;

    private final Color[][] gridColor; /* gridColor[r][c] is the color for square in row r, column c;
	                                 if it  is null, the square has the panel's background color.*/

    private final Double[][] gridNumbers;

    private final int nofRows; // Number of rows of squares.
    private final int nofColumns; // Number of columns of squares.
    double cellWidth;
    double cellHeight;

    public GridPanel(List<Integer> xSet,
                     List<Integer> ySet,
                     String xLabel,
                     String yLabel) {
        this(xSet,ySet,xLabel,yLabel,new Color[ySet.size()][xSet.size()], DEFAULT_RELATIVE_FRAME_SIZE);
    }

    public GridPanel(List<Integer> xSet,
                     List<Integer> ySet,
                     String xLabel,
                     String yLabel,
                     Color[][] gridColor,
                     float relativeFrameSize) {

        nofRows = ySet.size();
        nofColumns = xSet.size();

        this.gridColor = gridColor;
        this.xSet = xSet;
        this.ySet = ySet;
        this.xLabel=xLabel;
        this.yLabel=yLabel;

        gridNumbers=new Double[nofRows][nofColumns];
        gridNumbers[0][0]=1d;
        gridNumbers[2][2]=1d;

        defineAndSetSquareSize(nofRows, nofColumns, relativeFrameSize);
        setBackground(BACKGROUND_COLOR);
    }

    private void setCellSize() {
        cellWidth = (double) getWidth() / (nofColumns + NOF_EXTRA_COLS);
        cellHeight = (double) getHeight() /(nofRows +NOF_EXTRA_ROWS);
    }

    private void defineAndSetSquareSize(int rows, int columns, float relativeFrameSize) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float preferedFrameWidth = screenSize.width * relativeFrameSize;
        int sqSizeWidth = Math.round(preferedFrameWidth / (float) (nofColumns + NOF_EXTRA_COLS));
        float preferedFrameHeight = screenSize.height * relativeFrameSize;
        int sqSizeHeight = Math.round(preferedFrameHeight / (float) (nofColumns + NOF_EXTRA_ROWS));
        int preferredSquareSize = Math.min(sqSizeWidth, sqSizeHeight);
        setPreferredSize(new Dimension(preferredSquareSize * columns,
                preferredSquareSize * rows));
    }

    public void setColorAtCell(int row, int col, Color color) {
        int colIdx = getColIdx(col);
        int rowIdx = getRowIdx(row);
        this.gridColor[nofRows - rowIdx - 1][colIdx] = color;
    }

    public void setNumbersAtCell(int row, int col, Double num) {
        int colIdx = getColIdx(col);
        int rowIdx = getRowIdx(row);
        this.gridNumbers[nofRows - rowIdx - 1][colIdx] = num;
    }

    private int getRowIdx(int row) {
        int rowIdx= ySet.indexOf(row);
        if (rowIdx ==-1) {
            throw new IllegalArgumentException("Non defined y value");
        }
        return rowIdx;
    }

    private int getColIdx(int col) {
        int colIdx= xSet.indexOf(col);
        if (colIdx==-1) {
            throw new IllegalArgumentException("Non defined x value");
        }
        return colIdx;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        setCellSize();
        fillSquaresWithColor(g);
        fillSquaresWithText(g);
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
            g.drawLine(x, 0, x, (int) (nofRows *cellHeight));
        }
    }

    private void drawHorizontalLines(Graphics g) {
        g.setColor(LINE_COLOR);
        for (int row = 1; row < nofRows +1; row++) {
            int y = getYPos(row);
            int xOffset=(int) (NOF_EXTRA_COLS*cellWidth);
            g.drawLine(xOffset, y, xOffset+(int) (nofColumns *cellHeight), y);
        }
    }


    private void fillSquaresWithColor(Graphics g) {
        g.setColor(TEXT_COLOR);
        for (int row = 0; row < nofRows; row++) {
            for (int col = 0; col < nofColumns; col++) {
                fillSquareWithColor(g, row, col);
            }
        }
    }

    private void fillSquareWithColor(Graphics g, int row, int col) {
        if (gridColor[row][col] != null) {
            int x1 = getXpos(col);
            int y1 = getYPos(row);
            int x2 = getXpos(col +1);
            int y2 = getYPos(row +1);
            g.setColor(gridColor[row][col]);
            g.fillRect(x1, y1, (x2 - x1), (y2 - y1));
        }
    }

    private void fillSquaresWithText(Graphics g) {
        g.setColor(TEXT_COLOR);
        for (int row = 0; row < nofRows; row++) {
            for (int col = 0; col < nofColumns; col++) {
                if (gridNumbers[row][col] != null) {
                    int x = getXpos(col);
                    int y = getYPos(row+1);  //lower left corner of square
                    g.drawString(gridNumbers[row][col].toString(), x, y);
                }
            }
        }
    }


    private int getXpos(int col) {
        return (int) ((NOF_EXTRA_COLS +col) * cellWidth);
    }

    private int getYPos(int row) {
        return (int) (row * cellHeight);
    }

    private void textXticks(Graphics g) {
        for (int col = 0; col < nofColumns; col++) {
            int x = getXpos(col);
            g.setColor(TEXT_COLOR);
            g.drawString(xSet.get(col).toString(), x,(int) ((nofRows +1)*cellHeight));
        }
    }

    private void textYticks(Graphics g) {
        for (int row = 0; row < nofRows; row++) {
            int y = (int) ((nofRows -row) * cellHeight);
            g.setColor(TEXT_COLOR);
            g.drawString(ySet.get(row).toString(), (int) ((1)*cellWidth),y);
        }
    }

    private void textXlabel(Graphics g) {
        g.setColor(TEXT_COLOR);
        int y = getYPos(nofRows +NOF_EXTRA_ROWS);
        g.drawString(xLabel, (int) ((NOF_EXTRA_COLS)*cellWidth),y);
    }

    //https://kodejava.org/how-do-i-draw-a-vertical-text-in-java-2d/
    private void textYlabel(Graphics g) {
        g.setColor(TEXT_COLOR);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform at = new AffineTransform();
        for (int charIdx = 0; charIdx < yLabel.length(); charIdx++) {
            int y = (int) ((nofRows) * cellHeight/2+(nofRows -charIdx) * cellHeight/2);  //charIdx=0 => y = nofRaws*cellHeight
            at.setToRotation(Math.toRadians(-90), (int) ((1)*cellWidth),y);
            g2.setTransform(at);
            g2.drawString(yLabel.substring(charIdx,charIdx+1), (int) ((1)*cellWidth),y);
        }
    }

}
