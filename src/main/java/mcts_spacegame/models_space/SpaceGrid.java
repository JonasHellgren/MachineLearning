package mcts_spacegame.models_space;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;
import mcts_spacegame.generic_interfaces.StateInterface;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/***
 *   The space grid below has 3 rows and 7 columns. For example is cell (1,2) filled with an obstacle.
 *    y is inverse of row, for example y=0 is row 2 and y=2 is row 0.
 *
 *
 *      -----------------------------      /|\  y
 *      |   |   |   |   |   |   | G |       |
 *      |   |   | O | O |   |   | G |       |
 *      |   | O |   |   |   |   | G |       |
 *      -----------------------------       |
 *
 *   Every cell in very most right column are goals
 *
 */


@Log
@Getter
public class SpaceGrid implements SpaceGridInterface {

    @AllArgsConstructor
    @ToString
    static
    class RowAndColumn {
        public int column;
        public int row;
    }

    private final int nofRows;
    private final int nofColumns;
    private final SpaceCell[][] grid;

    public SpaceGrid(int nofRows, int nofColumns, List<Pair<Integer, Integer>> occupiedCells) {
        this.nofRows = nofRows;
        this.nofColumns = nofColumns;
        grid = new SpaceCell[nofColumns][nofRows];
        fillGrid(occupiedCells);
    }

    public Optional<SpaceCell> getCell(StateInterface<ShipVariables> state) {
        return getCell(state.getVariables().x,state.getVariables().y);

    }
    public Optional<SpaceCell> getCell(Integer x, Integer y) {

        Optional<SpaceCell> spaceCellOpt=Optional.empty();
        RowAndColumn rc=convertPos(x,y);

        if (rc.column < 0 || rc.column >= nofColumns || rc.row < 0 || rc.row >= nofRows) {
            log.fine("Non valid grid position");
            return spaceCellOpt;
        }
        spaceCellOpt=Optional.of(grid[rc.column][rc.row]);
        return spaceCellOpt;
    }

    public void clear() {
        fillGrid(new ArrayList<>());
    }

    public void fillGrid(List<Pair<Integer, Integer>> occupiedCells) {

        for (int y = 0; y < nofRows; y++) {
            for (int x = 0; x < nofColumns; x++) {
                RowAndColumn rc=convertPos(x,y);
                int finalY = y;
                boolean isObstacle = occupiedCells.stream().
                        anyMatch(p -> p.getFirst() == rc.column && p.getSecond() == finalY);
                boolean isGoal = (rc.column== nofColumns - 1);
                boolean isOnUpperBorder = (y == nofRows - 1);
                boolean isOnLowerBorder = (y== 0);

                SpaceCell cell = new SpaceCell(
                        isObstacle,
                        isGoal,
                        isOnLowerBorder,
                        isOnUpperBorder
                );
                grid[rc.column][rc.row] = cell;

            }
        }
    }

    private RowAndColumn convertPos(int x, int y) {
        return  new RowAndColumn(x,nofRows-y-1);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("line.separator"));
        for (int y = nofRows-1; y >=0; y--) {
            sb.append("|");
            for (int x = 0; x < nofColumns; x++) {
                String cell = (getCell(x, y).orElse(SpaceCell.EMPTY()).isObstacle) ? "o" : " ";
                sb.append(cell).append("|");
            }
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }


}
