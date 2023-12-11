package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerActorCriticPole {


    TrainerActorCriticPole trainer;
    AgentPole agent;
    EnvironmentPole environment;

    @BeforeEach
    public void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentPole.newRandomStartStateDefaultThetas(environment.getParameters());
        createTrainer(environment);
        trainer.train();
    }

    private void createTrainer(EnvironmentPole environment) {
        trainer = TrainerActorCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder().nofEpisodes(5000).gamma(0.7).beta(1e-3).build())
                .build();
    }

    @Test
    public void whenTrained_thenManySteps() {
        PoleHelper helper = PoleHelper.builder().environment(environment).agent(agent).build();
        int nofSteps = helper.runTrainedAgent(StatePole.newUprightAndStill());
        assertTrue(nofSteps > 50);
    }


}
