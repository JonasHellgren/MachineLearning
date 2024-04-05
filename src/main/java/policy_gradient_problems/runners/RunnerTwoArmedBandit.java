package policy_gradient_problems.runners;

import lombok.extern.java.Log;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.twoArmedBandit.*;

@Log
public class RunnerTwoArmedBandit {

    public static final int NOF_EPISODES = 1_000;
    public static final int NOF_STEPS_MAX = 1;

    public static void main(String[] args) {
        var trainerParam = createTrainerParam();
        trainerParam.train();
        plotting("param",trainerParam);  //will result in log warning for training progress

        var trainerNeural = createTrainerNeural();
        trainerNeural.train();
        plotting("neural",trainerNeural);

        log.info("finished");
    }

    private static void plotting(String title, TrainerAbstractBandit trainer) {
        trainer.getRecorderTrainingProgress().plot("Neural CEM");
        trainer.getRecorderActionProbabilities().plot("Action probs, "+title);
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
                .agent(AgentBanditNeuralActorI.newDefault(getTrainerParametersNeural().learningRateNonNeuralActor()))
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
