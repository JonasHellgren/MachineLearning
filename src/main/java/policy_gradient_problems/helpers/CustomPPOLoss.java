package policy_gradient_problems.helpers;

import common.MathUtils;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;

public class CustomPPOLoss  implements ILossFunction  {


    public static final double SMALL = 1e-5;

    double epsilon=0.2;  //todo via constructor also

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

    /**
     * label = [0,0,...,Adv,..,0.2, 0.5,..], first half is one hot coded Adv, second half is old probs
     *
     * label=[action,adv,probOld]
     */

    private INDArray scoreOnePoint(INDArray label, INDArray z, IActivation activationFn, INDArray mask) {
        int nofOut = label.columns();
        INDArray estProbabilities = activationFn.getActivation(z, false);
        double action=label.getDouble(0,0);
        double adv=label.getDouble(0,1);
        double probOld=label.getDouble(0,2);
        double probNew=estProbabilities.getDouble(0, (int) action);
        double probRatio=probNew/Math.max(probOld, SMALL);
        double clippedProbRatio= MathUtils.clip(probRatio,1-epsilon,1+epsilon);
        double ppoScore=Math.min(probRatio*adv,clippedProbRatio*adv);  //maximized
        double cost=-ppoScore;  //minimized
        return Nd4j.valueArrayOf(1,nofOut, cost);
    }


    private static INDArray getEmptyIndMatrix(INDArray labels) {
        return Nd4j.create(labels.rows(), labels.columns());
    }

}
