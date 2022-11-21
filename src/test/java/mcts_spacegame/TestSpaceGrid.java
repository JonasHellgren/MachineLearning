package mcts_spacegame;

import mcts_spacegame.models.SpaceGrid;
import mcts_spacegame.models.SpaceGridInterface;
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

        Assert.assertTrue(spaceGrid.getCell(0,0).isOnUpperBorder);
        Assert.assertFalse(spaceGrid.getCell(0,1).isOnUpperBorder);
        Assert.assertTrue(spaceGrid.getCell(0,2).isOnLowerBorder);
        Assert.assertFalse(spaceGrid.getCell(0,0).isObstacle);
        Assert.assertTrue(spaceGrid.getCell(2,0).isGoal);

    }

    @Test
    public void threeTimes7Grid() {
        spaceGrid= SpaceGridInterface.new3times7Grid();

        System.out.println("spaceGrid = " + spaceGrid);

        Assert.assertEquals(3,spaceGrid.getNofRows());
        Assert.assertEquals(7,spaceGrid.getNofColumns());

        Assert.assertFalse(spaceGrid.getCell(0,0).isObstacle);
        Assert.assertTrue(spaceGrid.getCell(1,2).isObstacle);
        Assert.assertTrue(spaceGrid.getCell(2,1).isObstacle);
        Assert.assertTrue(spaceGrid.getCell(3,1).isObstacle);


    }

}
