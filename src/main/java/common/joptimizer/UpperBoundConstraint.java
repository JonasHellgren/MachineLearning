package common.joptimizer;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;

@Builder
public class UpperBoundConstraint implements ConvexMultivariateRealFunction {

    @NonNull Integer nDim;
    @NonNull Integer idxVariable;
    @NonNull Double ub;

    @Builder.Default
    @Getter  int nIter =0;

    public static UpperBoundConstraint ofSingle(double ub) {
        return UpperBoundConstraint.builder().nDim(1).idxVariable(0).ub(ub).build();
    }

    @Override
    public double value(DoubleMatrix1D dm) {
        nIter++;
        return dm.toArray()[idxVariable]-ub;  //xi-ub<0
    }

    @Override
    public DoubleMatrix1D gradient(DoubleMatrix1D dm) {
        double[] arr= new double[nDim];
        Arrays.fill(arr, 0);
        arr[idxVariable] = 1;
        return DoubleFactory1D.dense.make(arr);
    }

    @Override
    public DoubleMatrix2D hessian(DoubleMatrix1D dm) {
        return  new DenseDoubleMatrix2D(new double[nDim][nDim]);
    }

    @Override
    public int getDim() {
        return nDim;
    }
}
