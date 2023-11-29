package policygradient.sink_the_ship;

import common.MathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.sink_the_ship.AgentShip;
import policy_gradient_problems.sink_the_ship.EnvironmentShip;
import policy_gradient_problems.sink_the_ship.TrainerActorCriticShip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerActorCriticShip {

    TrainerActorCriticShip trainer;
    AgentShip agent;

    @BeforeEach
    public void init() {
        agent = AgentShip.newRandomStartStateDefaultThetas();
        var environment= new EnvironmentShip();
        createTrainer(environment);
    }

    private void createTrainer(EnvironmentShip environment) {
        trainer = TrainerActorCriticShip.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(100).gamma(0.99d).beta(0.1).learningRate(2e-2)
                        .build())
                .build();
    }

    @Test
    public void whenTrained_thenCorrectActionSelectionInEachState() {
        trainer.train();
        printPolicy();
        assertEquals(1, agent.chooseAction(0));
        assertTrue(MathUtils.isInRange(agent.chooseAction(1),0,1));
        assertEquals(0, agent.chooseAction(2));
        var meansStd0 = agent.getMeanAndStdFromThetaVector(0);
        var meansStd1 = agent.getMeanAndStdFromThetaVector(1);

        System.out.println("meansStd0 = " + meansStd0);
        System.out.println("meansStd1 = " + meansStd1);

        assertEquals(0.30, meansStd0.getFirst());
        assertEquals(0.60, meansStd1.getFirst());


        //assertTrue(valueFunction.getValue(1)>valueFunction.getValue(2));


    }

    private void printPolicy() {
        System.out.println("policy");
        for (int s: EnvironmentShip.STATES) {
            System.out.println("s = "+s+", agent{mean,std} = " + agent.getMeanAndStdFromThetaVector(s));
        }
    }

}
