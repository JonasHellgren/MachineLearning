package safe_rl.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;
import safe_rl.domain.value_classes.*;
import safe_rl.domain.abstract_classes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestExperience {


    Experience<VariablesBuying> experience;

    @BeforeEach
     void init() {
        experience=Experience.notSafeCorrected(
                StateBuying.newDefault(),Action.ofInteger(0),0d,StateBuying.newDefault()
                ,false);
    }

    @Test
     void givenExperience_thenCorrect() {
        System.out.println("experience = " + experience);
        assertEquals(0,experience.state().getVariables().time());
        assertEquals(0d,experience.ars().reward());
        assertEquals(0,experience.ars().stateNext().getVariables().time());
        assertEquals(0,experience.value());
    }

    @Test
     void givenExperience_whenNewWithValue_thenCorrect() {
        experience=experience.copyWithValue(10);
        assertEquals(10,experience.value());
    }

    @Test
    void givenExperienceWithSafeCorrected_thenCorrect() {
        experience=Experience.safeCorrected(
                StateBuying.newDefault(),Action.ofInteger(0),Action.ofInteger(1),
                0d,StateBuying.newDefault()
                ,false);

        assertTrue(experience.isSafeCorrected());
        assertEquals(1d,experience.arsCorrected().orElseThrow().action().asInt());
    }


}
