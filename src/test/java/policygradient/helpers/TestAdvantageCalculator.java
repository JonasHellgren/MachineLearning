package policygradient.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.ParametersPole;
import policy_gradient_problems.environments.cart_pole.StatePole;
import policy_gradient_problems.environments.cart_pole.VariablesPole;
import policy_gradient_problems.helpers.AdvantageCalculator;

import java.util.Optional;
import java.util.function.Function;

class TestAdvantageCalculator {

    public static final StatePole STATE = StatePole.newUprightAndStill(ParametersPole.newDefault());
    public static final double VALUE = 1d;
    public static final double GAMMA = 0.5;
    AdvantageCalculator<VariablesPole> advantageCalculator;

    Function<StateI<VariablesPole>, Double> criticOut = state -> {
        if (state == null) {
            return Double.NaN; // Returning NaN as an indicator of an invalid state.
        }
        return VALUE;
    };

    @BeforeEach
    void init() {
        var trainerParams = TrainerParameters.newDefault().withGamma(GAMMA);
        advantageCalculator = new AdvantageCalculator<>(trainerParams, criticOut);
    }

    @Test
    void whenFailInExp_thenCorrect() {
        int reward = 0;
        Experience<VariablesPole> exp = Experience.<VariablesPole>builder()
                .isFail(true).state(STATE).action(Action.ofInteger(1)).reward(reward)
                .build();
        Assertions.assertEquals(reward, advantageCalculator.calcAdvantage(exp));
    }

    @Test
    void whenNotFailInExp_thenCorrect() {
        int reward = 0;
        Experience<VariablesPole> exp = Experience.<VariablesPole>builder()
                .isFail(false).state(STATE).action(Action.ofInteger(1)).stateNext(STATE).reward(reward)
                .build();
        Assertions.assertNotEquals(reward, advantageCalculator.calcAdvantage(exp));
    }

}
