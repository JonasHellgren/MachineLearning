package policygradient.abstract_classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.environments.twoArmedBandit.StateBandit;
import policy_gradient_problems.environments.twoArmedBandit.VariablesBandit;

public class TestExperience {

    Experience<VariablesBandit> experience;

    @BeforeEach
    public void init() {
        experience=Experience.<VariablesBandit>builder()
                .state(StateBandit.newDefault())
                .action(Action.ofInteger(0))
                .reward(0d)
                .stateNext(StateBandit.newDefault())
                .value(0d)
                .build();
    }

    @Test
    public void givenExperience_thenCorrect() {
        System.out.println("experience = " + experience);
        Assertions.assertEquals(0,experience.state().getVariables().arm());
        Assertions.assertEquals(0,experience.reward());
        Assertions.assertEquals(0,experience.stateNext().getVariables().arm());
        Assertions.assertEquals(0,experience.value());
    }

    @Test
    public void givenExperience_whenNewWithValue_thenCorrect() {
        experience=experience.copyWithValue(10);
        Assertions.assertEquals(10,experience.value());
    }

}
