package super_vised.radial_basis;

import com.google.common.base.Preconditions;
import common.list_arrays.Array2ListConverter;
import common.list_arrays.List2ArrayConverter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class WeightUpdater {

    public static final double LEARNING_RATE = 0.01;
    RadialBasis radialBasis;
    double learningRate;

    public static WeightUpdater of(RadialBasis radialBasis) {
        return new WeightUpdater(radialBasis, LEARNING_RATE);
    }

    public List<Double> updateWeights(List<List<Double>> inputs, List<Double>  yTargets, List<Double> weightList) {
        double[][] array = List2ArrayConverter.convertListWithListToDoubleMat(inputs);
        double[] vector = List2ArrayConverter.convertListToDoubleArr(yTargets);
        double[] weights = List2ArrayConverter.convertListToDoubleArr(weightList);
        return Array2ListConverter.convertDoubleArrToList(updateWeights(array,vector, weights));
    }


    public double[]  updateWeights(double[][] inputs, double[] yTargets, double[] weights) {
        double[] gradient = weightGradient(inputs, yTargets, weights);
        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * gradient[i];
        }
        return weights;
    }

    public List<Double> weightGradient(List<List<Double>> inputs, List<Double>  yTargets, List<Double>  weightList) {
        double[][] array = List2ArrayConverter.convertListWithListToDoubleMat(inputs);
        double[] vector = List2ArrayConverter.convertListToDoubleArr(yTargets);
        double[] weights = List2ArrayConverter.convertListToDoubleArr(weightList);
        return Array2ListConverter.convertDoubleArrToList(weightGradient(array,vector, weights));
    }

    public  double[] weightGradient(double[][] inputs, double[] yTargets, double[] weights) {
        int nExamples= inputs.length;
        Preconditions.checkArgument(nExamples == yTargets.length, "inputs and yTargets should be same length");
        int nKernels = radialBasis.nKernels();
        double[] gradient = new double[nKernels];
        for (int idxKernel = 0; idxKernel < nKernels; idxKernel++) {
            for (int idxExample = 0; idxExample < inputs.length; idxExample++) {
                double[] x = inputs[idxExample];
                double activation = radialBasis.activation(x, radialBasis.getKernel(idxKernel));
                double yTarget = yTargets[idxExample];
                double yPredicted=radialBasis.output(x, weights);
                gradient[idxKernel] += (yTarget-yPredicted) * activation;
            }
            gradient[idxKernel]=gradient[idxKernel]/nExamples;
        }
        return gradient;
    }

    public static double[] convertListToDoubleArr(List<Double> inList) {
        double[] outArray = new double[inList.size()];
        for (int i = 0; i < inList.size(); i++) {
            outArray[i] = inList.get(i); // Auto-unboxing converts Double to double
        }
        return outArray;
    }

}
