package monte_carlo_tree_search.domains.models_space;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface SpaceGridInterface {

    Optional<SpaceCell> getCell(Integer x, Integer y);

    void clear();

    void fillGrid(List<Pair<Integer, Integer>> occupiedCells,List<Triple<Integer,Integer,Double>> bonusCells);

    static SpaceGrid newWithNoObstacles(int nofRows, int nofColumns) {
        List<Pair<Integer, Integer>> occupiedCells = new ArrayList<>();
        return new SpaceGrid(nofRows, nofColumns, occupiedCells);
    }

    /***
     *      -----------------------------
     *      |   |   |   |   |   |   | G |
     *      |   |   | O | O |   |   | G |
     *      |   | O |   | O |   |   | G |
     *      -----------------------------
     */
    static SpaceGrid new3times7Grid() {
        List<Pair<Integer, Integer>> occupiedCells = Arrays.asList(
                new Pair<>(2, 1), new Pair<>(3, 1), new Pair<>(1, 0), new Pair<>(3, 0));
        return new SpaceGrid(3, 7, occupiedCells);
    }

    /***
     *      -----------------------------
     *      |   |   |   |   |   |   | G |
     *      |   |   | O | O | O | O | G |
     *      |   |   |   |   |   | O | G |
     *      -----------------------------
     */
    static SpaceGrid new3times7GridWithTrapCorridor() {
        List<Pair<Integer, Integer>> occupiedCells = Arrays.asList(
                new Pair<>(2, 1), new Pair<>(3, 1), new Pair<>(4, 1), new Pair<>(5, 1)
                , new Pair<>(5, 0));
        return new SpaceGrid(3, 7, occupiedCells);
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
    static SpaceGrid new5times15GridCorridor() {
        List<Pair<Integer, Integer>> occupiedCells = new ArrayList<>();
        List<Pair<Integer, Integer>> rowY1 = createRow(1);
        List<Pair<Integer, Integer>> rowY3 = createRow(3);
        occupiedCells.addAll(rowY1);
        occupiedCells.addAll(rowY3);
        List<Triple<Integer,Integer,Double>> bonusCells= Arrays.asList(
                Triple.of(14,2,3d), Triple.of(14,4,6d)
        );
        return new SpaceGrid(5, 15, occupiedCells,bonusCells);
    }


    /***
     *      -------------------------------------------------------------
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   |   |G+6|   (best, +2)
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   | O |   |
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   |   |G+3|   (second best. +1)
     *      |   |   |   |   |   |   |   |   |   |   |   |   |   | O |   |
     *      | S |   |   |   |   |   |   |   |   |   |   |   |x12|   | G |   (worst, 0)
     *      -------------------------------------------------------------
     */

    static SpaceGrid new5times15Grid() {
        List<Pair<Integer, Integer>> occupiedCells = Arrays.asList(
                new Pair<>(13, 1), new Pair<>(13, 3));
        List<Triple<Integer,Integer,Double>> bonusCells= Arrays.asList(
                Triple.of(14,2,3d), Triple.of(14,4,6d)
        );
        return new SpaceGrid(5, 15, occupiedCells,bonusCells);

    }

     static List<Pair<Integer, Integer>> createRow(int ri) {
        List<Pair<Integer, Integer>> row = new ArrayList<>();
        for (int ci = 4; ci < 15; ci++) {
            row.add(new Pair<>(ci, ri));
        }
        return row;
    }

    /***
     *      -------------------------------
     *      |   |   |   |   |   |   | G+5 |
     *      |   |   | O | O | O | O | G   |
     *      | S | O | O |   |   |   | G+10|
     *      |   |   |   | O | O | O | G   |
     *      |   |   |   |   | O | O | G   |
     *      ------------------------------
     */

    static SpaceGrid new5times7GridWithTrapCorridor() {
        List<Pair<Integer, Integer>> occupiedCells = Arrays.asList(
                new Pair<>(2, 3), new Pair<>(3, 3), new Pair<>(4, 3), new Pair<>(5, 3),
        new Pair<>(1, 2), new Pair<>(2, 2),
                                  new Pair<>(3, 1), new Pair<>(4, 1), new Pair<>(5, 1),
                                                    new Pair<>(4, 0), new Pair<>(5, 0));
        List<Triple<Integer,Integer,Double>> bonusCells = Arrays.asList(
                Triple.of(6,4,5d), Triple.of(6,2,10d)
        );
        return new SpaceGrid(5, 7, occupiedCells,bonusCells);
    }

}

