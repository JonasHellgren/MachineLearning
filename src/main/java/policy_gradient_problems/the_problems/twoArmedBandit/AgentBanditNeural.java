package policy_gradient_problems.the_problems.twoArmedBandit;

import common_dl4j.CustomPolicyGradientLoss;
import common.ListUtils;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.AgentNeuralActorI;

import java.util.List;

public class AgentBanditNeural extends AgentA<VariablesBandit> implements AgentNeuralActorI<VariablesBandit> {

    static final int numInput = 1;
    static final INDArray DUMMY_IN = Nd4j.zeros(1, numInput);
    public static final StateBandit DUMMY_STATE = StateBandit.newDefault();

    MultiLayerNetwork actorMemory;

    public static AgentBanditNeural newDefault(double learningRate) {
        return new AgentBanditNeural(learningRate);
    }

    public AgentBanditNeural(double learningRate) {
        super(DUMMY_STATE);
        this.actorMemory =createNetwork(learningRate);
    }

    public List<Double> getActionProbabilities() {
        return ListUtils.arrayPrimitiveDoublesToList(actorMemory.output(DUMMY_IN).toDoubleVector());
    }

    public void fit(INDArray out) {
        actorMemory.fit(DUMMY_IN,out);
    }

    private static MultiLayerNetwork createNetwork(double learningRate) {
        var netSettings= NetSettings.builder()
                .nHiddenLayers(1).nInput(numInput).nHidden(4).nOutput(2)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(learningRate).momentum(0.95).seed(1234)
                .lossFunction(CustomPolicyGradientLoss.newNotNum())
                .build();
        return MultiLayerNetworkCreator.create(netSettings);
    }

    @Override
    public void fitActor(INDArray in, INDArray out) {
        fit(out);
    }
}
