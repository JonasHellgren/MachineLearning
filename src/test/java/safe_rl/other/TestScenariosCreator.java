package safe_rl.other;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.other.scenario_creator.ScenarioParameterVariantsFactory;
import safe_rl.other.scenario_creator.ScenariosCreator;

class TestScenariosCreator {

    ScenariosCreator creator;

    @BeforeEach
    void init() {
        creator=new ScenariosCreator(ScenarioParameterVariantsFactory.create());
    }

    @Test
    void whenScenarios2EachParameter_thenCorrectNofCombos() {
        var setOfScenParams=creator.scenarios();
        int nParams = 5;
        int nValuesEachParam = 2;
        Assertions.assertEquals(Math.pow(nValuesEachParam, nParams),setOfScenParams.size());
    }

}
