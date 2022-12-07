package mcts_spacegame;

import common.MathUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestMathOnListElements {

    List<Double> listA;
    List<Double> listB;


    @Before
    public void init() {
        listA= Arrays.asList(1d,2d,3d,4d,5d);
        listB= Arrays.asList(11d,12d,13d,14d,15d);
    }

    @Test
    public void multiplyListWithScalar () {

        List<Double> listC= MathUtils.multiplyListElements(listA,10);
        System.out.println("listC = " + listC);

        Assert.assertTrue(listC.containsAll(Arrays.asList(10d,20d,30d,40d,50d)));
    }

    @Test
    public void multiplyListWithNegativeScalar () {

        List<Double> listC= MathUtils.multiplyListElements(listA,-10);
        System.out.println("listC = " + listC);

        Assert.assertTrue(listC.containsAll(Arrays.asList(-10d,-20d,-30d,-40d,-50d)));
    }

    @Test
    public void sumLists () {
        List<Double> listC= MathUtils.sumListElements(listA,listB);
        System.out.println("listC = " + listC);
        Assert.assertTrue(listC.containsAll(Arrays.asList(12d,14d,16d,18d,20d)));
    }

    @Test
    public void sumListsOneIsNegated () {
        List<Double> listC= MathUtils.sumListElements(listA,MathUtils.multiplyListElements(listB,-1));
        System.out.println("listC = " + listC);
        Assert.assertTrue(listC.containsAll(Arrays.asList(-10d,-10d,-10d,-10d,-10d)));
    }

}
