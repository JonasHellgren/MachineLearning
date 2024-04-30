package common.plotters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    /**
     * Generates a set of random walk data
     *
     * @param numPoints
     * @return
     */
    public static double[] getRandomWalk(int numPoints) {

        double[] y = new double[numPoints];
        y[0] = 0;
        for (int i = 1; i < y.length; i++) {
            y[i] = y[i - 1] + Math.random() - .5;
        }
        return y;
    }

    public static List<Double> getGaussianData(int count) {

        List<Double> data = new ArrayList<>(count);
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            data.add(r.nextGaussian() * 10);
        }
        return data;
    }

}
