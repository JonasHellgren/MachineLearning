package black_jack.result_drawer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class GridPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color LINE_COLOR = Color.BLACK;
    private static final int NOF_EXTRA_COLS = 2;
    private static final int NOF_EXTRA_ROWS = 2;
    private static final Color TEXT_COLOR = Color.BLUE;
    private final int gridRows; // Number of rows of squares.
    private final int gridCols; // Number of columns of squares.
    List<Integer> xTicks;
    List<Integer> yTicks;
    private final Color[][] gridColor; /* gridColor[r][c] is the color for square in row r, column c;
	                                 if it  is null, the square has the panel's background color.*/
    double cellWidth;
    double cellHeight;

    public GridPanel(int rows, int columns, float relativeFrameSize,List<Integer> xTicks,List<Integer> yTicks) {

        if (xTicks.size()!=columns) {
            throw new IllegalArgumentException("Bad length of xTicks");
        }

        this.gridColor = new Color[rows][columns]; // Create the array that stores square colors.
        gridRows = rows;
        gridCols = columns;
        this.xTicks=xTicks;
        this.yTicks=yTicks;

        defineAndSetAquareSize(rows, columns, relativeFrameSize);
        setBackground(BACKGROUND_COLOR); // Set the background color for this panel.
    }

    private void setCellSize() {
        cellWidth = (double) getWidth() / (gridCols+ NOF_EXTRA_COLS);
        cellHeight = (double) getHeight() /(gridRows+NOF_EXTRA_ROWS);
    }

    private void defineAndSetAquareSize(int rows, int columns, float relativeFrameSize) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float preferedFrameWidth = screenSize.width * relativeFrameSize;
        int sqSizeWidth = Math.round(preferedFrameWidth / (float) (gridCols+ NOF_EXTRA_COLS));
        float preferedFrameHeight = screenSize.height * relativeFrameSize;
        int sqSizeHeight = Math.round(preferedFrameHeight / (float) (gridCols+ NOF_EXTRA_ROWS));
        int preferredSquareSize = Math.min(sqSizeWidth, sqSizeHeight);
        setPreferredSize(new Dimension(preferredSquareSize * columns,
                preferredSquareSize * rows));
    }

    public void setColorAtCell(int row, int col, Color color) {

        int colIdx= xTicks.indexOf(col);
        int rawIdx= yTicks.indexOf(row);
        if (rawIdx==-1 || colIdx==-1) {
            System.out.println("row = " + row);
            System.out.println("gridRows = " + gridRows);
            System.out.println("col = " + col);
            System.out.println("gridCols = " + gridCols);
            throw new IllegalArgumentException("Wrong raw/column number");
        }
        this.gridColor[gridRows - row - 1][colIdx] = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        setCellSize();
        fillSquaresWithColor(g);
        drawHorisontalLines(g);
        drawVerticalLines(g);
        textXticks(g);
    }

    private void drawVerticalLines(Graphics g) {
        for (int col = 0; col < gridCols; col++) {
            int x = (int) ((NOF_EXTRA_COLS +col) * cellWidth);
            g.drawLine(x, 0, x, (int) (gridRows*cellHeight));
        }
    }

    private void drawHorisontalLines(Graphics g) {
        g.setColor(LINE_COLOR);
        for (int row = 1; row < gridRows+1; row++) {
            int y = (int) (row * cellHeight);
            int xOffset=(int) (NOF_EXTRA_COLS*cellWidth);
            g.drawLine(xOffset, y, xOffset+(int) (gridCols*cellHeight), y);
        }
    }

    private void fillSquaresWithColor(Graphics g) {
        double cellWidth = (double) getWidth() / (gridCols+ NOF_EXTRA_COLS);
        double cellHeight = (double) getHeight() /(gridRows+NOF_EXTRA_ROWS);
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                if (gridColor[row][col] != null) {
                    int x1 = (int) ((NOF_EXTRA_COLS +col) * cellWidth);
                    int y1 = (int) ((row) * cellHeight);
                    int x2 = (int) ((NOF_EXTRA_COLS +col + 1) * cellWidth);
                    int y2 = (int) ((row + 1) * cellHeight);
                    g.setColor(gridColor[row][col]);
                    g.fillRect(x1, y1, (x2 - x1), (y2 - y1));
                }
            }
        }
    }

    private void textXticks(Graphics g) {
        for (int col = 0; col < gridCols; col++) {
            int x = (int) ((NOF_EXTRA_COLS +col) * cellWidth);
            g.setColor(TEXT_COLOR);

            //System.out.println("xTicks.get(col).toString() = " + xTicks.get(col).toString());
            g.drawString(xTicks.get(col).toString(), x,(int) ((gridRows+1)*cellHeight));
        }
    }

}
