package policy_gradient_problems.the_problems.twoArmedBandit;

import common_dl4j.*;
import common.ListUtils;
import lombok.SneakyThrows;
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
    public static final int NOF_ACTIONS = 2;

    MultiLayerNetwork actor;
    NetSettings netSettings;
    Dl4JNetFitter netFitter;

    public static AgentBanditNeuralActor newDefault(double learningRate) {
        return new AgentBanditNeuralActor(learningRate);
    }

    public AgentBanditNeuralActor(double learningRate) {
        super(DUMMY_STATE);
        this.actor =createNetwork(learningRate);
        this.netSettings=getNetSettings(learningRate);
        this.netFitter=new Dl4JNetFitter(actor,netSettings);
    }

    public List<Double> getActionProbabilities() {
        return ListUtils.arrayPrimitiveDoublesToList(actor.output(DUMMY_IN).toDoubleVector());
    }

    private static MultiLayerNetwork createNetwork(double learningRate) {
        NetSettings netSettings = getNetSettings(learningRate);
        return MultiLayerNetworkCreator.create(netSettings);
    }

    private static NetSettings getNetSettings(double learningRate) {
        return NetSettings.builder()
                .nHiddenLayers(1).nInput(numInput).nHidden(10).nOutput(2)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(learningRate).momentum(0.5).seed(1234)
                .lossFunction(CustomPolicyGradientLossNew.newDefault())
                .build();
    }

    @SneakyThrows
    @Override
    public void fitActorOld(List<Double> in, List<Double> out) {
        throw  new NoSuchMethodException();
    }

    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {
        fit(outList);
    }

    private void fit(List<List<Double>> outList) {
        INDArray dumIn = Dl4JUtil.convertListOfLists(List.of(List.of(0d)));
        INDArray out=Dl4JUtil.convertListOfLists(outList);
        netFitter.fit(dumIn,out);
    }

}
