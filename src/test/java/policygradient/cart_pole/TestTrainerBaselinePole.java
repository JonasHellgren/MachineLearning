package policygradient.cart_pole;

import common.MathUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerBaselinePole {

    TrainerBaselinePole trainer;
    AgentParamActorPole agent;
    EnvironmentPole environment;

    @BeforeEach
    public void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentParamActorPole.newRandomStartStateDefaultThetas(environment.getParameters());
        createTrainer(environment);
        trainer.train();
    }

    private void createTrainer(EnvironmentPole environment) {
        trainer = TrainerBaselinePole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder().nofEpisodes(5000).gamma(0.9).learningRateCritic(1e-2).build())
                .build();
    }

    @Test
    public void whenTrained_thenManySteps() {
        PoleAgentOneEpisodeRunner helper = PoleAgentOneEpisodeRunner.builder().environment(environment).agent(agent).build();
        int nofSteps = helper.runTrainedAgent(StatePole.newUprightAndStill());
        System.out.println("nofSteps = " + nofSteps);
        assertTrue(nofSteps > 50);
    }

    @Test
    public void whenTrained_thenCorrectValueFunction() {
        double valuea0x0=trainer.getValueFunction().getValue(getFeatureVector(0, 0));
        double valuea0d3x0=trainer.getValueFunction().getValue(getFeatureVector(0.3, 0));
        var wVector=trainer.getValueFunction().getWVector();

        System.out.println("wVector = " + wVector);
        System.out.println("valuea0x0 = " + valuea0x0);
        System.out.println("valuea0d5x0 = " + valuea0d3x0);

        assertTrue(valuea0x0>valuea0d3x0);  //bad with large angle
        assertTrue(MathUtils.isNeg(wVector.getEntry(1)));  //bad with large angle
    }

    @NotNull
    private  ArrayRealVector getFeatureVector(double angle, double x) {
        StateI<VariablesPole> statePole = StatePole.newFromVariables(VariablesPole.builder()
                .angle(angle).x(x)
                .build());
        return TrainerBaselinePole.getFeatureVector(
                Experience.<VariablesPole>builder().state(statePole).build(),
                environment.getParameters()

        );
    }


}
