package black_jack;

import black_jack.enums.CardAction;
import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.LearnerStateValue;
import black_jack.models_cards.*;
import black_jack.models_episode.Episode;
import black_jack.models_memory.StateValueMemory;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.policies.PolicyHitBelow20;
import black_jack.policies.PolicyInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class TestStateValueMemory {
    public static final double DELTA = 0.1;
    public static final int NOF_UPDATES = 10_000;
    public static final double ALPHA = 0.01;
    boolean regardNofVisitsFlag=true;
    public static final int NOF_EPISODES = 100_000;

    EnvironmentInterface environment;
    Episode episode;
    ReturnsForEpisode returnsForEpisode;
    StateValueMemory stateValueMemory;
    NumberOfStateVisitsMemory numberOfStateVisitsMemory;
    LearnerStateValue learner;
    PolicyInterface policy;

    @Before
    public void init() {
        environment=new BlackJackEnvironment();
        episode = new Episode();
        returnsForEpisode = new ReturnsForEpisode();
        stateValueMemory = new StateValueMemory();
        numberOfStateVisitsMemory =new NumberOfStateVisitsMemory();
        learner = new LearnerStateValue(stateValueMemory, numberOfStateVisitsMemory,ALPHA,regardNofVisitsFlag);
        policy = new PolicyHitBelow20();
    }

    @Test
    public void learnMemoryFromOneEpisode() {

        StateObserved s1 = new StateObserved(2, false, 17);
        StateObserved s2 = new StateObserved(5, false, 17);
        StateObserved s3 = new StateObserved(12, false, 17);
        StateObserved s4 = new StateObserved(21, false, 17);

        episode.add(s1, CardAction.hit, 0d);
        episode.add(s2, CardAction.hit, 0d);
        episode.add(s3, CardAction.hit, 0d);
        episode.add(s4, CardAction.stick, 1d);

        returnsForEpisode.appendReturns(episode);

        System.out.println("returnsForEpisode = " + returnsForEpisode);

        learner.setRegardNofVisitsFlag(false);
        for (int i = 0; i < NOF_UPDATES; i++) {
            learner.updateMemoryFromEpisodeReturns(returnsForEpisode);
        }

        System.out.println("valueMemory = " + stateValueMemory);

        Assert.assertEquals(4, stateValueMemory.nofItems());
        Assert.assertEquals(1, stateValueMemory.read(s1), DELTA);
        Assert.assertEquals(1, stateValueMemory.read(s2), DELTA);

    }

    @Test
    public void learnMemoryFromManyEpisodes() {

        EpisodeRunner episodeRunner=new EpisodeRunner(environment,policy);
        for (int i = 0; i < NOF_EPISODES; i++) {
            StateCards cards = StateCards.newRandomPairs();
            Episode episode=episodeRunner.play(cards);
            returnsForEpisode.clear();
            returnsForEpisode.appendReturns(episode);
            learner.updateMemoryFromEpisodeReturns(returnsForEpisode);
        }

        //System.out.println("valueMemory = " + valueMemory);

        System.out.println("valueMemory.nofItems() = " + stateValueMemory.nofItems());
        System.out.println("numberOfVisitsMemory.nofItems() = " + numberOfStateVisitsMemory.nofItems());
        System.out.println("valueMemory.average() = " + stateValueMemory.average());
        System.out.println("numberOfVisitsMemory.average() = " + numberOfStateVisitsMemory.average());

        Assert.assertTrue(stateValueMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
        Assert.assertTrue(numberOfStateVisitsMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
    }

    @Test public void allStates() {
        Set<StateObserved> stateSet= StateInterface.allStates();
        System.out.println("stateSet = " + stateSet);
        System.out.println("stateSet.size() = " + stateSet.size());
        Assert.assertTrue(stateSet.size()>200);
    }

}
