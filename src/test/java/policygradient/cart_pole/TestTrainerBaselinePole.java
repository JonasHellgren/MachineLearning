package policygradient.cart_pole;

import common.MathUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTrainerBaselinePole {

    TrainerBaselinePole trainer;
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
        trainer = TrainerBaselinePole.builder()
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
        return TrainerBaselinePole.getFeatureVector(
                ExperiencePole.builder().state(StatePole.builder()
                        .angle(angle).x(x)
                        .build()).build(),
                environment.getParameters()

        );
    }


}