package common_dl4j;

import com.google.common.base.Preconditions;
import common.MathUtils;
import lombok.AllArgsConstructor;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.ILossFunction;

import java.util.stream.IntStream;

@AllArgsConstructor
public class CustomPPOLoss  implements ILossFunction  {

    public static final double EPSILON = 0.2;
    double epsilon;
    PPOScoreCalculator scoreCalculator;

    public static CustomPPOLoss newDefault() {
        return new CustomPPOLoss(EPSILON);
    }

    public CustomPPOLoss(double epsilon) {
        this.epsilon = epsilon;
        this.scoreCalculator=new PPOScoreCalculator(epsilon);
    }

    @Override
    public double computeScore(INDArray indArray, INDArray indArray1, IActivation iActivation, INDArray indArray2, boolean b) {
        return 0;
    }

    @Override
    public INDArray computeScoreArray(INDArray labels, INDArray preOut, IActivation actFcn, INDArray mask) {
        int numEx= (int) labels.size(0);
        var scoreArray= Nd4j.create(numEx);
        IntStream.range(0,numEx).forEach(i -> {
                var label=labels.getRow(i);
                var outPut=preOut.getRow(i);
                scoreArray.putScalar(i,scoreOnePoint(label,outPut,actFcn));

        });
        return scoreArray;
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
