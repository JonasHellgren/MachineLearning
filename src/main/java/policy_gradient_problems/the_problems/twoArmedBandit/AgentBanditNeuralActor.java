package policy_gradient_problems.the_problems.twoArmedBandit;

import common_dl4j.*;
import common.ListUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorI;

import java.util.List;

public class AgentBanditNeuralActor extends AgentA<VariablesBandit> implements AgentNeuralActorI<VariablesBandit> {

    static final int numInput = 1;
    static final INDArray DUMMY_IN = Nd4j.zeros(1, numInput);
    public static final StateBandit DUMMY_STATE = StateBandit.newDefault();
    public static final int NOF_ACTIONS = 2;

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
                .lossFunction(CustomPolicyGradientLossNew.newDefault())
                //.lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD.getILossFunction())

                .build();
        return MultiLayerNetworkCreator.create(netSettings);
    }

    @Override
    public void fitActorOld(List<Double> in, List<Double> out) {
        fitOld(out);
    }


    private void fitOld(List<Double> out) {
        INDArray labels = Nd4j.create(out);

        System.out.println("DUMMY_IN = " + DUMMY_IN);
        System.out.println("DUMMY_IN.shapeInfoToString() = " + DUMMY_IN.shapeInfoToString());
        System.out.println("labels = " + labels);
        System.out.println("labels.shapeInfoToString() = " + labels.shapeInfoToString());

        actor.fit(DUMMY_IN, labels);
    }

    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {
        fit(outList);
    }

    private void fit(List<List<Double>> outList) {
        int nofDataPoints=outList.size();
        INDArray dumIn = Dl4JUtil.convertListOfLists(List.of(List.of(0d)), numInput);
        INDArray out=Dl4JUtil.convertListOfLists(outList, NOF_ACTIONS);

        actor.fit(dumIn, out);
    }


/*
    public void fit(INDArray out) {
        actor.fit(DUMMY_IN,out);
    }
*/


}
