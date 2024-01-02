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
import policy_gradient_problems.agent_interfaces.AgentNeuralActorI;

import java.util.List;

public class AgentBanditNeuralActor extends AgentA<VariablesBandit> implements AgentNeuralActorI<VariablesBandit> {

    static final int numInput = 1;
    static final INDArray DUMMY_IN = Nd4j.zeros(1, numInput);
    public static final StateBandit DUMMY_STATE = StateBandit.newDefault();

    MultiLayerNetwork actor;

    public static AgentBanditNeuralActor newDefault(double learningRate) {
        return new AgentBanditNeuralActor(learningRate);
    }

    public AgentBanditNeuralActor(double learningRate) {
        super(DUMMY_STATE);
        this.actor =createNetwork(learningRate);
    }

    public List<Double> getActionProbabilities() {
        return ListUtils.arrayPrimitiveDoublesToList(actor.output(DUMMY_IN).toDoubleVector());
    }

    private static MultiLayerNetwork createNetwork(double learningRate) {
        var netSettings= NetSettings.builder()
                .nHiddenLayers(1).nInput(numInput).nHidden(10).nOutput(2)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(learningRate).momentum(0.5).seed(1234)
                .lossFunction(CustomPolicyGradientLoss.newDefault())
                .build();
        return MultiLayerNetworkCreator.create(netSettings);
    }

    @Override
    public void fitActor(List<Double> in, List<Double> out) {
        fit(out);
    }

    public void fit(List<Double> out) {
        actor.fit(DUMMY_IN, Nd4j.create(out));
    }

/*
    public void fit(INDArray out) {
        actor.fit(DUMMY_IN,out);
    }
*/


}
