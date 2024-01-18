package policygradient.cart_pole;

import common.RandUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import policy_gradient_problems.the_problems.cart_pole.NeuralCriticMemoryPole;
import policy_gradient_problems.the_problems.cart_pole.ParametersPole;
import policy_gradient_problems.the_problems.cart_pole.StatePole;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TestNeuralCriticPole {

    public static final int NOF_SAMPLES = 10;
    NeuralCriticMemoryPole memory;
    ParametersPole parameters;

    @BeforeEach
    public void init() {
        memory = NeuralCriticMemoryPole.newDefault();
        parameters = ParametersPole.newDefault();
    }

    @SneakyThrows
    @Test
    @Disabled("takes long time")
    public void givenAbsAngleLargerThan0d1Gives10Else0RestStatesZero_whenTrained_thenCorrect() {
        Function<Double,StatePole> stateFcn=(a) -> copyWithAngle(StatePole.newUprightAndStill(),a);
        int nofEpochs = 200;
        var errors = trainNet(stateFcn, nofEpochs);
        plotLoss(errors);
        printAndAssert();
    }

    @SneakyThrows
    @Test
    @Disabled("takes long time")
    public void givenAbsAngleLargerThan0d1Gives10Else0RestStatesRandom_whenTrained_thenCorrect() {
        Function<Double,StatePole> stateFcn=(a) ->  copyWithAngle(StatePole.newAllRandom(parameters),a);
        int nofEpochs = 1000;
        var errors = trainNet(stateFcn, nofEpochs);
        plotLoss(errors);
        printAndAssert();
    }

    private void printAndAssert() {
        double outLargeAngle = memory.getOutValue(getStateForAngleRestZero(0.2).asList());
        double outSmallAngle = memory.getOutValue(getStateForAngleRestZero(0.0).asList());
        System.out.println("outLargeAngle = " + outLargeAngle);
        System.out.println("outSmallAngle = " + outSmallAngle);
        Assertions.assertTrue(outLargeAngle<outSmallAngle);
    }

    private List<Double> trainNet(Function<Double,StatePole> stateFcn, int nofEpochs) {
        List<Double> errors = new ArrayList<>();
        for (int i = 0; i < nofEpochs; i++) {
            List<List<Double>> in = new ArrayList<>();
            List<Double> out = new ArrayList<>();
            for (int j = 0; j < NOF_SAMPLES; j++) {
                double angle = RandUtils.getRandomDouble(-parameters.angleMax(), parameters.angleMax());
                var state = stateFcn.apply(angle);
                double value = (Math.abs(angle) > 0.1) ? 20 : 200d;
                in.add(state.asList());
                out.add(value);
            }
            int nofFits = (int) (0.5 * NOF_SAMPLES);
            memory.fit(in, out);
            errors.add(memory.getError());
        }
        return errors;
    }

    private StatePole copyWithAngle(StatePole state,double angle) {
        return StatePole.builder()
                .angle(angle).x(state.x()).angleDot(state.angleDot()).xDot(state.xDot()).nofSteps(state.nofSteps())
                .build();
    }

    private static StatePole getStateForAngleRestZero(double angle) {
        return StatePole.builder().angle(angle).x(0d).angleDot(0).xDot(0).build();
    }

    @SneakyThrows
    private static void plotLoss(List<Double> errors) {
        XYChart chart = QuickChart.getChart("Training error", "episode", "Error", "e(ep)", null, errors);
        new SwingWrapper<>(chart).displayChart();
        Thread.sleep(1500);
    }

}
