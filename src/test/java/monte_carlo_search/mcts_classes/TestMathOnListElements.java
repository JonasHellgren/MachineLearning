package monte_carlo_search.mcts_classes;

import common.list_arrays.ListUtils;
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
    public void whenMultiplyListWithScalar_thenCorrect () {

        List<Double> listC= ListUtils.multiplyListElements(listA,10);
        System.out.println("listC = " + listC);

        Assert.assertTrue(listC.containsAll(Arrays.asList(10d,20d,30d,40d,50d)));
    }

    @Test
    public void whenMultiplyListWithNegativeScalar_thenCorrect () {

        List<Double> listC= ListUtils.multiplyListElements(listA,-10);
        System.out.println("listC = " + listC);

        Assert.assertTrue(listC.containsAll(Arrays.asList(-10d,-20d,-30d,-40d,-50d)));
    }

    @Test
    public void whenSumLists_thenCorrect () {
        List<Double> listC= ListUtils.sumListElements(listA,listB);
        System.out.println("listC = " + listC);
        Assert.assertTrue(listC.containsAll(Arrays.asList(12d,14d,16d,18d,20d)));
    }

    @Test
    public void whenSumListsOneIsNegated_thenCorrect () {
        List<Double> listC= ListUtils.sumListElements(listA,ListUtils.multiplyListElements(listB,-1));
        System.out.println("listC = " + listC);
        Assert.assertTrue(listC.containsAll(Arrays.asList(-10d,-10d,-10d,-10d,-10d)));
    }

}
