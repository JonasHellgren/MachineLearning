package radialbasis;

import book_rl_explained.radial_basis.Kernel;
import book_rl_explained.radial_basis.Kernels;
import book_rl_explained.radial_basis.RbfNetwork;
import book_rl_explained.radial_basis.TrainData;
import org.hellgren.plotters.plotting_2d.HeatMapChartCreator;
import org.hellgren.utilities.list_arrays.ArrayCreator;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.knowm.xchart.SwingWrapper;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.IntStream;

class RunnerRadialBasis3dFunction {

    static final int LENGTH = 50;
    static final double F_MAX = 10.0;
    static final int N_FITS = 1;
    static final int N_EPOCHS = 300;
    static final int BATCH_LEN = 10;
    static final int N_KERNELS_EACH_DIM = 20;
    static final double K_SIGMA = 0.75;
    static final double LEARNING_RATE = 0.99;
    static RbfNetwork rbfn;

    public static void main(String[] args) {
        DoubleBinaryOperator fcn = (x, y) -> F_MAX * Math.sin((Math.PI / 3) * x * (7 - y));
        var kernels = createKernels();
        rbfn = RbfNetwork.of(kernels, LEARNING_RATE);
        var trainData = createTrainData(fcn);
        IntStream.range(0, N_EPOCHS)
                .forEach(i -> rbfn.train(trainData.createBatch(BATCH_LEN), N_FITS));
        showData(createArrayData(fcn), "Ref HeatMap");
        DoubleBinaryOperator fcnRbn = (x, y) -> rbfn.outPut(List.of(x, y));
        showData(createArrayData(fcnRbn), "Rbf HeatMap");
    }

    private static Kernels createKernels() {
        var kernels = Kernels.empty();
        double xMin = getXData()[0];
        double xMax = getXData()[getXData().length - 1];
        double yMin = getYData()[0];
        double yMax = getYData()[getYData().length - 1];
        double sigmaX = K_SIGMA * ((xMax - xMin) / (N_KERNELS_EACH_DIM - 1));
        double sigmaY = K_SIGMA * ((yMax - yMin) / (N_KERNELS_EACH_DIM - 1));
        var xList = ListCreator.createFromStartToEndWithNofItems(xMin, xMax, N_KERNELS_EACH_DIM);
        var yList = ListCreator.createFromStartToEndWithNofItems(yMin, yMax, N_KERNELS_EACH_DIM);
        for (double x : xList) {
            for (double y : yList) {
                kernels.addKernel(Kernel.ofSigmas(new double[]{x, y}, new double[]{sigmaX, sigmaY}));
            }
        }
        return kernels;
    }

    private static TrainData createTrainData(DoubleBinaryOperator fcn) {
        var trainData = TrainData.emptyFourOutputs();
        for (double x : getXData()) {
            for (double y : getYData()) {
                trainData.addInOutPair(List.of(x, y), fcn.applyAsDouble(x, y));
            }
        }
        return trainData;
    }

    private static void showData(double[][] data, String title) {
        var settings = HeatMapChartCreator.Settings.defaultBuilder()
                .title(title).showDataValues(false)
                .showAxisTicks(false).build();
        var creator = HeatMapChartCreator.of(settings, data, getXData(), getYData());
        new SwingWrapper<>(creator.create()).displayChart();
    }

    private static double[][] createArrayData(DoubleBinaryOperator fcn) {
        double[][] data = new double[LENGTH][LENGTH];
        for (int xi = 0; xi < getXData().length; xi++) {
            for (int yi = 0; yi < getYData().length; yi++) {
                double x = getXData()[xi];
                double y = getYData()[yi];
                data[yi][xi] = fcn.applyAsDouble(x, y);
            }
        }
        return data;
    }

    private static double[] getXData() {
        return ArrayCreator.createArrayFromStartAndEnd(LENGTH, -3, 3);
    }

    private static double[] getYData() {
        return ArrayCreator.createArrayFromStartAndEnd(LENGTH, 0, 7);
    }


}
