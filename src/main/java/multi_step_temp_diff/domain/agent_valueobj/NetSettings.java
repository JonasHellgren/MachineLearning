package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.normalizer.NormalizerInterface;
import org.neuroph.util.TransferFunctionType;

import static common.DefaultPredicates.*;

@Builder
public record NetSettings (
        Integer outPutSize,
        Integer inputSize,
        Integer nofNeuronsHidden,
        Integer nofHiddenLayers,
        TransferFunctionType transferFunctionType,
        Double minOut,
        Double maxOut,
        Double netOutMin,
        Double netOutMax,
        Double learningRate,
        Double momentum,
        @NonNull NormalizerInterface normalizer
)
{
    public static final Integer OUTPUT_SIZE = 1;
    public static final Double OUT_MIN = 0d;
    public static final Double OUT_MAX = 1d;
    public static final Double LEARNING_RATE = 0.1;
    public static final double MOMENTUM = 0.25;

    @Builder
    public NetSettings(Integer outPutSize,
                       @NonNull  Integer inputSize,
                       @NonNull  Integer nofNeuronsHidden,
                       Integer nofHiddenLayers,
                       @NonNull TransferFunctionType transferFunctionType,
                       @NonNull  Double minOut,
                       @NonNull  Double maxOut,
                       Double netOutMin,
                       Double netOutMax,
                       Double learningRate,
                       Double momentum,
                       NormalizerInterface normalizer) {
        this.outPutSize = defaultIfNullInteger.apply(outPutSize,OUTPUT_SIZE);
        this.inputSize = inputSize;
        this.nofNeuronsHidden = nofNeuronsHidden;
        this.nofHiddenLayers = defaultIfNullInteger.apply(nofHiddenLayers,1);
        this.transferFunctionType=transferFunctionType;
        this.minOut = minOut;
        this.maxOut = maxOut;
        this.netOutMin = defaultIfNullDouble.apply(netOutMin,OUT_MIN);
        this.netOutMax = defaultIfNullDouble.apply(netOutMax,OUT_MAX);
        this.learningRate = defaultIfNullDouble.apply(learningRate,LEARNING_RATE);
        this.momentum = defaultIfNullDouble.apply(momentum, MOMENTUM);

        this.normalizer = normalizer;
    }

}
