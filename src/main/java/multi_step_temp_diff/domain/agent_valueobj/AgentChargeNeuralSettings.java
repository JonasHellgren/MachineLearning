package multi_step_temp_diff.domain.agent_valueobj;

import lombok.Builder;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;

import static common.DefaultPredicates.defaultIfNullDouble;
import static common.DefaultPredicates.defaultIfNullInteger;

@Builder
public record AgentChargeNeuralSettings(
        Integer nofStates,
        Integer startState,
        Double minValue,
        Double maxValue,
        Double discountFactor,
        Double learningRate) {

    public static final int START_STATE = 0;
    public static final double MAX_VALUE = 0d;
    public static final double LEARNING_RATE = 0.1, DISCOUNT_FACTOR = 1d;

    public static  AgentChargeNeuralSettings newDefault() {
        return AgentChargeNeuralSettings.builder().build();
    }

    @Builder
    public AgentChargeNeuralSettings(Integer nofStates,
                                     Integer startState,
                                     Double minValue,
                                     Double maxValue,
                                     Double discountFactor,
                                     Double learningRate) {
        ChargeEnvironmentSettings envSettings=ChargeEnvironmentSettings.newDefault();

        this.nofStates = envSettings.siteNodes().size();
        this.startState = defaultIfNullInteger.apply(startState, START_STATE);
        this.minValue = defaultIfNullDouble.apply(minValue, envSettings.rewardBad());
        this.maxValue = defaultIfNullDouble.apply(maxValue, MAX_VALUE);
        this.discountFactor = defaultIfNullDouble.apply(discountFactor, DISCOUNT_FACTOR);
        this.learningRate = defaultIfNullDouble.apply(learningRate, LEARNING_RATE);
    }
}
