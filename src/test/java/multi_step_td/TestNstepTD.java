package multi_step_td;

import common.Counter;
import common.ListUtils;
import common.MathUtils;
import lombok.extern.java.Log;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import multi_step_temp_diff.helpers.AgentInfo;
import multi_step_temp_diff.helpers.NStepTDHelper;
import multi_step_temp_diff.interfaces.AgentInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.models.AgentTabular;
import multi_step_temp_diff.models.ForkEnvironment;
import multi_step_temp_diff.models.StepReturn;
import org.apache.arrow.flatbuf.Int;
import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Log
public class TestNstepTD {
    private static final double DELTA = 0.1;
    private static final double PROB_RANDOM = 0.2;
    private static final int START_STATE = 0;
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
        int nofEpisodes = 1000;

        helper = NStepTDHelper.builder()
                .n(3)
                .episodeCounter(new Counter(0, nofEpisodes))
                .timeCounter(new Counter(0, Integer.MAX_VALUE))
                .build();
    }

    @Test
    public void fewEpisodesThreeStepper() {

        NStepTDHelper h=helper;
        while (!h.episodeCounter.isExceeded()) {
            agent.setState(START_STATE);
            h.reset();
            do {
                if (h.timeCounter.getCount() < h.T) {
                    final int action = agent.chooseAction(PROB_RANDOM);
                    stepReturn = environment.step(agent.getState(), action);

                    if (agent.getState()==5) {
                        System.out.println("action = " + action);
                        System.out.println("stepReturn = " + stepReturn);
                        System.out.println("agentCasted.getPairs() = " + agentCasted.getPairs());
                    }

                    h.statesMap.put(h.timeCounter.getCount(),agent.getState());
                    agent.updateState(stepReturn);
                    h.timeReturnMap.put(h.timeCounter.getCount()+1, stepReturn);
                    h.T = (stepReturn.isNewStateTerminal) ? h.timeCounter.getCount() + 1 : h.T;
                }

                h.tau = h.timeCounter.getCount() - h.n + 1;

                if (h.tau >= 0) {
                    double G = sumOfRewards(h);
                    G = ifNotBeyondTerminalBackUpFromStateAhead(h, G);
                    final int stateToUpdate = h.statesMap.get(h.tau);
                    double valuePresent = agent.readValue(stateToUpdate);
                    agentCasted.writeValue(stateToUpdate, valuePresent + h.alpha * (G - valuePresent));
                }
                h.timeCounter.increase();
            } while (h.tau != h.T - 1);
            System.out.println("h after episode = " + h);

            h.episodeCounter.increase();

        }


        AgentInfo agentInfo=new AgentInfo(agent);
        System.out.println("stateValueMap = " + agentInfo.stateValueMap(environment.stateSet()));

    }

    private double ifNotBeyondTerminalBackUpFromStateAhead(NStepTDHelper h, double G) {
        if (h.tau + h.n <= h.T) {
            final int stateAheadToBackupFrom = h.timeReturnMap.get(h.tau + h.n).newState;
            G = G + Math.pow(agent.getDiscountFactor(), h.n) * agent.readValue(stateAheadToBackupFrom);
        }
        return G;
    }

    private double sumOfRewards(NStepTDHelper h) {
        Pair<Integer, Integer> iMinMax = new Pair<>(h.tau + 1, Math.min(h.tau + h.n, h.T));
        List<Double> returnTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            returnTerms.add(Math.pow(agent.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }

       // System.out.println("iMinMax = " + iMinMax+", returnTerms = " + returnTerms);

        return ListUtils.sumDoubleList(returnTerms);
    }

}
