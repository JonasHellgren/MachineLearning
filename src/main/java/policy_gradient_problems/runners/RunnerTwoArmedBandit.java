package policy_gradient_problems.runners;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.twoArmedBandit.*;

import java.util.List;

public class RunnerTwoArmedBandit {

    public static final int NOF_EPISODES = 1_000;
    public static final int NOF_STEPS_MAX = 1;

    public static void main(String[] args) {
        var trainerParam = createTrainerParam();
        trainerParam.train();
        plotActionProbabilitiesDuringTraining("param",trainerParam);
        plotActorLoss(trainerParam);  //will result in log warning

        var trainerNeural = createTrainerNeural();
        trainerNeural.train();
        plotActionProbabilitiesDuringTraining("neural",trainerNeural);
        plotActorLoss(trainerNeural);
    }

    private static void plotActionProbabilitiesDuringTraining(String title, TrainerAbstractBandit trainer) {
        var plotter = new PlotterMultiplePanelsTrajectory(title,List.of("pi(0)", "pi(1)"), "episode");
        plotter.plot(trainer.getRecorderActionProbabilities().probTrajectoryForEachAction(0));

    }

    private static void plotActorLoss(TrainerAbstractBandit trainer) {
        trainer.getRecorderTrainingProgress().plot("Neural CEM");
    }

    private static TrainerBanditParamActor createTrainerParam() {
        return TrainerBanditParamActor.builder()
                .environment(getEnvironment())
                .agent(AgentBanditParamActor.newDefault())
                .parameters(getTrainerParametersParam())
                .build();
    }

    private static TrainerBanditNeuralActorCEM createTrainerNeural() {
        return TrainerBanditNeuralActorCEM.builder()
                .environment(getEnvironment())
                .agent(AgentBanditNeuralActor.newDefault(getTrainerParametersNeural().learningRateNonNeuralActor()))
                .parameters(getTrainerParametersNeural())
                .build();
    }

    private static EnvironmentBandit getEnvironment() {
        return EnvironmentBandit.newWithProbabilities(0.1, 0.9);
    }

    private static TrainerParameters getTrainerParametersParam() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX).learningRateNonNeuralActor(1e-1).build();
    }

    private static TrainerParameters getTrainerParametersNeural() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX).build();
    }

}
