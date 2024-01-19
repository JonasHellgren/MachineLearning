package policy_gradient_problems.common_helpers;

import common_dl4j.EntropyCalculator;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;

public class CustomPPOLoss  implements ILossFunction  {


    @Override
    public double computeScore(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2, boolean b) {
        return 0;
    }

    @Override
    public INDArray computeScoreArray(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2) {
        return null;
    }

    @Override
    public INDArray computeGradient(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2) {
        return null;
    }

    @Override
    public Pair<Double, INDArray> computeGradientAndScore(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2, boolean b) {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    private INDArray scoreOnePoint(INDArray label, INDArray z, IActivation activationFn, INDArray mask) {
        int nofOut = label.columns();
        INDArray scoreAllPoints = getEmptyIndMatrix(label);
        for (int i = 0; i < nofOut; i++) {
            INDArray estProbabilities = activationFn.getActivation(z, false);
            double ce = EntropyCalculator.calcCrossEntropy(label, estProbabilities);
            double entropy = EntropyCalculator.calcEntropy(estProbabilities);
            double K = 1; //getK(estProbabilities);
       //     scoreAllPoints.putScalar(0,i,ce - beta * K * entropy);
        }
        return scoreAllPoints.reshape(nofOut);
    }


    private static INDArray getEmptyIndMatrix(INDArray labels) {
        return Nd4j.create(labels.rows(), labels.columns());
    }

}
