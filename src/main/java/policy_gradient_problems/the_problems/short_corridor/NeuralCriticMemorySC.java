package policy_gradient_problems.the_problems.short_corridor;

import common.ListUtils;
import common_dl4j.*;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import policy_gradient_problems.abstract_classes.StateI;
import java.util.ArrayList;
import java.util.List;

import static common.ListUtils.findMax;
import static common.ListUtils.findMin;

public class NeuralCriticMemorySC {

    static int NOF_INPUTS = 3, NOF_OUTPUTS = 1;

    MultiLayerNetwork net;
    NormalizerMinMaxScaler normalizerOut;
    Dl4JBatchNetFitter fitter;

    public static NeuralCriticMemorySC newDefault() {
        return new NeuralCriticMemorySC(getDefaultNetSettings());
    }

    public NeuralCriticMemorySC(NetSettings netSettings) {
        this.net= MultiLayerNetworkCreator.create(netSettings);
        net.init();
        this.normalizerOut = createNormalizerOut();
        this.fitter = new Dl4JBatchNetFitter(net,netSettings);
    }


    public void fit(List<List<Double>> in, List<Double> out) {
        List<List<Double>> inListHot=new ArrayList<>();
        for (List<Double> inList:in) {
            List<Double> inHot = createOneHot(inList);
            inListHot.add(inHot);
        }

        INDArray inputNDArray = Dl4JUtil.convertListOfLists(inListHot, NOF_INPUTS);
        INDArray outPutNDArray = Nd4j.create(ListUtils.toArray(out), inListHot.size(), NOF_OUTPUTS);
        //INDArray outPutNDArray = Dl4JUtil.convertListOfLists(List.of(out), NOF_OUTPUTS);  //todo få detta funka
        normalizerOut.transform(outPutNDArray);
        fitter.fit(inputNDArray, outPutNDArray);
    }

    public Double getOutValue(StateI<VariablesSC> state) {
        INDArray indArray = Dl4JUtil.createOneHotAndReshape(NOF_INPUTS, state.getVariables().pos());
        return getOutValue(indArray);
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NOF_INPUTS).nHiddenLayers(3).nHidden(10).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.IDENTITY)
                .nofFitsPerEpoch(1).learningRate(1e-3).momentum(0.9).seed(1234)
                .lossFunction(LossFunctions.LossFunction.MSE.getILossFunction())
                .relativeNofFitsPerBatch(0.5).sizeBatch(16)
                .weightInit(WeightInit.RELU)
                .build();
    }

    private Double getOutValue(INDArray inData) {
        var output = net.output(inData, false);
        normalizerOut.revertFeatures(output);
        return output.getDouble();
    }

    private static List<Double> createOneHot(List<Double> in) {
        List<Double> onHot = ListUtils.createListWithEqualElementValues(NOF_INPUTS, 0d);
        onHot.set(in.get(0).intValue(), 1d);
        return onHot;
    }


    private static NormalizerMinMaxScaler createNormalizerOut() {
        var values=EnvironmentSC.STATE_REWARD_MAP.values().stream().toList();
        var outMinMax = List.of(Pair.create(findMin(values).orElseThrow(), findMax(values).orElseThrow()));
        return Dl4JUtil.createNormalizer(outMinMax);
    }

}
