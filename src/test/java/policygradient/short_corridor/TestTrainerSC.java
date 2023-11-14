package policygradient.short_corridor;

import common.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.short_corridor.AgentSC;
import policy_gradient_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.short_corridor.TrainerSC;

public class TestTrainerSC {

    TrainerSC trainer;
    AgentSC agent;

    @BeforeEach
    public void init() {
        agent = AgentSC.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentSC();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentSC environment) {
        trainer = TrainerSC.builder()
                .environment(environment)
                .agent(agent)
                .nofEpisodes(1000).nofStepsMax(10).gamma(1d).learningRate(1e-1)
                .build();
    }

    @Test
    public void givenEnvActionOneIsWellRewarded_whenTrained_thenCorrect() {
        trainer.train();
        printPolicy();
        int actionState0 = agent.chooseAction(0);
        int actionState1 = agent.chooseAction(1);
        int actionState2 = agent.chooseAction(2);

        Assertions.assertEquals(1, actionState0);
        Assertions.assertTrue(MathUtils.isInRange(actionState1,0,1));
        Assertions.assertEquals(0, actionState2);

    }

    private void printPolicy() {
        for (int s = 0; s < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES ; s++) {
            System.out.println("s = "+s+", agent.piTheta() = " + agent.getActionProbabilitiesInState(s));
        }
    }

}
