package policy_gradient_problems.the_problems.cart_pole;

import common_records.NetSettings;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.ArrayList;
import java.util.List;

import static common.Conditionals.executeIfTrue;

/***
 *  Details in
 *  https://medium.com/intro-to-artificial-intelligence/the-actor-critic-reinforcement-learning-algorithm-c8095a655c14
 *
 *  An episode gives a set of experiences: e0, e1, e2, ei,.....e_i+n
 *  Two cases are present for ei: 1) it is fail state 2) not fail state
 *  In the first case the actor should learn from the entire episode, it shall learn from the mistake resulting in fail
 *  But in the second case it shall stop learning after e_end-n. The reason is that the experiences after end-n are
 *  "miss leading". They indicate few rewards remaining, but that is not due to failure but due to non-fail terminal
 *  episode. Many environments stop after a specific amount of steps.
 *
 */

@Log
@Getter
public class TrainerActorCriticPole extends TrainerAbstractPole {

    NeuralMemoryPole memory;

    @Builder
    public TrainerActorCriticPole(@NonNull EnvironmentPole environment,
                                  @NonNull AgentPole agent,
                                  @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
        NetSettings netSettings = NetSettings.builder().nofFitsPerEpoch(10).learningRate(parameters.beta()).build();
        this.memory = new NeuralMemoryPole(netSettings, environment.getParameters());
    }

    public void train() {
        Integer n = parameters.stepHorizon();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experienceList = getExperiences();
            updateCritic(n, experienceList);
            updateActor(experienceList);
            printIfSuccessFul(ei, experienceList);
            updateTracker(ei, experienceList);
        }
    }

    private void updateCritic(Integer n,  List<ExperiencePole> experienceList) {
        int T = experienceList.size();
        double gammaPowN = Math.pow(parameters.gamma(), parameters.stepHorizon());
        var elInfo = new NStepReturnInfoPole(experienceList, parameters);
        int tEnd= elInfo.isEndExperienceFail() ? T : T - n + 1;  //explained in top of file
        List<List<Double>> stateValuesList = new ArrayList<>();
        List<Double> valueTarList = new ArrayList<>();
        for (int t = 0; t < tEnd; t++) {
            var resMS = elInfo.getResultManySteps(t);
            double sumRewards = resMS.sumRewardsNSteps();
            double valueFut = (resMS.stateFuture().isPresent())
                    ? gammaPowN * memory.getOutValue(resMS.stateFuture().orElseThrow())
                    : 0;
            stateValuesList.add(elInfo.getExperience(t).state().asList());
            valueTarList.add(sumRewards + valueFut);
        }
        executeIfTrue(!stateValuesList.isEmpty(), () -> memory.fit(stateValuesList, valueTarList));
    }


    private void  updateActor(List<ExperiencePole> experienceList) {
        var elInfo = new NStepReturnInfoPole(experienceList, parameters);
        int T = experienceList.size();
        for (int tau = 0; tau < T; tau++) {
            var expAtTau = elInfo.getExperience(tau);
            double advantage = calcAdvantage(expAtTau);
            var gradLogVector = agent.calcGradLogVector(expAtTau.state(), expAtTau.action());
            var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * advantage);
            agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
        }
    }

    /**
     *  If an action leads to a fail state, the advantage calculation focus on the immediate reward,
     *  value of future state can be regarded as not possible to define/irrelevant due to the fail state.
     */

    private double calcAdvantage(ExperiencePole expAtTau) {
        double r = expAtTau.reward();
        Double valueS = memory.getOutValue(expAtTau.state());
        Double valueSNew = memory.getOutValue(expAtTau.stateNext());
        boolean isActionResultingInFailState=expAtTau.isFail();
        return (isActionResultingInFailState) ? r  : r+parameters.gamma()*valueSNew-valueS;
    }

    private void printIfSuccessFul(int ei, List<ExperiencePole> experienceList) {
        var elInfo = new NStepReturnInfoPole(experienceList, parameters);
        executeIfTrue(!elInfo.isEndExperienceFail(), () ->
                log.info("Episode successful, ei = "+ ei+", n steps = "+ experienceList.size()));
    }

}
