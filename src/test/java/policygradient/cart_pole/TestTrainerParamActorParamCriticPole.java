package policygradient.cart_pole;

import common.MathUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestTrainerParamActorParamCriticPole {

    policy_gradient_problems.environments.cart_pole.TrainerParamActorParamCriticPole trainer;
    AgentParamActorPole agent;
    EnvironmentPole environment;
    ParametersPole parametersPole;

    @BeforeEach
    void init() {
        environment = EnvironmentPole.newDefault();
        agent = AgentParamActorPole.newRandomStartStateDefaultThetas(environment.getParameters());
        createTrainer(environment);
        trainer.train();
        parametersPole=ParametersPole.newDefault();
    }

    private void createTrainer(EnvironmentPole environment) {
        trainer = policy_gradient_problems.environments.cart_pole.TrainerParamActorParamCriticPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder().nofEpisodes(5000).gamma(0.9)
                        .build())
                .build();
    }

    @Test
    void whenTrained_thenManySteps() {
        PoleAgentOneEpisodeRunner helper = PoleAgentOneEpisodeRunner.newOf(environment,agent);

        int nofSteps = helper.runTrainedAgent(StatePole.newUprightAndStill(parametersPole));
        System.out.println("nofSteps = " + nofSteps);
        assertTrue(nofSteps > 50);
    }

    @Test
    @Disabled("long time")
    void whenTrained_thenCorrectValueFunction() {
        double valuea0x0 = trainer.getValueFunction().getValue(getFeatureVector(0, 0));
        double valuea0d3x0 = trainer.getValueFunction().getValue(getFeatureVector(0.3, 0));
        var wVector = trainer.getValueFunction().getWVector();

        System.out.println("wVector = " + wVector);
        System.out.println("valuea0x0 = " + valuea0x0);
        System.out.println("valuea0d5x0 = " + valuea0d3x0);

        assertTrue(valuea0x0 > valuea0d3x0);  //bad with large angle
        assertTrue(MathUtils.isNeg(wVector.getEntry(1)));  //bad with large angle
    }

    @NotNull
    private ArrayRealVector getFeatureVector(double angle, double x) {
        StateI<VariablesPole> statePole = StatePole.newFromVariables(
                VariablesPole.builder().angle(angle).x(x).build(),
                parametersPole);
        return policy_gradient_problems.environments.cart_pole.TrainerParamActorParamCriticPole.getFeatureVector(
                Experience.<VariablesPole>builder().state(statePole).build(),
                environment.getParameters()

        );
    }


}
