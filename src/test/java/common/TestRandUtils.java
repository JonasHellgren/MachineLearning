package common;

import common.other.RandUtilsML;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestRandUtils {

    @Test
    public void getRandomItemFromStringList() {
        List<String> list= Arrays.asList("a","b");
        String string= RandUtilsML.getRandomItemFromStringList(list);
        System.out.println("string = " + string);
        Assert.assertTrue(list.contains(string));
    }

    @Test public void getRandomItemFromList() {
        List<Integer> list= Arrays.asList(1,2,3,4);
        RandUtilsML<Integer> intRand=new RandUtilsML<>();
        Integer intNum=intRand.getRandomItemFromList(list);
        System.out.println("intNum = " + intNum);
        Assert.assertTrue(list.contains(intNum));
    }

    @Test public void getRandomIntNumber() {
        List<Integer> list= Arrays.asList(1,2,3,4);
        Integer intNum= RandUtilsML.getRandomIntNumber(1,4+1);
        System.out.println("intNum = " + intNum);
        Assert.assertTrue(list.contains(intNum));
    }

    @Test public void getRandomDouble() {
        double doubleNum= RandUtilsML.getRandomDouble(0,1);
        System.out.println("doubleNum = " + doubleNum);
        Assert.assertTrue(doubleNum>=0 && doubleNum<=1);
    }




}
