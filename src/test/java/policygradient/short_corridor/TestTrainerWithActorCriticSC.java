package policygradient.short_corridor;

import common.MathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.short_corridor.AgentParamActorTabCriticSC;
import policy_gradient_problems.the_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.the_problems.short_corridor.StateSC;
import policy_gradient_problems.the_problems.short_corridor.TrainerActorCriticSC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerWithActorCriticSC {

    TrainerActorCriticSC trainer;
    AgentParamActorTabCriticSC agent;

    @BeforeEach
    public void init() {
        agent = AgentParamActorTabCriticSC.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentSC();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentSC environment) {
        trainer = TrainerActorCriticSC.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(15_000).nofStepsMax(100).gamma(0.99)
                        .learningRateCritic(1e-2).learningRateActor(1e-3)
                        .build())
                .build();
    }

    @Test
    public void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();
        setRealPos(2);
        assertEquals(1, agent.chooseAction().asInt());
        assertTrue(MathUtils.isInRange(agent.chooseAction().asInt(),0,1));
        setRealPos(6);
        assertEquals(0, agent.chooseAction().asInt());
        var valueFunction = agent.getCritic();
        assertTrue(valueFunction.getValue(1)>valueFunction.getValue(0));
        assertTrue(valueFunction.getValue(1)>valueFunction.getValue(2));
    }


    private void setRealPos(int pos) {
        agent.setState(StateSC.newFromPos(pos));
    }


    private void printPolicy() {
        System.out.println("policy");
        for (int s = 0; s < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES ; s++) {
            System.out.println("s = "+s+", agent.actionProb() = " + agent.getHelper().calcActionProbsInObsState(s));
        }
    }

}
