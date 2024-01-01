package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.twoArmedBandit.*;

import java.util.List;

public class RunnerTwoArmedBandit {

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
        return EnvironmentBandit.newWithProbabilities(0.1, 0.5);
    }


    private static TrainerParameters getTrainerParametersParam() {
        return TrainerParameters.builder()
                .nofEpisodes(1000).nofStepsMax(1).gamma(1d).learningRateActor(1e-1).build();
    }

    private static TrainerParameters getTrainerParametersNeural() {
        return TrainerParameters.builder()
                .nofEpisodes(1_000).nofStepsMax(1).gamma(1d).learningRateActor(1e-3).build();
    }

}
