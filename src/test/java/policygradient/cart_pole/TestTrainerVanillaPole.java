package policygradient.cart_pole;

import common.Counter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;

public class TestTrainerVanillaPole {

    TrainerVanillaPole trainer;
    AgentPole agent;
    EnvironmentPole environment;

    @BeforeEach
    public void init() {
        environment= EnvironmentPole.newDefault();
        agent = AgentPole.newRandomStartStateDefaultThetas(environment.getParameters());
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentPole environment) {
        trainer = TrainerVanillaPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(2000).nofStepsMax(100).gamma(0.99).learningRate(2e-3)
                        .build())
                .build();
    }

    @Test
    public void whenTrained_thenManySteps() {
        trainer.train();
        System.out.println("agent.getThetaVector() = " + agent.getThetaVector());

        int nofSteps= runTrainedAgent(StatePole.newUprightAndStill());
        System.out.println("nofSteps = " + nofSteps);

        Assertions.assertTrue(nofSteps>50);

    }

    private int runTrainedAgent(StatePole stateStart) {
        agent.setState(stateStart);
        Counter counter=new Counter();
        StepReturnPole stepReturn;
        do {
            stepReturn = environment.step(agent.chooseAction(), agent.getState());
            agent.setState(stepReturn.newState().copy());
            counter.increase();
        } while (!stepReturn.isTerminal() );
        return counter.getCount();
    }


}
