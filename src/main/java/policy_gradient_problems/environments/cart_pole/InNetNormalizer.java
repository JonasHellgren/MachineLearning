package policy_gradient_problems.environments.cart_pole;

import common_dl4j.Dl4JUtil;
import org.apache.commons.math3.util.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

public class InNetNormalizer {

    private InNetNormalizer() {
    }

    static final int N_INPUTS = StatePole.newUprightAndStill(ParametersPole.newDefault()).nofStates();

    public static NormalizerMinMaxScaler createNormalizerIn(ParametersPole p) {
        List<Pair<Double, Double>> inMinMax = p.minMaxStatePairList();
        return Dl4JUtil.createNormalizer(inMinMax, Pair.create(-1d, 1d));  //0,1 gives worse performance
    }


    public static INDArray getInAsNormalized(List<Double> in, NormalizerMinMaxScaler normalizerIn1) {
        INDArray indArray = Nd4j.create(in);
        normalizerIn1.transform(indArray);
        indArray = indArray.reshape(1, N_INPUTS);
        return indArray;
    }


}
