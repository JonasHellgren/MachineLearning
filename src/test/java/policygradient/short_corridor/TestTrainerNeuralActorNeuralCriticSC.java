package policygradient.short_corridor;

import common.MathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.short_corridor.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                        .nofEpisodes(1000).nofStepsMax(100).gamma(0.5)
                        .build())
                .build();
    }

    @Test
    public void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();

        assertTrue(getCriticOutValue(1) > getCriticOutValue(0));
        assertTrue(getCriticOutValue(1) > getCriticOutValue(2));

        setRealPos(2);
        assertEquals(1, agent.chooseAction().asInt());
        assertTrue(MathUtils.isInRange(agent.chooseAction().asInt(), 0, 1));
        setRealPos(6);
        assertEquals(0, agent.chooseAction().asInt());
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
