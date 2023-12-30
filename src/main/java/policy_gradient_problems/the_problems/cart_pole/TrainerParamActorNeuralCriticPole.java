package policy_gradient_problems.the_problems.cart_pole;

import common_dl4j.NetSettings;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.abstract_classes.AgentParamActorNeuralCriticI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;

import static common.Conditionals.executeIfTrue;

/***
 *  Details in
 *  https://medium.com/intro-to-artificial-intelligence/the-actor-critic-reinforcement-learning-algorithm-c8095a655c14
 *  and pseudocode in md file pseudocode_pgrl.md
 *
 *
 *  An episode gives a set of experiences: e0, e1, e2, ei,.....,e_i+n
 *  Two cases are present for e_end: 1) it is fail state 2) not fail state
 *  In the first case the actor should learn from the entire episode, it shall learn from the mistake resulting in fail
 *  But in the second case it shall stop learning after e_end-n. The reason is that the experiences after end-n are
 *  "miss leading". They indicate few rewards remaining, but that is not due to failure but due to non-fail terminal
 *  episode. Many environments stop after a specific amount of steps.
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
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experiences = getExperiences(agent);
            updateCritic(n, experiences);
            updateActor(experiences);
            printIfSuccessFul(ei, experiences);
            updateTracker(ei, experiences);
        }
    }

    void updateCritic(Integer n, List<Experience<VariablesPole>> experiences) {
        int T = experiences.size();
        double gammaPowN = Math.pow(parameters.gamma(), n);
        var elInfo = new NStepReturnInfoPole(experiences, parameters);
        int tEnd = elInfo.isEndExperienceFail() ? T : T - n + 1;  //explained in top of file
        List<List<Double>> stateValuesList = new ArrayList<>();
        List<Double> valueTarList = new ArrayList<>();

        for (int t = 0; t < tEnd; t++) {
            var resMS = elInfo.getResultManySteps(t);
           // executeIfTrue(resMS.isEndOutside(), () -> log.info("Warning isEndOutside"));
            double sumRewards = resMS.sumRewardsNSteps();
            double valueFut = (resMS.stateFuture().isPresent())
                    ? gammaPowN * agent.getCriticOut(resMS.stateFuture().orElseThrow())
                    : 0;
            stateValuesList.add(elInfo.getExperience(t).state().asList());
            valueTarList.add(sumRewards + valueFut);
        }
        int nofFits = (int) Math.max(1,(parameters.relativeNofFitsPerEpoch() * tEnd));  //todo get from record method
        executeIfTrue(!stateValuesList.isEmpty(), () -> agent.fitCritic(stateValuesList, valueTarList, nofFits));
        executeIfTrue(stateValuesList.isEmpty(), () -> log.warning("empty stateValuesList"));
        stateValuesList.clear();
        valueTarList.clear();
    }


    void updateActor(List<Experience<VariablesPole>> experiences) {
        var elInfo = new NStepReturnInfoPole(experiences, parameters);
        int T = experiences.size();
        for (int tau = 0; tau < T; tau++) {
            var expAtTau = elInfo.getExperience(tau);
            double advantage = calcAdvantage(expAtTau);
            var gradLogVector = agent.calcGradLogVector(expAtTau.state(), expAtTau.action());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * advantage);
            agent.changeActor(changeInThetaVector);
        }
    }

    /**
     * advantage=Q(s,a)-V(s)=r+γ*V(s')-V(s')
     * If an action leads to a fail state, the advantage calculation focus on the immediate reward,
     * value of future state can be regarded as not possible to define/irrelevant due to the fail state.
     */
    double calcAdvantage(Experience<VariablesPole> expAtTau) {
        double r = expAtTau.reward();
        double valueS = agent.getCriticOut(expAtTau.state());
        double valueSNew = agent.getCriticOut(expAtTau.stateNext());
        boolean isActionResultingInFailState = expAtTau.isFail();
        return (isActionResultingInFailState) ? r : r + parameters.gamma() * valueSNew - valueS;
    }

    void printIfSuccessFul(int ei, List<Experience<VariablesPole>> experiences) {
        var elInfo = new NStepReturnInfoPole(experiences, parameters);
        executeIfTrue(!elInfo.isEndExperienceFail(), () ->
                log.info("Episode successful, ei = " + ei + ", n steps = " + experiences.size()));
    }

}
