package black_jack;

import black_jack.enums.CardAction;
import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.LearnerStateActionValue;
import black_jack.models_cards.StateObservedAction;
import black_jack.models_cards.StateCards;
import black_jack.models_episode.Episode;
import black_jack.models_memory.NumberOfStateActionsVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.policies.PolicyGreedyOnStateActionMemory;
import black_jack.policies.PolicyHitBelow20;
import black_jack.policies.PolicyInterface;
import black_jack.policies.PolicyRandom;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestStateShipActionsValueMemoryDP {

    public static final double DELTA = 0.1;
    public static final int NOF_UPDATES = 10_000;
    public static final double ALPHA = 0.01;
    private static final double ZERO_REWARD = 0d;
    private static final double ONE_REWARD = 1d;
    private static final double PROBABILITY_RANDOM_ACTION = 0.1;
    boolean regardNofVisitsFlag=true;
    public static final int NOF_EPISODES = 100_000;

    EnvironmentInterface environment;
    Episode episode;
    ReturnsForEpisode returnsForEpisode;
    StateActionValueMemory stateActionValueMemory;
    NumberOfStateActionsVisitsMemory numberOfStateVisitsMemory;
    LearnerStateActionValue learner;

    @Before
    public void init() {
        environment=new BlackJackEnvironment();
        episode = new Episode();
        returnsForEpisode = new ReturnsForEpisode();
        stateActionValueMemory = new StateActionValueMemory();
        numberOfStateVisitsMemory =new NumberOfStateActionsVisitsMemory();
        learner = new LearnerStateActionValue(stateActionValueMemory, numberOfStateVisitsMemory,ALPHA,regardNofVisitsFlag);

    }

    @Test
    public void learnMemoryFromOneEpisode() {

        StateObservedAction s1 = StateObservedAction.newFromScalars(2, false, 17,CardAction.hit);
        StateObservedAction s2 = StateObservedAction.newFromScalars(5, false, 17,CardAction.hit);
        StateObservedAction s3 = StateObservedAction.newFromScalars(12, false, 17,CardAction.hit);
        StateObservedAction s4 = StateObservedAction.newFromScalars(21, false, 17,CardAction.stick);

        episode.add(s1, ZERO_REWARD);
        episode.add(s2, ZERO_REWARD);
        episode.add(s3, ZERO_REWARD);
        episode.add(s4, ONE_REWARD);
        returnsForEpisode.appendReturns(episode);
        learner.setRegardNofVisitsFlag(false);
        for (int i = 0; i < NOF_UPDATES; i++) {
            learner.updateMemoryFromEpisodeReturns(returnsForEpisode);
        }

         System.out.println("valueMemory = " + stateActionValueMemory);

        Assert.assertEquals(4, stateActionValueMemory.nofItems());
        Assert.assertEquals(4, stateActionValueMemory.getVisitedStates().size());

        Assert.assertEquals(ONE_REWARD, stateActionValueMemory.read(s1), DELTA);
        Assert.assertEquals(ONE_REWARD, stateActionValueMemory.read(s2), DELTA);

    }

    @Test
    public void learnMemoryFromManyEpisodes() {
        trainMemory(new PolicyHitBelow20());
        System.out.println("valueMemory.nofItems() = " + stateActionValueMemory.nofItems());
        System.out.println("numberOfVisitsMemory.nofItems() = " + numberOfStateVisitsMemory.nofItems());
       // System.out.println("numberOfVisitsMemory.average() = " + numberOfStateVisitsMemory.average());
        System.out.println("valueMemory = " + stateActionValueMemory);

        Assert.assertTrue(stateActionValueMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
        Assert.assertTrue(numberOfStateVisitsMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
    }

    @Test
    public void learnForSumPlayer21StickIsBetterThanHit() {
        trainMemory(new PolicyRandom());

        double valueStick=stateActionValueMemory.read(StateObservedAction.newFromScalars(21,true,10,CardAction.stick));
        double valueHit=stateActionValueMemory.read(StateObservedAction.newFromScalars(21,true,10,CardAction.hit));

        double nofVisits=learner.getNumberOfStateActionsVisitsMemory().read(StateObservedAction.newFromScalars(20,true,10,CardAction.stick));

        System.out.println("valueStick = " + valueStick+", valueHit = " + valueHit);
        System.out.println("nofVisits = " + nofVisits);

        Assert.assertTrue(valueStick>valueHit);  //sumPlayer*cardDealer*ace*margin
        Assert.assertTrue(nofVisits>10);
    }

    @Test
    public void learnFromMemoryRandomGreedyPolicy() {
        trainMemory(new PolicyGreedyOnStateActionMemory(stateActionValueMemory, PROBABILITY_RANDOM_ACTION));

        double valueStick=stateActionValueMemory.read(StateObservedAction.newFromScalars(21,true,10,CardAction.stick));
        double valueHit=stateActionValueMemory.read(StateObservedAction.newFromScalars(21,true,10,CardAction.hit));

        double nofVisits=learner.getNumberOfStateActionsVisitsMemory().read(StateObservedAction.newFromScalars(20,true,10,CardAction.stick));

        System.out.println("valueStick = " + valueStick+", valueHit = " + valueHit);
        System.out.println("nofVisits = " + nofVisits);

        Assert.assertTrue(valueStick>valueHit);  //sumPlayer*cardDealer*ace*margin
        Assert.assertTrue(nofVisits>10);
    }


    private void trainMemory(PolicyInterface policy) {
        EpisodeRunner episodeRunner=new EpisodeRunner(environment,policy);
        for (int i = 0; i < NOF_EPISODES; i++) {
            StateCards cards = StateCards.newRandomPairs();
            Episode episode=episodeRunner.play(cards);
            returnsForEpisode.clear();
            returnsForEpisode.appendReturns(episode);
            learner.updateMemoryFromEpisodeReturns(returnsForEpisode);
        }
    }

}
