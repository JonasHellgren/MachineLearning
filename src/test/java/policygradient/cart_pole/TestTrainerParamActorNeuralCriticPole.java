package policygradient.cart_pole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorNeuralCriticI;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;


/***
 * Does not converge
 */

 class TestTrainerParamActorNeuralCriticPole {

    TrainerParamActorNeuralCriticPole trainer;
    AgentParamActorNeuralCriticI<VariablesPole> agent;
    EnvironmentPole environment;

    @BeforeEach
     void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentParamActorNeuralCriticPole.newDefaultCritic(StatePole.newUprightAndStill());
        createTrainer(environment, agent);
        trainer.train();
    }

    private void createTrainer(EnvironmentPole environment, AgentParamActorNeuralCriticI<VariablesPole> agent) {
        trainer = TrainerParamActorNeuralCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(300).nofStepsMax(100).gamma(0.99)
                        .stepHorizon(10)
                        //.relativeNofFitsPerBatch(0.5)
                        .build())
                .build();
    }

    @Test
    @Disabled("long time")
     void whenTrained_thenManySteps() {
        PoleAgentOneEpisodeRunner helper = PoleAgentOneEpisodeRunner.builder().environment(environment).agent(agent).build();
        int nofSteps = helper.runTrainedAgent(StatePole.newUprightAndStill());

        System.out.println("nofSteps = " + nofSteps);

        double valAll0=agent.getCriticOut(StatePole.newUprightAndStill());
        double valBigAngle=agent.getCriticOut(StatePole.builder().angle(0.2).build());

        System.out.println("valAll0 = " + valAll0);
        System.out.println("valBigAngle = " + valBigAngle);

        for (int i = 0; i < 10 ; i++) {
            StatePole statePole = StatePole.newAllRandom(environment.getParameters());
            double valAllRandom=agent.getCriticOut(statePole);
            System.out.println("state = "+statePole+", valAllRandom = " + valAllRandom);
        }

        assertTrue(nofSteps > 20);

    }


}
