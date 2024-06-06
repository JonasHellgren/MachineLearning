package common.linear_regression_batch_fitting;

import com.google.common.base.Preconditions;
import common.other.Conditionals;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log
public class DataSetCreator {

    private final List<double[]> xMatList;
    private final List<Double> yVecList;

    public DataSetCreator() {
        xMatList = new ArrayList<>();
        yVecList = new ArrayList<>();
    }

    public void clear() {
        xMatList.clear();
        yVecList.clear();
    }

    public int size() {
        return xMatList.size();
    }

    public void addPoint(double[] x, Double y) {
        Preconditions.checkNotNull(x, "Input vector x cannot be null");
        Preconditions.checkNotNull(y, "Output value y cannot be null");
        xMatList.add(x);
        yVecList.add(y);
    }


    public Pair<RealMatrix, RealVector> createDataSet() {
        Preconditions.checkArgument(size() == yVecList.size(), "Non correct data size");
        Conditionals.executeIfTrue(xMatList.isEmpty(),
                () -> log.warning("No data available for data set creation"));
        double[][] xMatArray = new double[xMatList.size()][];
        double[] yVecArray = new double[yVecList.size()];
        for (int i = 0; i < xMatList.size(); i++) {
            xMatArray[i] = xMatList.get(i);
            yVecArray[i] = yVecList.get(i);
        }
        return new Pair<>(new Array2DRowRealMatrix(xMatArray), new ArrayRealVector(yVecArray));
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (int i = 0; i < xMatList.size(); i++) {
            sb.append("i=").append(i).append(", x=")
                    .append(Arrays.toString(xMatList.get(i)))
                    .append(", y=").append(yVecList.get(i))
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }

}
