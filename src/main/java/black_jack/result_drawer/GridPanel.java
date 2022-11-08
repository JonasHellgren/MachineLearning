package black_jack.result_drawer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GridPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color LINE_COLOR = Color.BLACK;
    private static final int NOF_EXTRA_COLS = 2;
    private static final int NOF_EXTRA_ROWS = 2;
    private static final Color TEXT_COLOR = Color.BLUE;
    private final int nofRaws; // Number of rows of squares.
    private final int nofColumns; // Number of columns of squares.
    List<Integer> xSet;
    List<Integer> ySet;
    private final Color[][] gridColor; /* gridColor[r][c] is the color for square in row r, column c;
	                                 if it  is null, the square has the panel's background color.*/
    double cellWidth;
    double cellHeight;

    public GridPanel(List<Integer> xSet, List<Integer> ySet,float relativeFrameSize) {

        /*
        if (xTicks.size()!=columns) {
            throw new IllegalArgumentException("Bad length of xTicks");
        }  */

        nofRaws = ySet.size();
        nofColumns = xSet.size();

        this.gridColor = new Color[nofRaws][nofColumns]; // Create the array that stores square colors.
       // gridRows = rows;
       // gridCols = columns;
        this.xSet = xSet;
        this.ySet = ySet;

        defineAndSetSquareSize(nofRaws, nofColumns, relativeFrameSize);
        setBackground(BACKGROUND_COLOR); // Set the background color for this panel.
    }

    private void setCellSize() {
        cellWidth = (double) getWidth() / (nofColumns + NOF_EXTRA_COLS);
        cellHeight = (double) getHeight() /(nofRaws +NOF_EXTRA_ROWS);
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

        int colIdx= xSet.indexOf(col);
        int rawIdx= ySet.indexOf(row);
        if (rawIdx==-1 || colIdx==-1) {
            System.out.println("row = " + row);
            System.out.println("gridRows = " + nofRaws);
            System.out.println("col = " + col);
            System.out.println("gridCols = " + nofColumns);
            throw new IllegalArgumentException("Wrong raw/column number");
        }
        this.gridColor[nofRaws - rawIdx - 1][colIdx] = color;
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
        textYticks(g);
    }

    private void drawVerticalLines(Graphics g) {
        for (int col = 0; col < nofColumns; col++) {
            int x = (int) ((NOF_EXTRA_COLS +col) * cellWidth);
            g.drawLine(x, 0, x, (int) (nofRaws *cellHeight));
        }
    }

    private void drawHorisontalLines(Graphics g) {
        g.setColor(LINE_COLOR);
        for (int row = 1; row < nofRaws +1; row++) {
            int y = (int) (row * cellHeight);
            int xOffset=(int) (NOF_EXTRA_COLS*cellWidth);
            g.drawLine(xOffset, y, xOffset+(int) (nofColumns *cellHeight), y);
        }
    }

    private void fillSquaresWithColor(Graphics g) {
        double cellWidth = (double) getWidth() / (nofColumns + NOF_EXTRA_COLS);
        double cellHeight = (double) getHeight() /(nofRaws +NOF_EXTRA_ROWS);
        for (int row = 0; row < nofRaws; row++) {
            for (int col = 0; col < nofColumns; col++) {
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
        for (int col = 0; col < nofColumns; col++) {
            int x = (int) ((NOF_EXTRA_COLS +col) * cellWidth);
            g.setColor(TEXT_COLOR);
            g.drawString(xSet.get(col).toString(), x,(int) ((nofRaws +1)*cellHeight));
        }
    }

    private void textYticks(Graphics g) {
        for (int row = 0; row < nofRaws; row++) {
            int y = (int) ((nofRaws-row) * cellHeight);
            g.setColor(TEXT_COLOR);
            g.drawString(ySet.get(row).toString(), (int) ((1)*cellWidth),y);
        }
    }

}
