package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.the_problems.twoArmedBandit.AgentBandit;
import policy_gradient_problems.the_problems.twoArmedBandit.EnvironmentBandit;
import policy_gradient_problems.the_problems.twoArmedBandit.TrainerBandit;

import java.util.List;

public class RunnerTwoArmedBandit {

    public static void main(String[] args) {
        var trainer = createEnvironmentAgentTrainerAndReturnTrainer();
        trainer.train();
        plotActionProbabilitiesDuringTraining(trainer);
    }

    private static TrainerBandit createEnvironmentAgentTrainerAndReturnTrainer() {
        var environment = EnvironmentBandit.newWithProbabilities(0.1, 0.5);
        var agent = AgentBandit.newDefault();
        return createTrainer(environment, agent);
    }

    private static void plotActionProbabilitiesDuringTraining(TrainerBandit trainer) {
        var plotter = new PlotterMultiplePanelsTrajectory(List.of("pi(0)", "pi(1)"), "episode");
        plotter.plot(trainer.getTracker().getMeasureTrajectoriesForState(0));
    }

    private static TrainerBandit createTrainer(EnvironmentBandit environment, AgentBandit agent) {
        return TrainerBandit.builder()
                .environment(environment)
                .agent(agent)
                .nofEpisodes(1000).nofStepsMax(1).gamma(1d).learningRate(0.2)
                .build();
    }

}
