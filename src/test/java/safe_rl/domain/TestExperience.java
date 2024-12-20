package safe_rl.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestExperience {


    Experience<VariablesBuying> experience;

    @BeforeEach
     void init() {
        experience= Experience.notSafeCorrected(
                StateBuying.newZero(), Action.ofInteger(0),0d,StateBuying.newZero()
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
                StateBuying.newZero(),Action.ofInteger(0),Action.ofInteger(1),
                0d,StateBuying.newZero()
                ,false);

        assertTrue(experience.isSafeCorrected());
        assertEquals(1d,experience.arsCorrected().orElseThrow().action().asInt());
    }


}
