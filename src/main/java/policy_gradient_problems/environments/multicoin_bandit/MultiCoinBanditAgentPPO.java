package policy_gradient_problems.environments.multicoin_bandit;

import common.ListUtils;
import common_dl4j.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorI;
import policy_gradient_problems.environments.twoArmedBandit.StateBandit;
import policy_gradient_problems.environments.twoArmedBandit.VariablesBandit;
import java.util.List;

/***
 * The parameters to newWithEpsPPOEpsFinDiff(epsPPO,epsilonFinDiff) are critical
 *
 * epsilonFinDiff needs to be relatively large (1e-1) for clipping to be present
 * A smaller epsPPO means less policy change <=> smoother convergence
 */

public class MultiCoinBanditAgentPPO extends AgentA<VariablesBandit> implements AgentNeuralActorI<VariablesBandit> {

    static final int numInput = 1;
    static final INDArray DUMMY_IN = Nd4j.zeros(1, numInput);
    public static final StateBandit DUMMY_STATE = StateBandit.newDefault();
    public static final int NOF_ACTIONS = 2;
    public static final INDArray DUM_LIST_IN = Dl4JUtil.convertListOfLists(List.of(List.of(0d)));

    MultiLayerNetwork actor;
    NetSettings netSettings;
    Dl4JNetFitter netFitter;

    public static MultiCoinBanditAgentPPO newDefault(double learningRate) {
        return new MultiCoinBanditAgentPPO(learningRate);
    }

    public MultiCoinBanditAgentPPO(double learningRate) {
        super(DUMMY_STATE);
        this.actor =createNetwork(learningRate);
        this.netSettings=getNetSettings(learningRate);
        this.netFitter=new Dl4JNetFitter(actor,netSettings);
    }

    public List<Double> getActionProbabilities() {
        return ListUtils.arrayPrimitiveDoublesToList(actor.output(DUMMY_IN).toDoubleVector());
    }

    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> labelList) {
        fit(labelList);
    }

    private static MultiLayerNetwork createNetwork(double learningRate) {
        NetSettings netSettings = getNetSettings(learningRate);
        return MultiLayerNetworkCreator.create(netSettings);
    }

    private static NetSettings getNetSettings(double learningRate) {
        return NetSettings.builder()
                .nHiddenLayers(1).nInput(numInput).nHidden(3).nOutput(2)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(learningRate).momentum(0.5).seed(1234)
                .lossFunction(PPOLoss.newWithEpsPPOEpsFinDiff(1e-2,1e-1))
                .build();
    }


    private void fit(List<List<Double>> labelList) {
        INDArray out=Dl4JUtil.convertListOfLists(labelList);
        netFitter.fit(DUM_LIST_IN,out);
    }



}
