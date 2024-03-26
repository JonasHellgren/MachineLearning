package policy_gradient_problems.runners;

import plotters.PlotterMultiplePanelsTrajectory;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.multicoin_bandit.EnvironmentMultiCoinBandit;
import policy_gradient_problems.environments.multicoin_bandit.TrainerMultiCoinBanditAgentPPO;
import java.util.List;

public class RunnerMultiCoinBandit {

    public static void main(String[] args) {
        var environment= EnvironmentMultiCoinBandit.newWithProbabilities(0.0,1.0);
        var trainer=createTrainer(environment);
        trainer.train();
        plotActionProbabilitiesDuringTraining("ppo",trainer);

    }

    static TrainerMultiCoinBanditAgentPPO createTrainer(EnvironmentMultiCoinBandit environment) {
        return TrainerMultiCoinBanditAgentPPO.builder()
                .environment(environment)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(1).gamma(1d).build())
                .build();
    }

    private static void plotActionProbabilitiesDuringTraining(String title, TrainerMultiCoinBanditAgentPPO trainer) {
        var plotter = new PlotterMultiplePanelsTrajectory(title, List.of("pi(0)", "pi(1)"), "episode");
        plotter.plot(trainer.getTracker().getMeasureTrajectoriesForState(0));
    }

}
