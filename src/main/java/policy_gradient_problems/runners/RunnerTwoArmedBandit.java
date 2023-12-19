package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.twoArmedBandit.AgentBanditRealVector;
import policy_gradient_problems.the_problems.twoArmedBandit.EnvironmentBandit;
import policy_gradient_problems.the_problems.twoArmedBandit.TrainerBanditRealVector;

import java.util.List;

public class RunnerTwoArmedBandit {

    public static void main(String[] args) {
        var trainer = createEnvironmentAgentTrainerAndReturnTrainer();
        trainer.train();
        plotActionProbabilitiesDuringTraining(trainer);
    }

    private static TrainerBanditRealVector createEnvironmentAgentTrainerAndReturnTrainer() {
        var environment = EnvironmentBandit.newWithProbabilities(0.1, 0.5);
        var agent = AgentBanditRealVector.newDefault();
        return createTrainer(environment, agent);
    }

    private static void plotActionProbabilitiesDuringTraining(TrainerBanditRealVector trainer) {
        var plotter = new PlotterMultiplePanelsTrajectory(List.of("pi(0)", "pi(1)"), "episode");
        plotter.plot(trainer.getTracker().getMeasureTrajectoriesForState(0));
    }

    private static TrainerBanditRealVector createTrainer(EnvironmentBandit environment, AgentBanditRealVector agent) {
        return TrainerBanditRealVector.builder()
                .environment(environment)
                .agent(agent)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(1000).nofStepsMax(1).gamma(1d).learningRateActor(0.2).build())
                .build();
    }

}
