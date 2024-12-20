package radialbasis;


import book_rl_explained.radial_basis.Kernel;
import book_rl_explained.radial_basis.Kernels;
import book_rl_explained.radial_basis.RbfNetwork;
import book_rl_explained.radial_basis.TrainData;
import common.math.ScalerLinear;
import org.hellgren.plotters.plotting_2d.HeatMapChartCreator;
import org.hellgren.utilities.list_arrays.ArrayCreator;
import org.hellgren.utilities.list_arrays.ListCreator;
import org.knowm.xchart.SwingWrapper;

import java.util.List;
import java.util.function.BinaryOperator;

public class RunnerRadialBasis3dFunction {

    public static final int LENGTH = 50;
    public static final double F_MAX = 10.0;
    public static final double TOL = 1.0;
    public static final int N_FITS = 1;
    public static final int N_EPOCHS = 300;

    public static final int N_KERNELS_EACH_DIM = 20;
    public static final double LEARNING_RATE = 0.99;
    public static final int WIDTH = 400;
    public static final int HEIGHT = 200;
    public static final int BATCH_LEN = 10;
    static RbfNetwork rbn;

    public static void main(String[] args) {

        BinaryOperator<Double> fcn = (x, y) -> F_MAX * Math.sin((Math.PI / 3) * x * (7-y));
        var fcnData = createArrayData(fcn);
        var kernels = Kernels.empty();

        double xMin=getXData()[0];
        double xMax=getXData()[getXData().length-1];
        double yMin=getYData()[0];
        double yMax=getYData()[getYData().length-1];
        double sigmax = 0.75*((xMax-xMin) / (N_KERNELS_EACH_DIM - 1));
        double sigmay = 0.75*((yMax-yMin) / (N_KERNELS_EACH_DIM - 1));
        var xList=ListCreator.createFromStartToEndWithNofItems(xMin, xMax, N_KERNELS_EACH_DIM);
        var yList=ListCreator.createFromStartToEndWithNofItems(yMin, yMax, N_KERNELS_EACH_DIM);
        for (double x : xList) {
            for (double y : yList) {
                kernels.addKernel(Kernel.ofSigmas(new double[]{x,y}, new double[]{sigmax,sigmay}));
            }
        }

        rbn = RbfNetwork.of(kernels,LEARNING_RATE);
        var trainData= TrainData.emptyFourOutputs();
        int nRows = fcnData.length;
        int nCols = fcnData[0].length;

        var scalerX= new ScalerLinear(0, nCols, xMin,xMax);
        var scalerY= new ScalerLinear(0, nRows, yMin,yMax);
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                var in=List.of(scalerX.calcOutDouble(col),scalerY.calcOutDouble(row));
                var out=fcnData[row][col];
                trainData.addInOutPair(in,out);
            }
        }

        for (int i = 0; i < N_EPOCHS; i++) {
            rbn.train(trainData.createBatch(BATCH_LEN),N_FITS);
        }


        BinaryOperator<Double> fcnRbn = (x, y) -> rbn.outPut(List.of(x,y));
        var rbnData = createArrayData(fcnRbn);


        showData(fcnData, "Ref HeatMap");
        showData(rbnData, "Rbf HeatMap");
    }

    private static void showData(double[][] data, String title) {
        var settings = HeatMapChartCreator.Settings.defaultBuilder()
                .title(title).showDataValues(false)
                .showAxisTicks(false).build();
        var creator = HeatMapChartCreator.of(settings, data, getXData(),getYData());
        new SwingWrapper<>(creator.create()).displayChart();
    }

    private static double[][] createArrayData(BinaryOperator<Double> fcn) {
        double[][] data = new double[LENGTH][LENGTH];
        for (int xi = 0; xi < getXData().length; xi++) {
            for (int yi = 0; yi < getYData().length; yi++) {
                double x = getXData()[xi];
                double y = getYData()[yi];
                data[yi][xi] = fcn.apply(x, y);
            }
        }
        return data;
    }

    private static double[] getXData() {
        return ArrayCreator.createArrayFromStartAndEnd(LENGTH,-3, 3);
    }

    private static double[] getYData() {
        return ArrayCreator.createArrayFromStartAndEnd(LENGTH, 0, 7 );
    }


}
