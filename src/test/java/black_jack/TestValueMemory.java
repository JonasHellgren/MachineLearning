package black_jack;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.Learner;
import black_jack.models.*;
import black_jack.policies.RuleBasedPolicies;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestValueMemory {
    public static final double DELTA = 0.1;
    public static final int NOF_UPDATES = 100;
    public static final double ALPHA = 0.1;
    public static final int NOF_EPISODES = 1_000_000;

    EnvironmentInterface environment;
    Episode episode;
    ReturnsForEpisode returnsForEpisode;
    ValueMemory valueMemory;
    NumberOfVisitsMemory numberOfVisitsMemory;
    Learner learner;

    @Before
    public void init() {
        environment=new BlackJackEnvironment();
        episode = new Episode();
        returnsForEpisode = new ReturnsForEpisode();
        valueMemory = new ValueMemory();
        numberOfVisitsMemory=new NumberOfVisitsMemory();
        learner = new Learner(valueMemory,numberOfVisitsMemory,ALPHA);
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

        for (int i = 0; i < NOF_EPISODES; i++) {
            StateCards cards = StateCards.newRandomPairs();
            Episode episode=runEpisode(cards);
            returnsForEpisode.clear();
            returnsForEpisode.appendReturns(episode);
            learner.updateMemory(returnsForEpisode);
        }

        System.out.println("valueMemory = " + valueMemory);

    }

    private Episode runEpisode(StateCards cards) {
        Episode episode = new Episode();
        boolean stopPlaying;
        do {
            CardAction action = RuleBasedPolicies.hitBelow20(cards.observeState());
            StepReturnBJ returnBJ = environment.step(action, cards);
            stopPlaying= returnBJ.stopPlaying;
            episode.add(cards.observeState(), action, returnBJ.reward);
            cards.copy(returnBJ);
        } while (!stopPlaying);
        return episode;
    }
}
