package policygradient.short_corridor;

import common.MathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.short_corridor.*;

import static org.junit.jupiter.api.Assertions.*;

 class TestTrainerParamActorSC {

    TrainerParamActorSC trainer;
    AgentParamActorSC agent;

    @BeforeEach
     void init() {
        agent = AgentParamActorSC.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentSC();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentSC environment) {
        trainer = TrainerParamActorSC.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(100).gamma(1d).learningRateNonNeuralActor(2e-2)
                        .build())
                .build();
    }

    @Test
     void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();
        setRealPos(2);
        assertEquals(1, agent.chooseAction().asInt());
        setRealPos(3);
        assertTrue(MathUtils.isInRange(agent.chooseAction().asInt(),0,1));
        setRealPos(6);
        assertEquals(0, agent.chooseAction().asInt());

    }

    private void setRealPos(int pos) {
        agent.setState(StateSC.newFromRealPos(pos));
    }

    private void printPolicy() {
        System.out.println("policy");
        for (int s = 0; s < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES ; s++) {
            System.out.println("s = "+s+", agent.actionProb() = " + agent.getHelper().calcActionProbsInObsState(s));
        }
    }

}
