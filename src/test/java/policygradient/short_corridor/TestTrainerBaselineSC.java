package policygradient.short_corridor;

import common.MathUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.short_corridor.AgentSC;
import policy_gradient_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.short_corridor.TrainerBaselineSC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerBaselineSC {

    TrainerBaselineSC trainer;
    AgentSC agent;

    @BeforeEach
    public void init() {
        agent = AgentSC.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentSC();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentSC environment) {
        trainer = TrainerBaselineSC.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(100).gamma(1d).beta(0.01).learningRate(2e-2)
                        .build())
                .build();
    }

    @Test
    public void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();
        assertEquals(1, agent.chooseAction(0));
        assertTrue(MathUtils.isInRange(agent.chooseAction(1),0,1));
        assertEquals(0, agent.chooseAction(2));
        var wVector = trainer.getValueFunction();
        assertTrue(wVector.getValue(1)>wVector.getValue(0));
        assertTrue(wVector.getValue(1)>wVector.getValue(2));
    }

    private void printPolicy() {
        System.out.println("policy");
        for (int s = 0; s < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES ; s++) {
            System.out.println("s = "+s+", agent.actionProb() = " + agent.calcActionProbabilitiesInState(s));
        }
    }

}