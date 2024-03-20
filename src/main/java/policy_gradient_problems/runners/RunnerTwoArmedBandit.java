package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.twoArmedBandit.*;

import java.util.List;

public class RunnerTwoArmedBandit {

    public static final int NOF_EPISODES = 1_000, NOF_STEPS_MAX = 1;

    public static void main(String[] args) {
        var trainerParam = createTrainerParam();
        trainerParam.train();
        plotActionProbabilitiesDuringTraining("param",trainerParam);

        var trainerNeural = createTrainerNeural();
        trainerNeural.train();
        plotActionProbabilitiesDuringTraining("neural",trainerNeural);
    }

    private static void plotActionProbabilitiesDuringTraining(String title, TrainerAbstractBandit trainer) {
        var plotter = new PlotterMultiplePanelsTrajectory(title,List.of("pi(0)", "pi(1)"), "episode");
        plotter.plot(trainer.getTracker().getMeasureTrajectoriesForState(0));
    }

    private static TrainerBanditParamActor createTrainerParam() {
        return TrainerBanditParamActor.builder()
                .environment(getEnvironment())
                .agent(AgentBanditParamActor.newDefault())
                .parameters(getTrainerParametersParam())
                .build();
    }

    private static TrainerBanditNeuralActor createTrainerNeural() {
        return TrainerBanditNeuralActor.builder()
                .environment(getEnvironment())
                .agent(AgentBanditNeuralActor.newDefault(getTrainerParametersNeural().learningRateActor()))
                .parameters(getTrainerParametersNeural())
                .build();
    }

    private static EnvironmentBandit getEnvironment() {
        return EnvironmentBandit.newWithProbabilities(0.1, 0.9);
    }

    private static TrainerParameters getTrainerParametersParam() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX).learningRateActor(1e-1).build();
    }

    private static TrainerParameters getTrainerParametersNeural() {
        return TrainerParameters.builder()
                .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX).learningRateActor(1e-1).build();
    }

}
