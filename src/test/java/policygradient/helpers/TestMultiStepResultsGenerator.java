package policygradient.helpers;

import common.list_arrays.ListUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.AgentActorICriticPoleCEM;
import policy_gradient_problems.environments.cart_pole.ParametersPole;
import policy_gradient_problems.environments.cart_pole.StatePole;
import policy_gradient_problems.environments.cart_pole.VariablesPole;
import policy_gradient_problems.helpers.MultiStepResultsGenerator;

import java.util.ArrayList;
import java.util.List;

public class TestMultiStepResultsGenerator {

    public static final int INT_ACTION = 0;
    public static final int REWARD = 0;
    public static final int STEP_HORIZON = 3;
    public static final int PROB_ACTION = 1;
    ParametersPole parametersPole;
    MultiStepResultsGenerator<VariablesPole> generator;
    List<Experience<VariablesPole>> experiences;

    @BeforeEach
    void init() {
        parametersPole = ParametersPole.newDefault();
        var agent = AgentActorICriticPoleCEM.newDefault(
                StatePole.newUprightAndStill(parametersPole));
        TrainerParameters parameters = TrainerParameters.newDefault().withGamma(0d).withStepHorizon(STEP_HORIZON);
        generator=new MultiStepResultsGenerator<>(parameters,agent);
        experiences=new ArrayList<>();
        StatePole stateUp = StatePole.newUprightAndStill(parametersPole);
        StatePole stateRandom = StatePole.newAllRandom(parametersPole);

        experiences.add(Experience.of(stateUp, Action.ofInteger(INT_ACTION), REWARD,stateUp));
        experiences.add(Experience.of(stateUp, Action.ofInteger(INT_ACTION),REWARD,stateUp));
        experiences.add(Experience.of(stateRandom, Action.ofInteger(INT_ACTION), REWARD,stateUp));
        experiences.add(Experience.of(stateUp, Action.ofInteger(INT_ACTION), REWARD,stateUp));
        experiences.add(Experience.of(stateRandom, Action.ofInteger(INT_ACTION), REWARD,stateUp));

    }

    @Test
    void givenEndIsNotFail_whenGenerating_thenCorrect() {
        var msr=generator.generate(experiences);
        int nofExp = 5;
        Assertions.assertEquals(nofExp,msr.nofSteps());
        Assertions.assertEquals(nofExp-STEP_HORIZON+1,msr.tEnd());  //described in class why
        Assertions.assertEquals(0, ListUtils.sumList(msr.valueTarList()));  //only sum rewards when gamma is zero
        Assertions.assertNotEquals(0, ListUtils.sumList(msr.valueCriticList()));
    }

    @Test
    void givenEndIsFail_whenGenerating_thenCorrect() {
        StatePole stateFail = StatePole.newAllRandom(parametersPole).copyWithAngle(1d);
        experiences.add(Experience.ofWithIsFail(
                stateFail, Action.ofInteger(INT_ACTION), REWARD,stateFail, PROB_ACTION,true));
        var msr=generator.generate(experiences);
        int nofExp = 6;
        Assertions.assertEquals(nofExp,msr.nofSteps());
        Assertions.assertEquals(nofExp,msr.tEnd());  //described in class why
    }


}
