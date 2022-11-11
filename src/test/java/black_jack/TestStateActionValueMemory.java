package black_jack;

import black_jack.enums.CardAction;
import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.LearnerAbstract;
import black_jack.helper.LearnerStateActionValue;
import black_jack.models_cards.StateActionObserved;
import black_jack.models_cards.StateCards;
import black_jack.models_episode.Episode;
import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.policies.PolicyHitBelow20;
import black_jack.policies.PolicyInterface;
import black_jack.policies.PolicyRandom;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestStateActionValueMemory {

    public static final double DELTA = 0.1;
    public static final int NOF_UPDATES = 10_000;
    public static final double ALPHA = 0.01;
    private static final double ZERO_REWARD = 0d;
    private static final double ONE_REWARD = 1d;
    boolean regardNofVisitsFlag=true;
    public static final int NOF_EPISODES = 100_000;

    EnvironmentInterface environment;
    Episode episode;
    ReturnsForEpisode returnsForEpisode;
    StateActionValueMemory stateActionValueMemory;
    NumberOfStateVisitsMemory numberOfStateVisitsMemory;
    LearnerAbstract learner;

    @Before
    public void init() {
        environment=new BlackJackEnvironment();
        episode = new Episode();
        returnsForEpisode = new ReturnsForEpisode();
        stateActionValueMemory = new StateActionValueMemory();
        numberOfStateVisitsMemory =new NumberOfStateVisitsMemory();
        learner = new LearnerStateActionValue(stateActionValueMemory, numberOfStateVisitsMemory,ALPHA,regardNofVisitsFlag);

    }

    @Test
    public void learnMemoryFromOneEpisode() {

        StateActionObserved s1 = StateActionObserved.newStateAction(2, false, 17,CardAction.hit);
        StateActionObserved s2 = StateActionObserved.newStateAction(5, false, 17,CardAction.hit);
        StateActionObserved s3 = StateActionObserved.newStateAction(12, false, 17,CardAction.hit);
        StateActionObserved s4 = StateActionObserved.newStateAction(21, false, 17,CardAction.stick);

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
        System.out.println("numberOfVisitsMemory.average() = " + numberOfStateVisitsMemory.average());
        System.out.println("valueMemory = " + stateActionValueMemory);

        Assert.assertTrue(stateActionValueMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
        Assert.assertTrue(numberOfStateVisitsMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
    }

    @Test
    public void learnForSumPlayer21StickIsBetterThanHit() {
        trainMemory(new PolicyRandom());  //todo PolicyRandom

        double valueStick=stateActionValueMemory.read(StateActionObserved.newStateAction(21,true,10,CardAction.stick));
        double valueHit=stateActionValueMemory.read(StateActionObserved.newStateAction(21,true,10,CardAction.hit));

        System.out.println("valueStick = " + valueStick+", valueHit = " + valueHit);
        learner.getNumberOfStateVisitsMemory().read(StateActionObserved.newStateAction(20,true,10,CardAction.stick))

        Assert.assertTrue(valueStick>valueHit);  //sumPlayer*cardDealer*ace*margin
  //      Assert.assertTrue(numberOfVisitsMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
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
