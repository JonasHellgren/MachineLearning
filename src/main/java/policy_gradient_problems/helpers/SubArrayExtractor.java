package policy_gradient_problems.helpers;

import common.ArrayUtil;
import lombok.AllArgsConstructor;

import static java.lang.System.arraycopy;

/***
 *  An array has the length thetaLength, it is divided into sub arrays, each with length nofThetasPerSubArray
 *  Theta refers to a double number in the array
 */

@AllArgsConstructor
public class SubArrayExtractor {

    int thetaLength;
    int nofThetasPerChunk;

    public double[] arrayWithZeroExceptAtSubArray(int subArrayIndex, double[] thetasInSubArray) {
        double[] gradLogAllStates = ArrayUtil.createArrayWithSameDoubleNumber(thetaLength, 0);
        int indexSource = 0;
        int indexFirstTheta = getIndexFirstThetaForSubArray(subArrayIndex);
        arraycopy(thetasInSubArray, indexSource, gradLogAllStates, indexFirstTheta, nofThetasPerChunk);
        return gradLogAllStates;
    }

    public  int getIndexFirstThetaForSubArray(int subArrayIndex) {
        return subArrayIndex * nofThetasPerChunk;
    }

}
