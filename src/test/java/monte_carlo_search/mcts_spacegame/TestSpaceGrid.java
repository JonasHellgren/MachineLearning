package monte_carlo_search.mcts_spacegame;

import monte_carlo_tree_search.domains.models_space.SpaceCell;
import monte_carlo_tree_search.domains.models_space.SpaceGrid;
import monte_carlo_tree_search.domains.models_space.SpaceGridInterface;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

public class TestSpaceGrid {

    private static final double DELTA = 0.1;
    private static final int DEFAULT_BONUS = 0;
    SpaceGrid spaceGrid;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.newWithNoObstacles(3,3);
    }

    @Test
    public void givenGridNoObstacles_thenCorrectCells() {
        spaceGrid= SpaceGridInterface.newWithNoObstacles(3,3);

        System.out.println("spaceGrid = " + spaceGrid);

        Assert.assertEquals(3,spaceGrid.getNofRows());
        Assert.assertEquals(3,spaceGrid.getNofColumns());

        Assert.assertTrue(spaceGrid.getCell(0,0).orElse(SpaceCell.EMPTY()).isOnLowerBorder);
        Assert.assertFalse(spaceGrid.getCell(0,1).orElse(SpaceCell.EMPTY()).isOnUpperBorder);
        Assert.assertTrue(spaceGrid.getCell(0,2).orElse(SpaceCell.EMPTY()).isOnUpperBorder);
        Assert.assertFalse(spaceGrid.getCell(0,0).orElse(SpaceCell.EMPTY()).isObstacle);
        Assert.assertTrue(spaceGrid.getCell(2,0).orElse(SpaceCell.EMPTY()).isGoal);

    }

    @Test
    public void whenThreeTimes7Grid_thenCorrectCells() {
        spaceGrid= SpaceGridInterface.new3times7Grid();

        System.out.println("spaceGrid = " + spaceGrid);

        Assert.assertEquals(3,spaceGrid.getNofRows());
        Assert.assertEquals(7,spaceGrid.getNofColumns());

        Assert.assertFalse(spaceGrid.getCell(0,0).orElse(SpaceCell.EMPTY()).isObstacle);
        System.out.println("spaceGrid.getCell(1,0) = " + spaceGrid.getCell(1, 0));
        Assert.assertTrue(spaceGrid.getCell(1,0).orElse(SpaceCell.EMPTY()).isObstacle);
        Assert.assertTrue(spaceGrid.getCell(2,1).orElse(SpaceCell.EMPTY()).isObstacle);
        Assert.assertTrue(spaceGrid.getCell(3,1).orElse(SpaceCell.EMPTY()).isObstacle);

    }

    @Test public void whenTriple_thenCorrect() {
        List<Triple<Integer,Integer,Double>> bonusCells= Arrays.asList(
                Triple.of(14,2,2d), Triple.of(14,4,6d)
        );
        System.out.println("bonusCells = " + bonusCells);
    }

    @Test public void whenExtractBonusCellsWithStreams_thenCorrect() {
        List<Triple<Integer,Integer,Double>> bonusCells= Arrays.asList(
                Triple.of(14,2,2d), Triple.of(14,4,6d)
        );

        Assert.assertEquals(0,getBonus(bonusCells,14,1), DELTA);
        Assert.assertEquals(2,getBonus(bonusCells,14,2), DELTA);
        Assert.assertEquals(6,getBonus(bonusCells,14,4), DELTA);

    }

    @Test public void whenNew5times15Grid_thenCorrectBonus() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times15Grid();

        Assert.assertEquals(6,spaceGrid.getCell(14,4).orElseThrow().bonus,DELTA);
        Assert.assertEquals(3,spaceGrid.getCell(14,2).orElseThrow().bonus,DELTA);
        Assert.assertEquals(0,spaceGrid.getCell(14,0).orElseThrow().bonus,DELTA);
        Assert.assertEquals(0,spaceGrid.getCell(1,0).orElseThrow().bonus,DELTA);

    }

    private double getBonus(List<Triple<Integer, Integer, Double>> bonusCells, int col, int row) {
        OptionalDouble optBonus= bonusCells.stream()
                .filter(t -> t.getLeft() == col && t.getMiddle() == row).mapToDouble(t -> t.getRight())
                .findAny();
        return optBonus.orElse(DEFAULT_BONUS);
    }



}
