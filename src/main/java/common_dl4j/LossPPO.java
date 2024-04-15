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

import static common_dl4j.FiniteDifferenceCalculator.calculateGradient;

@AllArgsConstructor
public class LossPPO implements ILossFunction  {

    public static final int ACTION_INDEX = 0;
    public static final int ADV_INDEX = 1;
    public static final int PROB_OLD_INDEX = 2;
    public static final double MIN_STD = 0.05;
    public static final int MAX_STD = 10;
    public static final int STD_CONT = 1;

    public static final double DEF_EPSILON = 0.1;
    public static final double EPSILON_FIN_DIFF = 1e-3;
    public static final double BETA_ENTROPY = 1e-2;
    public static final EntropyCalculatorDiscreteActions ENTROPY_DISC = new EntropyCalculatorDiscreteActions();
    public static final EntropyCalculatorContActions ENTROPY_CONT = new EntropyCalculatorContActions();

    double epsilonFinDiff; // Epsilon value for finite difference calculation
    double beta;
    PPOScoreCalculatorI scoreCalculator;
    EntropyCalculatorI entropyCalculator;

    public static LossPPO newDefaultDiscrete() {
        return new LossPPO(EPSILON_FIN_DIFF, BETA_ENTROPY,new PPOScoreCalculatorDiscreteAction(DEF_EPSILON), ENTROPY_DISC);
    }

    public static LossPPO newWithEpsilonPPODiscrete(double epsilon) {
        return new LossPPO(EPSILON_FIN_DIFF, BETA_ENTROPY,new PPOScoreCalculatorDiscreteAction(epsilon), ENTROPY_DISC);
    }

    public static LossPPO newWithEpsPPOEpsFinDiffDiscrete(double epsPPO, double epsilonFinDiff) {
        return new LossPPO(epsilonFinDiff,BETA_ENTROPY,new PPOScoreCalculatorDiscreteAction(epsPPO), ENTROPY_DISC);
    }

    public static LossPPO newWithEpsPPOEpsFinDiffBetaEntropyDiscrete(double epsPPO, double epsilonFinDiff, double beta) {
        return new LossPPO(epsilonFinDiff,beta,new PPOScoreCalculatorDiscreteAction(epsPPO), ENTROPY_DISC);
    }

    public static LossPPO newWithEpsPPOBetaEntropyCont(double epsPPO, double beta) {
        return new LossPPO(EPSILON_FIN_DIFF,beta,new PPOScoreCalculatorContAction(epsPPO),ENTROPY_CONT);
    }

    public static LossPPO newWithEpsPPOEpsFinDiffBetaEntropyCont(double epsPPO, double epsilonFinDiff, double beta) {
        return new LossPPO(epsilonFinDiff,beta,new PPOScoreCalculatorContAction(epsPPO),ENTROPY_CONT);
    }

    @Override
    public double computeScore(INDArray labels,
                               INDArray preOut,
                               IActivation actFcn,
                               INDArray mask,
                               boolean average) {
        INDArray scoreArr=computeScoreArray(labels,preOut,actFcn,mask);
       double score=scoreArr.sumNumber().doubleValue();
        return average  ? score/scoreArr.size(0)  : score;
    }

    @Override
    public INDArray computeScoreArray(INDArray labels,
                                      INDArray preOut,
                                      IActivation actFcn,
                                      INDArray mask) {
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
        Preconditions.checkArgument(labels.rank()==2,"Rank label is not 2");
        Preconditions.checkArgument(labels.rank()==2,"Rank preOut is not 2");

        var gradArray= Nd4j.create(preOut.rows(), preOut.columns());
        IntStream.range(0,labels.rows()).forEach(i -> {
            Function<INDArray, Double> scoreFunction = (p) -> scoreOnePoint(labels.getRow(i), p, activationFn);
            INDArray gradArr = calculateGradient(scoreFunction, preOut.getRow(i), epsilonFinDiff);
            gradArray.putRow(i, gradArr);
        });
        return gradArray;
    }

    @Override
    public Pair<Double, INDArray> computeGradientAndScore(INDArray labels,
                                                          INDArray preOut,
                                                          IActivation actFcn,
                                                          INDArray mask,
                                                          boolean avg) {
        return Pair.of(
                computeScore(labels, preOut, actFcn, mask, avg),
                computeGradient(labels, preOut, actFcn, mask));
    }

    @Override
    public String name() {
        return "PPO";
    }

    /**
     * label=[action,adv,probOld]
     */

    private double scoreOnePoint(INDArray label, INDArray z, IActivation activationFn) {
        int nofOut = label.columns();
        Preconditions.checkArgument(nofOut==3,"Wrong label definition PPO custom loss, label="+label);
        INDArray estProbabilities = activationFn.getActivation(z, false);
        double entropy = entropyCalculator.calcEntropy(estProbabilities);
        double ppoScore = scoreCalculator.calcScore(label, estProbabilities);

       //System.out.println("label = " + label+", estProbabilities = " + estProbabilities+", ppoScore = " + ppoScore);
       // System.out.println("entropy = " + entropy);

        return -(ppoScore+ beta *entropy);  //Negative for maximization in optimization context
    }


}
