package safe_rl.environments.buying;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

class TestStateBuyingZero {

    StateBuying state;

    @BeforeEach
    void init() {
        state=StateBuying.newZero();
    }

    @Test
    void whenCopy_thenCorrect() {
        var stateCopy=state.copy();
        state.setVariables(VariablesBuying.newSoc(0.5));  //does not affect stateCopy
        Assertions.assertEquals(0.5,state.getVariables().soc());
        Assertions.assertEquals(0,stateCopy.getVariables().soc());
        Assertions.assertEquals(0,stateCopy.getVariables().time());
    }


    @Test
    void whenContFeat_thenCorrect() {
        var contFeat = state.continousFeatures();
        Assertions.assertEquals(0, contFeat[0]);
        Assertions.assertEquals(1, state.nContinousFeatures());
    }

    @Test
    void whenDiscFeat_thenCorrect() {
        var discFeat = state.discretFeatures();
        Assertions.assertEquals(0, discFeat[0]);
        Assertions.assertEquals(1, discFeat.length);
    }

    /**
     * equals and hashcode applies to discrete features only
     * The reasons is that they only discrete are used for hashmap lookup
     */

    @Test
    void givenModifiedSoc_whenEqual_thenCorrect() {
        var stateCopyNotModif=state.copy();
        state.setVariables(state.getVariables().withSoc(0.5));
        Assertions.assertEquals(state, stateCopyNotModif);
        Assertions.assertEquals(state.hashCode(), stateCopyNotModif.hashCode());
    }

    @Test
    void givenModifiedTime_whenEqual_thenCorrect() {
        var stateCopyNotModif=state.copy();
        state.setVariables(state.getVariables().withTime(2));
        Assertions.assertNotEquals(state, stateCopyNotModif);
        Assertions.assertNotEquals(state.hashCode(), stateCopyNotModif.hashCode());
    }



}
