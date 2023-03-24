package monte_carlo_search.mcts_classes;

import common.ListUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestDiscountListMethods {


    private static final double DELTA = 0.1;

    /**
     *  list = [10 10 10], df=0.5 => listDf=[1*df^0 1*df^1 1*df^2] => dotProduct(list,listDf)=10+5+2.5
     */

    @Test
    public void whenDiscountedSum_thenCorrectSum() {
        List<Double> list= Arrays.asList(10d,10d,10d);
        double ds= ListUtils.discountedSum(list,0.5);
        System.out.println("ds = " + ds);
        Assert.assertEquals(17.5, ds, DELTA);
    }

    @Test
    public void whenDiscountedElements_thenCorrect() {
        List<Double> list= Arrays.asList(10d,10d,10d);
        List<Double>  ds= ListUtils.discountedElements(list,0.5);
        System.out.println("ds = " + ds);

        Assert.assertTrue(ds.containsAll(Arrays.asList(10d,5d,2.5d)));
    }

    @Test
    public void whenReverseDiscountedElements_thenCorrect() {
        List<Double> list= Arrays.asList(1d,10d,10d);
        List<Double>  ds= ListUtils.discountedElementsReverse(list,0.5);
        System.out.println("ds = " + ds);

        Assert.assertTrue(ds.containsAll(Arrays.asList(0.25d,5d,10d)));
    }

    /**
     * rewards = 1d,10d,10d , df=0.5-> rewardsDiscounted =  0.25d,5d,10d -> returns = 15.25, 15, 10
     */

    @Test
    public void givenRewardsAnd0d5Discount_thenCorrectReturns() {
        List<Double> rewards= Arrays.asList(1d,10d,10d);
        List<Double>  returns= getDiscountedReturns(rewards,0.5);
        System.out.println("returns = " + returns);
        Assert.assertTrue(returns.containsAll(Arrays.asList(15.25,15d,10d)));
    }


    private List<Double> getDiscountedReturns(final List<Double> rewards, double discountFactor) {
        List<Double> rewardsDiscounted  = ListUtils.discountedElementsReverse(rewards,discountFactor);
        return ListUtils.getReturns(rewardsDiscounted);
    }

    private List<Double> getDiscountedReturnsOld(final List<Double> rewards, double discountFactor) {
        List<Double> returns = ListUtils.getReturns(rewards);
        return ListUtils.discountedElementsReverse(returns,discountFactor);
    }

}
