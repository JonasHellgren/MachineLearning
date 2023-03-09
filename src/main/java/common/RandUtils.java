package common;

import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Random;

public class RandUtils <T> {



    public static String getRandomItemFromStringList(List<String> list) {
        return list.get(RandomUtils.nextInt(0,list.size()));
    }

    public  T getRandomItemFromList(List<T> list) {
        return list.get(RandomUtils.nextInt(0,list.size()));
    }

    public static int getRandomIntNumber(int minInclusive, int maxExclusive) {
        return (int) ((Math.random() * (maxExclusive - minInclusive)) + minInclusive);
    }

    public static double getRandomDouble(double minValue, double maxValue) {
        return minValue+Math.random()*(maxValue-minValue);
    }

}
