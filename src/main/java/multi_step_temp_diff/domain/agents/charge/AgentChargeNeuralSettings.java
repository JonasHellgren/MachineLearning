package multi_step_temp_diff.domain.agents.charge;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentSettingsInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerInterface;
import org.neuroph.util.TransferFunctionType;

import static common.other.MyFunctions.defaultIfNullDouble;
import static common.other.MyFunctions.defaultIfNullInteger;

@Builder
public record AgentChargeNeuralSettings (
        Integer nofStates,
        Integer startState,
        Double minValue,
        Double maxValue,
        Double discountFactor,
        Double learningRate,
        Double momentum,
        Integer nofNeuronsHidden,
        Integer nofLayersHidden,
        TransferFunctionType transferFunctionType,
        @NonNull NormalizerInterface valueNormalizer) implements AgentSettingsInterface {

    public static final int START_STATE = 0;
    public static final double MAX_VALUE = 0d;
    public static final double LEARNING_RATE = 0.1, DISCOUNT_FACTOR = 1d;
    public static final double MOMENTUM = 0.25;

    public static  AgentChargeNeuralSettings newDefault() {
        return AgentChargeNeuralSettings.builder()
                .transferFunctionType(TransferFunctionType.GAUSSIAN)
                .valueNormalizer(new NormalizeMinMax(-100,1)).build();
    }

    @Builder
    public AgentChargeNeuralSettings(Integer nofStates,
                                     Integer startState,
                                     Double minValue,
                                     Double maxValue,
                                     Double discountFactor,
                                     Double learningRate,
                                     Double momentum,
                                     Integer nofNeuronsHidden,
                                     Integer nofLayersHidden,
                                     @NonNull TransferFunctionType transferFunctionType,
                                     @NonNull NormalizerInterface valueNormalizer) {
        ChargeEnvironmentSettings envSettings=ChargeEnvironmentSettings.newDefault();

        this.nofStates = envSettings.siteNodes().size();
        this.startState = defaultIfNullInteger.apply(startState, START_STATE);
        this.minValue = defaultIfNullDouble.apply(minValue, envSettings.rewardBad());
        this.maxValue = defaultIfNullDouble.apply(maxValue, MAX_VALUE);
        this.discountFactor = defaultIfNullDouble.apply(discountFactor, DISCOUNT_FACTOR);
        this.learningRate = defaultIfNullDouble.apply(learningRate, LEARNING_RATE);
        this.momentum = defaultIfNullDouble.apply(momentum, MOMENTUM);
        this.nofNeuronsHidden = defaultIfNullInteger.apply(nofNeuronsHidden, nofStates);
        this.nofLayersHidden = defaultIfNullInteger.apply(nofLayersHidden, 1);
        this.transferFunctionType=transferFunctionType;
        this.valueNormalizer = valueNormalizer;
    }
}
