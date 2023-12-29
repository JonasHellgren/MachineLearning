package policy_gradient_problems.the_problems.twoArmedBandit;

import common_dl4j.CustomPolicyGradientLoss;
import common.ListUtils;
import common.RandUtils;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
import lombok.SneakyThrows;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.StateI;

import java.util.List;
import static common.IndexFinder.findBucket;
import static policy_gradient_problems.common.BucketLimitsHandler.*;

public class AgentBanditNeural  implements AgentI<Integer> {  //todo AgentNeuralPolicyInterface

    static final int seed = 12345;
    static final double momentum = 0.95;
    static final int numInput = 1;
    static final INDArray IN = Nd4j.zeros(1, numInput);
    static final int numOutputs = 2;
    static final int nHidden = 4;

    MultiLayerNetwork network;

    public static AgentBanditNeural newDefault(double learningRate) {
        return new AgentBanditNeural(learningRate);
    }

    public AgentBanditNeural(double learningRate) {
        this.network=createNetwork(learningRate);
    }

    public int chooseActionOld() {
        var limits = getLimits(getActionProbabilities());
        throwIfBadLimits(limits);
        return findBucket(ListUtils.toArray(limits), RandUtils.randomNumberBetweenZeroAndOne());
    }

    @SneakyThrows
    @Override
    public Action chooseAction() {
        throw new NoSuchMethodException();
    }

    @SneakyThrows
    @Override
    public void setState(StateI state) {
        throw new NoSuchMethodException();
    }

    public List<Double> getActionProbabilities() {
        return ListUtils.arrayPrimitiveDoublesToList(network.output(IN).toDoubleVector());
    }

    public void fit(INDArray out) {
        network.fit(IN,out);
    }

    private static MultiLayerNetwork createNetwork(double learningRate1) {
        var netSettings= NetSettings.builder()
                .nHiddenLayers(1).nInput(numInput).nHidden(nHidden).nOutput(numOutputs)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(learningRate1).momentum(momentum).seed(seed)
                .lossFunction(CustomPolicyGradientLoss.newNotNum())
                .build();
        return MultiLayerNetworkCreator.create(netSettings);
    }

}
