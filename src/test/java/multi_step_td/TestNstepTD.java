package multi_step_td;

import common.Conditionals;
import common.Counter;
import common.ListUtils;
import common.ScalerLinear;
import lombok.extern.java.Log;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepTDHelper;
import multi_step_temp_diff.interfaces.AgentInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.models.AgentTabular;
import multi_step_temp_diff.models.ForkEnvironment;
import multi_step_temp_diff.models.StepReturn;
import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * https://www.cs.ubc.ca/labs/lci/mlrg/slides/Multi-step_Bootstrapping.pdf
 * https://lcalem.github.io/blog/2018/11/19/sutton-chap07-nstep
 */

@Log
public class TestNstepTD {
    private static final int START_STATE = 0;
    private static final int NOF_EPISODES = 100;
    private static final double P_RAND_START = 0.5d,P_RAND_END = 0d;
    private static final double ALPHA = 0.9;
    private static final int N = 3;
    EnvironmentInterface environment;
    AgentInterface agent;
    AgentTabular agentCasted;
    StepReturn stepReturn;
    NStepTDHelper helper;

    @Before
    public void init() {
        environment = new ForkEnvironment();
        agent = AgentTabular.newDefault();
        agentCasted = (AgentTabular) agent;       //to access class specific methods

        helper = NStepTDHelper.builder()
                 .alpha(ALPHA).n(N)
                .episodeCounter(new Counter(0, NOF_EPISODES))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .build();
    }

    @Test
    public void fewEpisodesThreeStepper() {

        NStepTDHelper h=helper;
        ScalerLinear scaler=new ScalerLinear(0, NOF_EPISODES, P_RAND_START, P_RAND_END);
        BiPredicate<Integer,Integer> isNotAtTerminationTime = (t,tTerm) -> t<tTerm;
        BiFunction<Integer,Integer,Integer> timeForUpdate = (t,n) -> t-n+1;
        Predicate<Integer> isUpdatePossible = (tau) -> tau>=0;
        BiPredicate<Integer,Integer> isAtTimeJustBeforeTermination = (tau,tTerm) -> tau == tTerm-1;

        while (!h.episodeCounter.isExceeded()) {
            agent.setState(START_STATE);
            h.reset();
            do {
                Conditionals.executeIfTrue(isNotAtTerminationTime.test(h.timeCounter.getCount(),h.T), () ->
                    chooseActionStepAndStoreExperience(h, scaler));
                h.tau = timeForUpdate.apply(h.timeCounter.getCount(),h.n);
                Conditionals.executeIfTrue(isUpdatePossible.test(h.tau), () ->
                    updateStateValueForStatePresentAtTimeTau(h));
                h.timeCounter.increase();
            } while (!isAtTimeJustBeforeTermination.test(h.tau,h.T));
            h.episodeCounter.increase();
        }

        AgentInfo agentInfo=new AgentInfo(agent);
        System.out.println("stateValueMap = " + agentInfo.stateValueMap(environment.stateSet()));

    }

    BiPredicate<Integer,Integer> isTimeToBackUpFromAtOrBeforeTermination = (t,tTerm) -> t<=tTerm;

    private void updateStateValueForStatePresentAtTimeTau(NStepTDHelper h) {
        double G = sumOfRewardsFromTimeToUpdatePlusOne(h);
        int tBackUpFrom=h.tau + h.n;
        if (isTimeToBackUpFromAtOrBeforeTermination.test(tBackUpFrom,h.T)) {
            int stateAheadToBackupFrom = h.timeReturnMap.get(h.tau + h.n).newState;
            G = G + Math.pow(agent.getDiscountFactor(), h.n) * agent.readValue(stateAheadToBackupFrom);
        }
        final int stateToUpdate = h.statesMap.get(h.tau);
        double valuePresent = agent.readValue(stateToUpdate);
        agentCasted.writeValue(stateToUpdate, valuePresent + h.alpha * (G - valuePresent));
    }


    private void chooseActionStepAndStoreExperience(NStepTDHelper h, ScalerLinear scaler) {
        final int action = agent.chooseAction(scaler.calcOutDouble(h.episodeCounter.getCount()));
        stepReturn = environment.step(agent.getState(), action);
        h.statesMap.put(h.timeCounter.getCount(),agent.getState());
        agent.updateState(stepReturn);
        h.timeReturnMap.put(h.timeCounter.getCount()+1, stepReturn);
        h.T = (stepReturn.isNewStateTerminal) ? h.timeCounter.getCount() + 1 : h.T;
    }

    private double sumOfRewardsFromTimeToUpdatePlusOne(NStepTDHelper h) {
        Pair<Integer, Integer> iMinMax = new Pair<>(h.tau + 1, Math.min(h.tau + h.n, h.T));
        List<Double> returnTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            returnTerms.add(Math.pow(agent.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(returnTerms);
    }

}
