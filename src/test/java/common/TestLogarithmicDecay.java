package common;

import common.math.LogarithmicDecay;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestLogarithmicDecay {

    private static final double DELTA = 0.1;
    LogarithmicDecay decayOneToZeroInTime10;
    LogarithmicDecay decayOneToDot1InTime100;
    @Before
    public void init() {
        decayOneToZeroInTime10=new LogarithmicDecay(1,0,10);
        decayOneToDot1InTime100=new LogarithmicDecay(1,0.01,100);
    }

    @Test
    public void givenDecayOneToZeroInTime10_thenTimeZeroGives1 () {
        printDecay(decayOneToZeroInTime10);
        Assert.assertEquals(1,decayOneToZeroInTime10.calcOut(0), DELTA);
    }

    @Test
    public void givenDecayOneToZeroInTime10_thenTime10oGives0 () {
        printDecay(decayOneToZeroInTime10);
        Assert.assertEquals(0,decayOneToZeroInTime10.calcOut(10), DELTA);
    }

    @Test
    public void givenDecayOneToZeroInTime10_thenTimeDot3oGivesZeroDot3 () {
        printDecay(decayOneToZeroInTime10);
        Assert.assertEquals(0.3,decayOneToZeroInTime10.calcOut(0.3), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenDecayOneToZeroInTime11_thenException () {
        printDecay(decayOneToZeroInTime10);
        Assert.assertEquals(0,decayOneToZeroInTime10.calcOut(11), DELTA);
    }

    @Test
    public void givenDecayOneToDot01InTime100_thenCorrectCurve () {
        printDecay(decayOneToDot1InTime100);
        Assert.assertEquals(0.6,decayOneToDot1InTime100.calcOut(10), DELTA);
        Assert.assertEquals(0.1,decayOneToDot1InTime100.calcOut(50), DELTA);
        Assert.assertEquals(0.01,decayOneToDot1InTime100.calcOut(99), DELTA);
    }

    private void printDecay(LogarithmicDecay decay) {
        System.out.println("decay = " + decay.toString());
    }


}
