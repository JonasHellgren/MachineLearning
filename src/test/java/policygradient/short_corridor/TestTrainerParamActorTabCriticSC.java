package policygradient.short_corridor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.short_corridor.AgentParamActorTabCriticSC;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;
import policy_gradient_problems.environments.short_corridor.StateSC;
import policy_gradient_problems.environments.short_corridor.TrainerParamActorTabCriticSC;

import static org.junit.jupiter.api.Assertions.*;

 class TestTrainerParamActorTabCriticSC {

    TrainerParamActorTabCriticSC trainer;
    static AgentParamActorTabCriticSC agent;

    @BeforeEach
     void init() {
        agent = AgentParamActorTabCriticSC.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentSC();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentSC environment) {
        trainer = TrainerParamActorTabCriticSC.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(15_000).nofStepsMax(100).gamma(0.99)
                        .learningRateCritic(1e-2).learningRateActor(1e-3)
                        .build())
                .build();
    }

    @Test
     void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();

        setRealPos(2);
        assertTrue(isProbMovingRightLarger());
        setRealPos(6);
        assertFalse(isProbMovingRightLarger());

        assertTrue(getCriticOutValue(1) >getCriticOutValue(0));
        assertTrue(getCriticOutValue(1)>getCriticOutValue(2));
    }

    private static double getCriticOutValue(int state) {
        var valueFunction = agent.getCritic();
        return valueFunction.getValue(state);
    }

    private static boolean isProbMovingRightLarger() {
        return agent.getActionProbabilities().get(0) < agent.getActionProbabilities().get(1);
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
