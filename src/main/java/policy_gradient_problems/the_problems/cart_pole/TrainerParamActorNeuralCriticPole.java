package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.abstract_classes.AgentParamActorNeuralCriticI;
import policy_gradient_problems.common_episode_trainers.MultistepNeuralCriticUpdater;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.AdvantageCalculator;
import policy_gradient_problems.common_helpers.NStepReturnInfo;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;

import static common.Conditionals.executeIfTrue;

/***
 *  Details in
 *  https://medium.com/intro-to-artificial-intelligence/the-actor-critic-reinforcement-learning-algorithm-c8095a655c14
 *  and pseudocode in md file pseudocode_pgrl.md
 *
 *  Thanks to the multi-step critic training, a balance between variance and bias can be achieved.
 *  Many steps (large stepHorizon) gives high variance, while few steps gives higher bias.
 *
 */

@Log
@Getter
public class TrainerParamActorNeuralCriticPole extends TrainerAbstractPole {

    AgentParamActorNeuralCriticI<VariablesPole> agent;

    @Builder
    public TrainerParamActorNeuralCriticPole(@NonNull EnvironmentPole environment,
                                             @NonNull AgentParamActorNeuralCriticI<VariablesPole> agent,
                                             @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }

    public void train() {
        Integer n = parameters.stepHorizon();
        var criticUpdater=new MultistepNeuralCriticUpdater<VariablesPole>(
                parameters,
                (s) -> agent.getCriticOut(s),
                (t) -> agent.fitCritic(t.getLeft(),t.getMiddle(),t.getRight()) );
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experiences = getExperiences(agent);
            criticUpdater.updateCritic(n, experiences);
            updateActor(experiences);
            printIfSuccessFul(ei, experiences);
            updateTracker(ei, experiences);
        }
    }

    void printIfSuccessFul(int ei, List<Experience<VariablesPole>> experiences) {
        var elInfo = new NStepReturnInfo<>(experiences, parameters);
        executeIfTrue(!elInfo.isEndExperienceFail(), () ->
                log.info("Episode successful, ei = " + ei + ", n steps = " + experiences.size()));
    }


    void updateActor(List<Experience<VariablesPole>> experiences) {
        var nri = new NStepReturnInfo<>(experiences, parameters);
        var ac=new AdvantageCalculator<VariablesPole>(parameters, (s) -> agent.getCriticOut(s));
        int T = experiences.size();
        for (int tau = 0; tau < T; tau++) {
            var expAtTau = nri.getExperience(tau);
            double advantage = ac.calcAdvantage(expAtTau);
            var gradLogVector = agent.calcGradLogVector(expAtTau.state(), expAtTau.action());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * advantage);
            agent.changeActor(changeInThetaVector);
        }
    }



}
