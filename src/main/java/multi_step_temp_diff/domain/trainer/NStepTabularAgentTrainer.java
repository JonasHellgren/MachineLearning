package multi_step_temp_diff.domain.trainer;

import common.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import multi_step_temp_diff.domain.agent_abstract.AgentTabularInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.helpers.NStepTDHelper;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * https://www.cs.ubc.ca/labs/lci/mlrg/slides/Multi-step_Bootstrapping.pdf
 * https://lcalem.github.io/blog/2018/11/19/sutton-chap07-nstep
 *
 * a potential improvement is to check that start state is valid
 */

@Builder
@Setter
public class NStepTabularAgentTrainer<S> {

    private static final double ALPHA = 0.5;
    private static final int N = 3;
    private static final int NOF_EPIS = 100;
    private static final int START_STATE = 0;

    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentTabularInterface<S> agent;

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
    @NonNull Supplier<StateInterface<S>> startStateSupplier;

    AgentInfo<S> agentInfo;

    public void train() {
        agentInfo=new AgentInfo<>(agent);
        NStepTDHelper<S> h= NStepTDHelper.newFromNofEpisodesAndNofStepsBetween(nofEpisodes,nofStepsBetweenUpdatedAndBackuped);

        LogarithmicDecay decayProb=new LogarithmicDecay(probStart, probEnd,nofEpisodes);
        BiPredicate<Integer,Integer> isNotAtTerminationTime = (t, tTerm) -> t<tTerm;
        BiFunction<Integer,Integer,Integer> timeForUpdate = (t, n) -> t-n+1;
        Predicate<Integer> isUpdatePossible = (tau) -> tau>=0;
        BiPredicate<Integer,Integer> isAtTimeJustBeforeTermination = (tau,tTerm) -> tau == tTerm-1;

        while (!h.episodeCounter.isExceeded()) {
            agent.setState(startStateSupplier.get());

            h.reset();
            do {
                Conditionals.executeIfTrue(isNotAtTerminationTime.test(h.timeCounter.getCount(),h.T), () ->
                        chooseActionAndStep(h, decayProb));
                h.tau = timeForUpdate.apply(h.timeCounter.getCount(),nofStepsBetweenUpdatedAndBackuped);
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
        int tBackUpFrom=h.tau + nofStepsBetweenUpdatedAndBackuped;
        if (isTimeToBackUpFromAtOrBeforeTermination.test(tBackUpFrom,h.T)) {
            StateInterface<S> stateAheadToBackupFrom = h.timeReturnMap.get(h.tau + nofStepsBetweenUpdatedAndBackuped).newState;
            G = sumRewards + Math.pow(agentInfo.getDiscountFactor(), nofStepsBetweenUpdatedAndBackuped) * agent.readValue(stateAheadToBackupFrom);
        }
        final StateInterface<S> stateToUpdate = h.statesMap.get(h.tau);
        double valuePresent = agent.readValue(stateToUpdate);
        final double difference = G - valuePresent;

        agent.writeValue(stateToUpdate, valuePresent + alpha * difference);
        agent.storeTemporalDifference(difference);
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
        Pair<Integer, Integer> iMinMax = new Pair<>(h.tau + 1, Math.min(h.tau + nofStepsBetweenUpdatedAndBackuped, h.T));
        List<Double> returnTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            returnTerms.add(Math.pow(agentInfo.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(returnTerms);
    }




}
