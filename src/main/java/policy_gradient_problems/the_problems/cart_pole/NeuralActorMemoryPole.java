package policy_gradient_problems.the_problems.cart_pole;

import common.ListUtils;
import common_dl4j.*;
import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;
import java.util.Random;

import static common.ListUtils.arrayPrimitiveDoublesToList;

/**
 * The out value is probabilitiesm in [0,1], hence shall not be reverted in getOutValue
 * But normalization is needed in the fit method
 *
 */

public class NeuralActorMemoryPole {
    static int NOF_INPUTS = StatePole.newUprightAndStill().asList().size(), NOF_OUTPUTS = EnvironmentPole.NOF_ACTIONS;

    //todo StatePole.noActions()

    MultiLayerNetwork net;
    NormalizerMinMaxScaler normalizerIn, normalizerOut;
    Dl4JBatchNetFitter fitter;

    public static NeuralActorMemoryPole newDefault(ParametersPole parametersPole) {
        return new NeuralActorMemoryPole(getDefaultNetSettings(),parametersPole);
    }

    public NeuralActorMemoryPole(NetSettings netSettings, ParametersPole parametersPole) {
        this.net= MultiLayerNetworkCreator.create(netSettings);
        this.normalizerIn = createNormalizerIn(parametersPole);
        this.normalizerOut = createNormalizerOut(parametersPole);
        net.init();
        this.fitter=new Dl4JBatchNetFitter(net,netSettings);
    }

    public void fit(List<List<Double>> in, List<List<Double>> outList) {
        INDArray inAsNormalized = getInAsNormalized(in.get(0));
        INDArray outAsNormalized = getOutAsNormalized(outList.get(0));

        inAsNormalized=inAsNormalized.reshape(1,4);
        outAsNormalized=outAsNormalized.reshape(1,2);

      //  INDArray inAsNormalized=Nd4j.create(new float[]{1,1,1,1},new int[]{1,4});
      //  INDArray outAsNormalized=Nd4j.create(new float[]{1,1},new int[]{1,2});


        net.fit(inAsNormalized, outAsNormalized);
     //   fitter.train(in,out,1);   //todo apply*/

/*

        INDArray inputNDArray = Dl4JUtil.convertListOfLists(in, NOF_INPUTS);
        //INDArray inputNDArray = Dl4JUtil.convertListOfLists(in, NOF_INPUTS);

        //INDArray outPutNDArray = Nd4j.create(ListUtils.toArray(outList), in.size(), NOF_OUTPUTS);
              INDArray outPutNDArray = Dl4JUtil.convertListOfLists(outList, NOF_OUTPUTS);
        normalizerIn.transform(inputNDArray);
        normalizerOut.transform(outPutNDArray);

        System.out.println("inputNDArray = " + inputNDArray);
        System.out.println("outPutNDArray = " + outPutNDArray);
*/



    //    fitter.fit(inAsNormalized,outAsNormalized);


    }

    public List<Double> getOutValue(List<Double> in) {
        return arrayPrimitiveDoublesToList(net.output(getInAsNormalized(in)).toDoubleVector());
    }

    public double getError() {
        return net.gradientAndScore().getSecond();
    }

    private INDArray getInAsNormalized(List<Double> in) {
        INDArray indArray = Nd4j.create(in);
        normalizerIn.transform(indArray);
        indArray=indArray.reshape(1,NOF_INPUTS);
        return indArray;
    }

    private INDArray getOutAsNormalized(List<Double> out) {
        INDArray indArray = Nd4j.create(out);
        normalizerOut.transform(indArray);
        return indArray;
    }

    private static NetSettings getDefaultNetSettings() {
        return NetSettings.builder()
                .nInput(NOF_INPUTS).nHiddenLayers(3).nHidden(20).nOutput(NOF_OUTPUTS)
                .activHiddenLayer(Activation.RELU).activOutLayer(Activation.SOFTMAX)
                .nofFitsPerEpoch(1).learningRate(1e-3).momentum(0.95).seed(1234)
                .lossFunction(CustomPolicyGradientLossNew.newWithBeta(0.5))
                .relativeNofFitsPerBatch(0.5)
                .build();
    }


    private static NormalizerMinMaxScaler createNormalizerIn(ParametersPole p) {
        List<Pair<Double, Double>> inMinMax = p.minMaxStatePairList();
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(-1d,1d));  //0,1 gives worse performance
    }

    private static NormalizerMinMaxScaler createNormalizerOut(ParametersPole p) {
        var outMinMax = List.of(Pair.create(0d, 10d));
        return Dl4JUtil.createNormalizer(outMinMax, Pair.create(0d,1d));
    }

}
