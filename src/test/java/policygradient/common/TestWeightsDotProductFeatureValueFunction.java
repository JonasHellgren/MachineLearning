package policygradient.common;

import common.ArrayUtil;
import common.RandUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import policy_gradient_problems.common.WeightsDotProductFeatureValueFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWeightsDotProductFeatureValueFunction {


    public static final double ALPHA = 0.1;
    public static final int NOF_FEATURES = 1;
    public static final int NOF_ITERATIONS = 1000;
    public static final double TOL = 0.01;
    WeightsDotProductFeatureValueFunction valueFunction;

    @BeforeEach
    public void init() {
        valueFunction =new WeightsDotProductFeatureValueFunction(NOF_FEATURES, ALPHA);
        trainValueFunction();
    }

    @ParameterizedTest
    @CsvSource({"0.1,1", "0.5,5", "1,10"})
    public void whenUpdated_thenCorrect(ArgumentsAccessor arguments) {
        double x = arguments.getDouble(0);
        double y = arguments.getDouble(1);
        assertEquals(y, valueFunction.getValue(getStateIn(x)), TOL);
    }

    private void trainValueFunction() {
        ArrayRealVector in=new ArrayRealVector(ArrayUtil.createArrayInRange(0,0.1,1));
        ArrayRealVector out=new ArrayRealVector(ArrayUtil.createArrayInRange(0,1.0,10));

        for (int i = 0; i < NOF_ITERATIONS; i++) {
            int randIndex= RandUtils.getRandomIntNumber(in.getMinIndex(),in.getMaxIndex()+1);
            ArrayRealVector stateIn = getStateIn(in.getEntry(randIndex));
            double valueRef = out.getEntry(randIndex);
            valueFunction.update(stateIn,valueRef);
        }
    }

    @NotNull
    private static ArrayRealVector getStateIn(double inValue) {
        return new ArrayRealVector(new double[]{inValue});
    }


}
