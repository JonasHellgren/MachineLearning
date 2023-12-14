package policygradient.cart_pole;

import common.RandUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import policy_gradient_problems.the_problems.cart_pole.NeuralMemoryPole;
import policy_gradient_problems.the_problems.cart_pole.ParametersPole;
import policy_gradient_problems.the_problems.cart_pole.StatePole;
import java.util.ArrayList;
import java.util.List;

public class TestNeuralValueFunctionPole {

    public static final int NOF_EPOCHS = 100;
    public static final int NOF_SAMPLES = 10;
    NeuralMemoryPole memory;
    ParametersPole parameters;

    @BeforeEach
    public void init() {
        memory = NeuralMemoryPole.newDefault();
        parameters = ParametersPole.newDefault();
    }

    @SneakyThrows
    @Test
    public void givenAbsAngleLargerThan0d1Gives10Else0_whenTrained_thenCorrect() {

        List<Double> errors = new ArrayList<>();

        for (int i = 0; i < NOF_EPOCHS; i++) {

            List<List<Double>> in = new ArrayList<>();
            List<Double> out = new ArrayList<>();

            for (int j = 0; j < NOF_SAMPLES; j++) {
                double angle = RandUtils.getRandomDouble(-parameters.angleMax(), parameters.angleMax());
                var state = StatePole.builder().angle(angle).x(0d).angleDot(0).xDot(0).build();
                double value = (Math.abs(angle) > 0.1) ? 5 : 10d;
                in.add(state.asList());
                out.add(value);
            }

            memory.fit(in, out);
            errors.add(memory.getError());
        }

        plotLoss(errors);
        Thread.sleep(1500);

        double outLargeAngle = memory.getOutValue(StatePole.builder().angle(0.2).x(0d).angleDot(0).xDot(0).build().asList());
        double outSmallAngle = memory.getOutValue(StatePole.builder().angle(0.0).x(0d).angleDot(0).xDot(0).build().asList());

        System.out.println("outLargeAngle = " + outLargeAngle);
        System.out.println("outSmallAngle = " + outSmallAngle);

        Assertions.assertTrue(outLargeAngle<outSmallAngle);

    }

    private static void plotLoss(List<Double> errors) {
        XYChart chart = QuickChart.getChart("Training error", "episode", "Error", "e(ep)", null, errors);
        new SwingWrapper<>(chart).displayChart();
    }

}
