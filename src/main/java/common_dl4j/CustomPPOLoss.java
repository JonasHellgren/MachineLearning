package common_dl4j;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;

import java.util.function.Function;
import java.util.stream.IntStream;

@AllArgsConstructor
public class CustomPPOLoss  implements ILossFunction  {

    public static final double DEF_EPSILON = 0.2;
    public static final double EPSILON_FIN_DIFF = 1e-6;

    double epsilon;  //for PPO clipping
    double eps = EPSILON_FIN_DIFF; // Epsilon value for finite difference calculation

    PPOScoreCalculator scoreCalculator;

    public static CustomPPOLoss newDefault() {
        return new CustomPPOLoss(DEF_EPSILON);
    }

    public CustomPPOLoss(double epsilon) {
        this.epsilon = epsilon;
        this.scoreCalculator=new PPOScoreCalculator(epsilon);
    }

    @Override
    public double computeScore(INDArray labels, INDArray preOut, IActivation actFcn, INDArray mask, boolean average) {
       INDArray scoreArr=computeScoreArray(labels,preOut,actFcn,mask);
       double score=scoreArr.sumNumber().doubleValue();
      return average  ? score/scoreArr.size(0)  : score;
    }

    @Override
    public INDArray computeScoreArray(INDArray labels, INDArray preOut, IActivation actFcn, INDArray mask) {
        int numEx= (int) labels.size(0);
        var scoreArray= Nd4j.create(numEx);
        IntStream.range(0,numEx).forEach(i ->
                scoreArray.putScalar(i,scoreOnePoint(labels.getRow(i),preOut.getRow(i),actFcn)));
        return scoreArray;
    }

    /***
      Computing the gradient numerically for the preOutput
      Can potentially be replaced with some non-numerical
     */

    @Override
    public INDArray computeGradient(INDArray labels, INDArray preOut, IActivation activationFn, INDArray indArray2) {
        Function<INDArray, Double> scoreFunction = (parameters) -> {
            INDArray activatedOutput = activationFn.getActivation(preOut.dup(), true);
            return scoreOnePoint(labels, activatedOutput,activationFn);
        };
        return FiniteDifferenceCalculator.calculateGradient(scoreFunction, preOut, eps);
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
     * label=[action,adv,probOld]
     */

    private double scoreOnePoint(INDArray label, INDArray z, IActivation activationFn) {
        int nofOut = label.columns();
        Preconditions.checkArgument(nofOut==3,"Wrong label definition PPO custom loss");
        INDArray estProbabilities = activationFn.getActivation(z, false);
        double ppoScore = scoreCalculator.calcScore(label, estProbabilities);
        return -ppoScore;  //Negative for maximization in optimization context
    }




    private static INDArray getEmptyIndMatrix(INDArray labels) {
        return Nd4j.create(labels.rows(), labels.columns());
    }

}
