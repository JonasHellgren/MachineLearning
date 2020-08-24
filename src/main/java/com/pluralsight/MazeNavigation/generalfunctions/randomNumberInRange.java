package com.pluralsight.MazeNavigation.generalfunctions;
import java.util.Random;

public class randomNumberInRange {

    public static int get(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
