package monte_carlo_search.network_training;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.energy_trading.EnvironmentEnergyTrading;
import monte_carlo_tree_search.domains.energy_trading.VariablesEnergyTrading;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import monte_carlo_tree_search.network_training.ReplayBufferValueSetter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestReplayBufferValueSetter {

    private static final int MAX_BUFFER_SIZE = 10;
    private static final double DELTA = 0.1;
    ReplayBuffer<VariablesEnergyTrading,Integer> bufferEpisode;


    @Before
    public void init() {
        bufferEpisode=new ReplayBuffer<>(MAX_BUFFER_SIZE);
        List<Double> soEList= Arrays.asList(0.5,0.7,0.7,0.7,0.7,0.7,0.7,0.5);
        List<Double> rewardList= Arrays.asList(-1d,0.0,0.0,0.0,0.0,0.0,2.0,0.0);
        for (int time = 0; time < EnvironmentEnergyTrading.AFTER_MAX_TIME; time++) {
        bufferEpisode.addExperience(Experience.<VariablesEnergyTrading, Integer>builder()
                .stateVariables(VariablesEnergyTrading.builder()
                        .time(time).SoE(soEList.get(time)).build())
                .reward(rewardList.get(time))
                .build());
        }
    }

    @Test
    public void givenBuffer_thenCorrect() {
        System.out.println("bufferEpisode = " + bufferEpisode);
        Assert.assertEquals(EnvironmentEnergyTrading.AFTER_MAX_TIME,bufferEpisode.size());
    }

    @Test public void givenBuffer_thenReturns() {
        ReplayBufferValueSetter<VariablesEnergyTrading,Integer> vs=new ReplayBufferValueSetter<>(bufferEpisode,1,true);

        ReplayBuffer<VariablesEnergyTrading,Integer> bufferEpisodeWithValues=vs.createBufferFromReturns(1);
        System.out.println("bufferEpisodeWithValues = " + bufferEpisodeWithValues);

        Assert.assertEquals(EnvironmentEnergyTrading.AFTER_MAX_TIME,bufferEpisodeWithValues.size());
        Assert.assertEquals(1,bufferEpisodeWithValues.getExperience(0).value, DELTA);
        Assert.assertEquals(2,bufferEpisodeWithValues.getExperience(6).value, DELTA);
    }

}
