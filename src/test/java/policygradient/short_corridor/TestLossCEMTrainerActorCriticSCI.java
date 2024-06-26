package policygradient.short_corridor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.short_corridor.*;

import static org.junit.jupiter.api.Assertions.*;

 class TestLossCEMTrainerActorCriticSCI {

    TrainerActorCriticSCLossCEM trainer;
    static AgentActorICriticSCLossCEM agent;

    @BeforeEach
     void init() {
        var environment = new EnvironmentSC();
        agent = AgentActorICriticSCLossCEM.newDefault();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentSC environment) {
        trainer = TrainerActorCriticSCLossCEM.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).gamma(0.5)
                        .build())
                .build();
    }

    @Test
    //@Disabled("long time")
     void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();

        assertTrue(getCriticOutValue(1) > getCriticOutValue(0));
        assertTrue(getCriticOutValue(1) > getCriticOutValue(2));

        setRealPos(2);
        assertTrue(isProbMovingRightLarger());
        setRealPos(6);
        assertFalse(isProbMovingRightLarger());

    }

    private static boolean isProbMovingRightLarger() {
        return agent.actionProbabilitiesInPresentState().get(0) < agent.actionProbabilitiesInPresentState().get(1);
    }

    private static Double getCriticOutValue(int pos) {
        var critic = agent.getCritic();
        return critic.getOutValue(StateSC.newFromObsPos(pos));
    }


    private void setRealPos(int pos) {
        agent.setState(StateSC.newFromRealPos(pos));
    }


    private void printPolicy() {
        System.out.println("policy");
        for (int pos = 0; pos < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES; pos++) {
            System.out.println("s = " + pos +
                    ", agent.actionProb() = " + agent.actorOut(StateSC.newFromObsPos(pos)) +
                    ", value = " + getCriticOutValue(pos));
        }
    }


}
