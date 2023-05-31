package common;

import org.apache.arrow.flatbuf.Int;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestRandUtils {

    @Test
    public void getRandomItemFromStringList() {
        List<String> list= Arrays.asList("a","b");
        String string=RandUtils.getRandomItemFromStringList(list);
        System.out.println("string = " + string);
        Assert.assertTrue(list.contains(string));
    }

    @Test public void getRandomItemFromList() {
        List<Integer> list= Arrays.asList(1,2,3,4);
        RandUtils<Integer> intRand=new RandUtils<>();
        Integer intNum=intRand.getRandomItemFromList(list);
        System.out.println("intNum = " + intNum);
        Assert.assertTrue(list.contains(intNum));
    }

    @Test public void getRandomIntNumber() {
        List<Integer> list= Arrays.asList(1,2,3,4);
        Integer intNum=RandUtils.getRandomIntNumber(1,4+1);
        System.out.println("intNum = " + intNum);
        Assert.assertTrue(list.contains(intNum));
    }

    @Test public void getRandomDouble() {
        double doubleNum=RandUtils.getRandomDouble(0,1);
        System.out.println("doubleNum = " + doubleNum);
        Assert.assertTrue(doubleNum>=0 && doubleNum<=1);
    }




}
