package policy_gradient_problems.the_problems.twoArmedBandit;

import common_dl4j.CustomPolicyGradientLoss;
import common.ListUtils;
import common_dl4j.Dl4JUtil;
import common_dl4j.MultiLayerNetworkCreator;
import common_dl4j.NetSettings;
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
                .lossFunction(CustomPolicyGradientLoss.newDefault())
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

        var out=Dl4JUtil.convertListOfLists(outList, NOF_ACTIONS);
       // INDArray dumIn = Nd4j.zeros(nofDataPoints, numInput);
        //INDArray out = Nd4j.create(ListUtils.toArray(out), length, NOF_ACTIONS);
/*

        System.out.println("nofDataPoints = " + nofDataPoints);
        System.out.println("outList = " + outList);
        System.out.println("dumIn = " + dumIn);
        System.out.println("dumIn.shapeInfoToString() = " + dumIn.shapeInfoToString());
        System.out.println("out = " + out);
        System.out.println("out.shapeInfoToString() = " + out.shapeInfoToString());

*/

        INDArray input = Nd4j.zeros(1, 1);
        // correspondending list with expected output values
        INDArray labels = Nd4j.zeros(1, 2);
        // create first dataset, when first input=0 and second input=0
        input.putScalar(new int[]{0, 0}, 0);
        labels.putScalar(new int[]{0, 0}, 1);    labels.putScalar(new int[]{0, 1}, 0);

/*
        System.out.println("input.shapeInfoToString() = " + input.shapeInfoToString());
        System.out.println("labels.shapeInfoToString() = " + labels.shapeInfoToString());
*/
        DataSet ds = new DataSet(input, labels);

        System.out.println("ds = " + ds);

        actor.fit(ds);


        //actor.fit(input, labels);
    }


/*
    public void fit(INDArray out) {
        actor.fit(DUMMY_IN,out);
    }
*/


}
