package common;

import org.apache.commons.lang3.RandomUtils;

import java.util.List;

public class RandUtils <T> {

    public static double calcRandomFromInterval(double minValue, double maxValue) {
        return minValue+Math.random()*(maxValue-minValue);
    }

    public static String getRandomItemFromStringList(List<String> list) {
        return list.get(RandomUtils.nextInt(0,list.size()));
    }

    public  T getRandomItemFromList(List<T> list) {
        return list.get(RandomUtils.nextInt(0,list.size()));
    }
}
