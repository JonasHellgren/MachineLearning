package common.plotters;

import com.google.common.collect.Lists;
import org.apache.commons.math3.util.Pair;

import java.util.Arrays;
import java.util.List;

/*
                       ______
  stairs example:  ___|      |_____  (0,1,0)

  converts xy data to stair data

  example data:
  x=[0, 1, 2], y=[0, 1, 0]  =>
  x=[0.0, 1.0, 1.0, 2.0, 2.0, 3.0], y=[0.0, 0.0, 1.0, 1.0, 0.0, 0.0]

 */

public class StairDataGenerator {

    private StairDataGenerator() {
    }

    public static Pair<List<Double>, List<Double>> generateWithEndStep(Pair<double[], double[]> xyData) {
        var xyStairData = generate(xyData);
        addEndStep(xyStairData.getFirst(), xyStairData.getSecond(), xyData);
        return Pair.create(xyStairData.getFirst(), xyStairData.getSecond());
    }

    public static Pair<List<Double>, List<Double>> generate(Pair<double[], double[]> xyData) {
        List<Double> xStairData = Lists.newArrayList();
        List<Double> yStairData = Lists.newArrayList();
        addFirstSteps(xStairData, yStairData, xyData);
        return Pair.create(xStairData, yStairData);
    }

    static void addFirstSteps(List<Double> xStairData,
                              List<Double> yStairData,
                              Pair<double[], double[]> xyData) {
        var xData = xyData.getFirst();
        var yData = xyData.getSecond();
        int len = xData.length;
        for (int i = 0; i < len - 1; i++) {
            xStairData.add(xData[i]);
            yStairData.add(yData[i]);
            xStairData.add(xData[i + 1]);
            yStairData.add(yData[i]);
        }
    }

    static void addEndStep(List<Double> xStairData,
                           List<Double> yStairData,
                           Pair<double[], double[]> xyData) {
        var xData = xyData.getFirst();
        var yData = xyData.getSecond();
        int len = xData.length;
        double max = Arrays.stream(xData).max().orElseThrow();
        double min = Arrays.stream(xData).min().orElseThrow();
        double deltaX = (max - min) / (len-1);
        xStairData.add(xData[len - 1]);
        yStairData.add(yData[yData.length - 1]);
        xStairData.add(xData[len - 1] + deltaX);
        yStairData.add(yData[yData.length - 1]);
    }

}
