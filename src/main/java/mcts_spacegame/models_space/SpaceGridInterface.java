package mcts_spacegame.models_space;

import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface SpaceGridInterface {

    Optional<SpaceCell> getCell(Integer x, Integer y);
    void clear();
    void fillGrid(List<Pair<Integer, Integer>> occupiedCells);

    static SpaceGrid newWithNoObstacles(int nofRows,int nofColumns) {
        List<Pair<Integer, Integer>> occupiedCells= new ArrayList<>();
        return new SpaceGrid(nofRows,nofColumns,occupiedCells);
    }

    /***
     *      -----------------------------
     *      |   |   |   |   |   |   | G |
     *      |   |   | O | O |   |   | G |
     *      |   | O |   | O |   |   | G |
     *      -----------------------------
     */
    static SpaceGrid new3times7Grid() {
        List<Pair<Integer, Integer>> occupiedCells= Arrays.asList(
                new Pair<>(2,1), new Pair<>(3,1), new Pair<>(1,0), new Pair<>(3,0));
        return new SpaceGrid(3,7,occupiedCells);
    }

    /***
     *      -------------------------------------------------------------
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   |   |G+6|   (best, +2)
     *      |   |   |   |   | O | O | O | O | O | O | O | O | O | O | G |
     *      | S |   |   |   |   |   |   |   |   |   |   |   |   |   |G+3|   (second best. +1)
     *      |   |   |   |   | O | O | O | O | O | O | O |O  |O  |O  | G |
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   |   | G |   (worst, 0)
     *      -------------------------------------------------------------
     */

    //new5times15Grid

    /***
     *      -------------------------------------------------------------
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   |   |G+6|   (best, +2)
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   | O |   |
     *      | S |   |   |   |   |   |   |   |   |   |   |   |   |   |G+3|   (second best. +1)
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   | O |   |
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   |   | G |   (worst, 0)
     *      -------------------------------------------------------------
     */


}
