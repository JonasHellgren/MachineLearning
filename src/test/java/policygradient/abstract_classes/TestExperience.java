package policygradient.abstract_classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.environments.twoArmedBandit.StateBandit;
import policy_gradient_problems.environments.twoArmedBandit.VariablesBandit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestExperience {

    Experience<VariablesBandit> experience;

    @BeforeEach
     void init() {
        experience=Experience.<VariablesBandit>builder()
                .state(StateBandit.newDefault())
                .action(Action.ofInteger(0))
                .reward(0d)
                .stateNext(StateBandit.newDefault())
                .value(0d)
                .build();
    }

    @Test
     void givenExperience_thenCorrect() {
        System.out.println("experience = " + experience);
        assertEquals(0,experience.state().getVariables().arm());
        assertEquals(0d,experience.reward());
        assertEquals(0,experience.stateNext().getVariables().arm());
        assertEquals(0,experience.value());
    }

    @Test
     void givenExperience_whenNewWithValue_thenCorrect() {
        experience=experience.copyWithValue(10);
        assertEquals(10,experience.value());
    }

}
