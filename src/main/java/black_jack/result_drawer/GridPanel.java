package black_jack.result_drawer;

import javax.swing.*;
import java.awt.*;

public class GridPanel  extends JPanel  {

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color LINE_COLOR = Color.BLACK;
    private final int gridRows; // Number of rows of squares.
    private final int gridCols; // Number of columns of squares.
    private final Color[][] gridColor; /* gridColor[r][c] is the color for square in row r, column c;
	                                 if it  is null, the square has the panel's background color.*/
    private final Color lineColor; // Color for lines drawn between squares; if null, no lines are drawn.

    public GridPanel(int rows, int columns, float relativeFrameSize) {
        this.gridColor = new Color[rows][columns]; // Create the array that stores square colors.
        gridRows = rows;
        gridCols = columns;

        this.gridColor[5][5] = new Color( (int)(225*Math.random()),
                (int)(225*Math.random()),(int)(225*Math.random()) );

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float preferedFrameWidth=screenSize.width*relativeFrameSize;
        int sqSizeWidth=Math.round(preferedFrameWidth/ (float) gridCols);
        float preferedFrameHeight=screenSize.height*relativeFrameSize;
        int sqSizeHeight=Math.round(preferedFrameHeight/ (float) gridCols);

        int preferredSquareSize=Math.min(sqSizeWidth,sqSizeHeight);

        lineColor = LINE_COLOR;
        setPreferredSize( new Dimension(preferredSquareSize*columns,
                preferredSquareSize*rows) );
        setBackground(BACKGROUND_COLOR); // Set the background color for this panel.
    }

    public void setColorAtCell(int row, int col, Color color) {
        if (row<0 || row>=gridRows || col<0 ||col>=gridCols) {
            throw new IllegalArgumentException("Wrong raw/column number");
        }
        this.gridColor[gridRows-row-1][col]=color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());
        int row, col;
        double cellWidth = (double)getWidth() / gridCols;
        double cellHeight = (double)getHeight() / gridRows;
        for (row = 0; row < gridRows; row++) {
            for (col = 0; col < gridCols; col++) {
                if (gridColor[row][col] != null) {
                    int x1 = (int)(col*cellWidth);
                    int y1 = (int)(row*cellHeight);
                    int x2 = (int)((col+1)*cellWidth);
                    int y2 = (int)((row+1)*cellHeight);
                    g.setColor(gridColor[row][col]);
                    g.fillRect( x1, y1, (x2-x1), (y2-y1) );
                }
            }
        }
        if (lineColor != null) {
            g.setColor(lineColor);
            for (row = 1; row < gridRows; row++) {
                int y = (int)(row*cellHeight);
                g.drawLine(0,y,getWidth(),y);
            }
            for (col = 1; col < gridRows; col++) {
                int x = (int)(col*cellWidth);
                g.drawLine(x,0,x,getHeight());
            }
        }
    }

}
