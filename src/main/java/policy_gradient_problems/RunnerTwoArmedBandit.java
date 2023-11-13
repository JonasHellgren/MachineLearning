package policy_gradient_problems;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.twoArmedBandit.Agent;
import policy_gradient_problems.twoArmedBandit.Environment;
import policy_gradient_problems.twoArmedBandit.Trainer;

import java.util.List;

public class RunnerTwoArmedBandit {

    public static void main(String[] args) {
        var trainer = createEnvironmentAgentTrainerAndReturnTrainer();
        trainer.train();
        plotActionProbabilitiesDuringTraining(trainer);
    }

    private static Trainer createEnvironmentAgentTrainerAndReturnTrainer() {
        var environment = Environment.newWithProbabilities(0.1, 0.5);
        var agent = Agent.newDefault();
        return createTrainer(environment, agent);
    }

    private static void plotActionProbabilitiesDuringTraining(Trainer trainer) {
        var plotter = new PlotterMultiplePanelsTrajectory(List.of("pi(0)", "pi(1)"), "episode");
        plotter.plot(trainer.getTracker().getProbabilitiesTrajectoriesForState(0));
    }

    private static Trainer createTrainer(Environment environment, Agent agent) {
        return Trainer.builder()
                .environment(environment)
                .agent(agent)
                .nofEpisodes(1000).nofStepsMax(1).gamma(1d).learningRate(0.2)
                .build();
    }

}
