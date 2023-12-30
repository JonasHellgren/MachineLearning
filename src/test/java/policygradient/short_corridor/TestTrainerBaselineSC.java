package policygradient.short_corridor;

import common.MathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.short_corridor.AgentSC;
import policy_gradient_problems.the_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.the_problems.short_corridor.StateSC;
import policy_gradient_problems.the_problems.short_corridor.TrainerBaselineSC;

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
                        .nofEpisodes(1000).nofStepsMax(100).gamma(1d)
                        .learningRateCritic(0.01).learningRateActor(2e-2)
                        .build())
                .build();
    }

    @Test
    @Disabled("takes long time")
    public void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();
        agent.setState(StateSC.newFromPos(1));  //todo correct real pos
        assertEquals(1, agent.chooseAction().asInt());
        assertTrue(MathUtils.isInRange(agent.chooseAction().asInt(),0,1));
        agent.setState(StateSC.newFromPos(2));  //todo correct real pos
        assertEquals(0, agent.chooseAction().asInt());
        var wVector = agent.getCritic();
        assertTrue(wVector.getValue(1)>wVector.getValue(0));
        assertTrue(wVector.getValue(1)>wVector.getValue(2));
    }

    private void printPolicy() {
        System.out.println("policy");
        for (int s = 0; s < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES ; s++) {
            System.out.println("s = "+s+", agent.actionProb() = " + agent.getHelper().calcActionProbsInObsState(s));
        }
    }

}
