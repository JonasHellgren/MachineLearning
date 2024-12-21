package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class Weights {

    double[] weights;

    public static Weights allZero(int size) {
       return new Weights(createWeightsAllZero(size));
    }

    public static Weights allWithValue(int size, double value) {
        var weights= new Weights(createWeightsAllZero(size));
        for (int i = 0; i < size; i++) {
            weights.set(i, value);
        }
        return weights;
    }

    public double get(int index) {
        Preconditions.checkArgument(index >= 0 && index < weights.length,
                "Non valid weight index");
        return weights[index];
    }


    public double[] getArray() {
        return weights;
    }

    public int size() {
        return weights.length;
    }

    public void setWeights(double[] doubles) {
        Preconditions.checkArgument(doubles.length == size(),
                "weights and doubles should have same length");
        for (int i = 0; i <size() ; i++) {
            set(i,doubles[i]);
        }
    }

    public void set(int i, double v) {
        weights[i] = v;
    }

    private static double[] createWeightsAllZero(int size) {
        return new double[size];
    }


    @Override
    public String toString() {
        return "Weights{" +
                "weights=" + Arrays.toString(weights) +
                '}';
    }

}
