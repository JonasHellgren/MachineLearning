package multi_step_temp_diff.helpers;

import common.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import multi_step_temp_diff.interfaces_and_abstract.AgentAbstract;
import multi_step_temp_diff.interfaces_and_abstract.AgentInterface;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * https://www.cs.ubc.ca/labs/lci/mlrg/slides/Multi-step_Bootstrapping.pdf
 * https://lcalem.github.io/blog/2018/11/19/sutton-chap07-nstep
 */

@Builder
@Setter
public class NStepTabularAgentTrainer<S> {

    private static final double ALPHA = 0.5;
    private static final int N = 3;
    private static final int NOF_EPIS = 100;
    private static final int START_STATE = 0;

    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentInterface<S> agent;

    @Builder.Default
    double alpha= ALPHA;
    @Builder.Default
    int nofStepsBetweenUpdatedAndBackuped= N;
    @Builder.Default
    int nofEpisodes= NOF_EPIS;
    @Builder.Default
    double probStart=0.5;
    @Builder.Default
    double probEnd=0.01;

    @NonNull StateInterface<S> startState;

    AgentInfo<S> agentInfo;

    public void train() {
        agentInfo=new AgentInfo<>(agent);
        NStepTDHelper<S> h= NStepTDHelper.<S>builder()
                .alpha(alpha).n(nofStepsBetweenUpdatedAndBackuped)
                .episodeCounter(new Counter(0, nofEpisodes))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .build();

        LogarithmicDecay decayProb=new LogarithmicDecay(probStart, probEnd,nofEpisodes);
        BiPredicate<Integer,Integer> isNotAtTerminationTime = (t, tTerm) -> t<tTerm;
        BiFunction<Integer,Integer,Integer> timeForUpdate = (t, n) -> t-n+1;
        Predicate<Integer> isUpdatePossible = (tau) -> tau>=0;
        BiPredicate<Integer,Integer> isAtTimeJustBeforeTermination = (tau,tTerm) -> tau == tTerm-1;

        while (!h.episodeCounter.isExceeded()) {
            agent.setState(startState);

            h.reset();
            do {
                Conditionals.executeIfTrue(isNotAtTerminationTime.test(h.timeCounter.getCount(),h.T), () ->
                        chooseActionAndStep(h, decayProb));
                h.tau = timeForUpdate.apply(h.timeCounter.getCount(),h.n);
                Conditionals.executeIfTrue(isUpdatePossible.test(h.tau), () ->
                        updateStateValueForStatePresentAtTimeTau(h));
                h.timeCounter.increase();

            //    System.out.println("h = " + h);
            } while (!isAtTimeJustBeforeTermination.test(h.tau,h.T));
            h.episodeCounter.increase();
        }


    }

    public Map<StateInterface<S>,Double> getStateValueMap() {
        AgentInfo<S> agentInfo=new AgentInfo<>(agent);
        return agentInfo.stateValueMap(environment.stateSet());
    }


    static  BiPredicate<Integer,Integer> isTimeToBackUpFromAtOrBeforeTermination = (t,tTerm) -> t<=tTerm;

    private void updateStateValueForStatePresentAtTimeTau(NStepTDHelper<S> h) {
        double sumRewards = sumOfRewardsFromTimeToUpdatePlusOne(h), G=sumRewards;
        int tBackUpFrom=h.tau + h.n;
        if (isTimeToBackUpFromAtOrBeforeTermination.test(tBackUpFrom,h.T)) {
            StateInterface<S> stateAheadToBackupFrom = h.timeReturnMap.get(h.tau + h.n).newState;
            G = sumRewards + Math.pow(agentInfo.getDiscountFactor(), h.n) * agent.readValue(stateAheadToBackupFrom);
        }
        final StateInterface<S> stateToUpdate = h.statesMap.get(h.tau);
        double valuePresent = agent.readValue(stateToUpdate);
        AgentAbstract<S> agentCasted = (AgentAbstract<S>) agent;       //to access class specific methods
        final double difference = G - valuePresent;

        agentCasted.writeValue(stateToUpdate, valuePresent + h.alpha * difference);
        agentCasted.addTemporalDifference(difference);
    }


    private void chooseActionAndStep(NStepTDHelper<S> h, LogarithmicDecay scaler) {
        final int action = agent.chooseAction(scaler.calcOut(h.episodeCounter.getCount()));
        StepReturn<S> stepReturn = environment.step(agent.getState(), action);
        h.statesMap.put(h.timeCounter.getCount(),agent.getState());
        agent.updateState(stepReturn);
        h.timeReturnMap.put(h.timeCounter.getCount()+1, stepReturn);
        h.T = (stepReturn.isNewStateTerminal) ? h.timeCounter.getCount() + 1 : h.T;
    }

    private double sumOfRewardsFromTimeToUpdatePlusOne(NStepTDHelper<S> h) {
        Pair<Integer, Integer> iMinMax = new Pair<>(h.tau + 1, Math.min(h.tau + h.n, h.T));
        List<Double> returnTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            returnTerms.add(Math.pow(agentInfo.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(returnTerms);
    }




}
