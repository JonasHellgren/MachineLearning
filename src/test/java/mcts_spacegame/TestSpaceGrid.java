package mcts_spacegame;

import mcts_spacegame.models_space.SpaceCell;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSpaceGrid {

    SpaceGrid spaceGrid;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.newWithNoObstacles(3,3);
    }

    @Test
    public void noObstacles() {
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
    public void threeTimes7Grid() {
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

}
