package safe_rl.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;
import safe_rl.domain.value_classes.*;
import safe_rl.domain.abstract_classes.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestExperience {


    Experience<VariablesBuying> experience;

    @BeforeEach
     void init() {
        experience=Experience.<VariablesBuying>builder()
                .state(StateBuying.newDefault())
                .action(Action.ofInteger(0))
                .reward(0d)
                .stateNext(StateBuying.newDefault())
                .value(0d)
                .experienceSafe(Optional.empty())
                .build();
    }

    @Test
     void givenExperience_thenCorrect() {
        System.out.println("experience = " + experience);
        assertEquals(0,experience.state().getVariables().time());
        assertEquals(0d,experience.reward());
        assertEquals(0,experience.stateNext().getVariables().time());
        assertEquals(0,experience.value());
    }

    @Test
     void givenExperience_whenNewWithValue_thenCorrect() {
        experience=experience.copyWithValue(10);
        assertEquals(10,experience.value());
    }

    @Test
    void givenExperienceWithSafeCorrected_thenCorrect() {
        experience=Experience.ofWithIsSafeCorrected(StateBuying.newDefault(),Action.ofInteger(0),0.1,
                ExperienceSafe.<VariablesBuying>builder()
                        .action(Action.ofInteger(1))
                        .reward(0.5).stateNext(StateBuying.newDefault()).probAction(0.5).isTerminal(false)
                        .build());
        assertTrue(experience.isSafeCorrected());
        assertEquals(1d,experience.experienceSafe().orElseThrow().action().asInt());
    }


}
