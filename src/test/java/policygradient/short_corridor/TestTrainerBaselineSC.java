package policygradient.short_corridor;

import common.MathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.short_corridor.AgentParamActorTabCriticSC;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;
import policy_gradient_problems.environments.short_corridor.StateSC;
import policy_gradient_problems.environments.short_corridor.TrainerBaselineSC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestTrainerBaselineSC {

    TrainerBaselineSC trainer;
    AgentParamActorTabCriticSC agent;

    @BeforeEach
     void init() {
        agent = AgentParamActorTabCriticSC.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentSC();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentSC environment) {
        trainer = TrainerBaselineSC.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(100).gamma(1d)
                        .build())
                .build();
    }

    @Test
    //@Disabled("takes long time")
     void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();
        agent.setState(StateSC.newFromRealPos(2));
        assertEquals(1, agent.chooseAction().asInt());
        assertTrue(MathUtils.isInRange(agent.chooseAction().asInt(),0,1));
        agent.setState(StateSC.newFromRealPos(6));
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
