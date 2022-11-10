package black_jack;

import black_jack.enums.CardAction;
import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.Learner;
import black_jack.models_cards.*;
import black_jack.models_episode.Episode;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.models_memory.NumberOfVisitsMemory;
import black_jack.models_memory.ValueMemory;
import black_jack.policies.HitBelow20Policy;
import black_jack.policies.PolicyInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestValueMemory {
    public static final double DELTA = 0.1;
    public static final int NOF_UPDATES = 1000;
    public static final double ALPHA = 0.01;
    boolean regardNofVisitsFlag=true;
    public static final int NOF_EPISODES = 100_000;

    EnvironmentInterface environment;
    Episode episode;
    ReturnsForEpisode returnsForEpisode;
    ValueMemory valueMemory;
    NumberOfVisitsMemory numberOfVisitsMemory;
    Learner learner;
    PolicyInterface policy;

    @Before
    public void init() {
        environment=new BlackJackEnvironment();
        episode = new Episode();
        returnsForEpisode = new ReturnsForEpisode();
        valueMemory = new ValueMemory();
        numberOfVisitsMemory=new NumberOfVisitsMemory();
        learner = new Learner(valueMemory,numberOfVisitsMemory,ALPHA,regardNofVisitsFlag);
        policy = new HitBelow20Policy();
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

        for (int i = 0; i < NOF_UPDATES; i++) {
            learner.updateMemory(returnsForEpisode);
        }

        System.out.println("valueMemory = " + valueMemory);

        Assert.assertEquals(4, valueMemory.nofItems());
        Assert.assertEquals(1, valueMemory.read(s1), DELTA);
        Assert.assertEquals(1, valueMemory.read(s2), DELTA);

    }

    @Test
    public void learnMemoryFromManyEpisodes() {

        EpisodeRunner episodeRunner=new EpisodeRunner(environment,policy);
        for (int i = 0; i < NOF_EPISODES; i++) {
            StateCards cards = StateCards.newRandomPairs();
            Episode episode=episodeRunner.play(cards);
            returnsForEpisode.clear();
            returnsForEpisode.appendReturns(episode);
            learner.updateMemory(returnsForEpisode);
        }

        //System.out.println("valueMemory = " + valueMemory);

        System.out.println("valueMemory.nofItems() = " + valueMemory.nofItems());
        System.out.println("numberOfVisitsMemory.nofItems() = " + numberOfVisitsMemory.nofItems());
        System.out.println("valueMemory.average() = " + valueMemory.average());
        System.out.println("numberOfVisitsMemory.average() = " + numberOfVisitsMemory.average());

        Assert.assertTrue(valueMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
        Assert.assertTrue(numberOfVisitsMemory.nofItems()>15*10*2*0.5);  //sumPlayer*cardDealer*ace*margin
    }

}
