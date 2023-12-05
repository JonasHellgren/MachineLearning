package policy_gradient_problems.runners;

import common.MovingAverage;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.cart_pole.AgentPole;
import policy_gradient_problems.the_problems.cart_pole.EnvironmentPole;
import policy_gradient_problems.the_problems.cart_pole.ParametersPole;
import policy_gradient_problems.the_problems.cart_pole.TrainerVanillaPole;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RunnerTrainersPole {


    public static void main(String[] args) {

        AgentPole agent=AgentPole.newAllZeroStateDefaultThetas();
        EnvironmentPole environment=new EnvironmentPole(ParametersPole.newDefault());

        var trainerVanilla = TrainerVanillaPole.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(2000).nofStepsMax(100).gamma(0.99).learningRate(2e-3)
                        .build())
                .build();
        trainerVanilla.train();

        TrainingTracker tracker = trainerVanilla.getTracker();
        List<Double> nofStepsList = tracker.getMeasureTrajectoriesForState(0).get(0);
        MovingAverage movingAverage=new MovingAverage(100,nofStepsList);
        XYChart chart = QuickChart.getChart("Sample Chart", "Episode", "Nof steps", "vanilla",
                IntStream.range(0,nofStepsList.size()).boxed().toList(),
                movingAverage.getFiltered());
        new SwingWrapper<>(chart).displayChart();

    }


}
