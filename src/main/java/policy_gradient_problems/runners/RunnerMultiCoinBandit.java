package policy_gradient_problems.runners;

import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.multicoin_bandit.EnvironmentMultiCoinBandit;
import policy_gradient_problems.environments.multicoin_bandit.TrainerMultiCoinBanditAgentPPO;

public class RunnerMultiCoinBandit {

    public static void main(String[] args) {
        var environment= EnvironmentMultiCoinBandit.newWithProbabilities(0.0,1.0);
        var trainer=createTrainer(environment);
        trainer.train();
        plotRecorders(trainer);
    }

    static TrainerMultiCoinBanditAgentPPO createTrainer(EnvironmentMultiCoinBandit environment) {
        return TrainerMultiCoinBanditAgentPPO.builder()
                .environment(environment)
                .parameters(TrainerParameters.builder()
                        .nofEpisodes(100).nofStepsMax(1).gamma(1d).build())
                .build();
    }

    private static void plotRecorders(TrainerMultiCoinBanditAgentPPO trainer) {
        trainer.getRecorderActionProbabilities().plot("Action probs - Multicoin bandit");
        trainer.getRecorderTrainingProgress().plot("Multi coin bandit - PPO");
    }

}
