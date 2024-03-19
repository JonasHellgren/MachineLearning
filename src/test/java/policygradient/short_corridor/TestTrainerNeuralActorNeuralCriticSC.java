package policygradient.short_corridor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.short_corridor.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestTrainerNeuralActorNeuralCriticSC {

    TrainerNeuralActorNeuralCriticSC trainer;
    static AgentNeuralActorNeuralCriticSC agent;

    @BeforeEach
    public void init() {
        var environment = new EnvironmentSC();
        agent = AgentNeuralActorNeuralCriticSC.newDefault();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentSC environment) {
        trainer = TrainerNeuralActorNeuralCriticSC.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).gamma(0.5)
                        .build())
                .build();
    }

    @Test
    //@Disabled("long time")
    public void whenTrained_thenCorrectActionSelectionInEachState() {
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
        return agent.getActionProbabilities().get(0) < agent.getActionProbabilities().get(1);
    }

    private static Double getCriticOutValue(int pos) {
        var critic = agent.getCritic();
        return critic.getOutValue(StateSC.newFromPos(pos));
    }


    private void setRealPos(int pos) {
        agent.setState(StateSC.newFromPos(pos));
    }


    private void printPolicy() {
        System.out.println("policy");
        for (int pos = 0; pos < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES; pos++) {
            System.out.println("s = " + pos +
                    ", agent.actionProb() = " + agent.calcActionProbabilitiesInObsState(pos) +
                    ", value = " + getCriticOutValue(pos));
        }
    }


}
