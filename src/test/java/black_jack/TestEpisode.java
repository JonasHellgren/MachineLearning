package black_jack;

import black_jack.enums.CardAction;
import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.environment.StepReturnBJ;
import black_jack.models_cards.*;
import black_jack.models_episode.Episode;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestEpisode {

    EnvironmentInterface environment;
    Episode episode;

    @Before
    public void init() {
        environment=new BlackJackEnvironment();
        episode=new Episode();
    }

    @Test
    public void generateEpisodeFromHittingGoodPlayerCards() {
        StateCards cards= getGoodCardsForPlayer();

        boolean stopPlaying;
        do {
            CardAction action=CardAction.hit;
            StepReturnBJ returnBJ= environment.step(action,cards);
            stopPlaying= returnBJ.stopPlaying;
            episode.add(cards.observeState(), action, returnBJ.reward);
            cards.copy(returnBJ);

        } while (!stopPlaying);

        System.out.println("episode = " + episode);

        Assert.assertTrue(episode.nofItems()>0);
    }

    @Test public void testIsStatePresentBeforeTimeStep() {
        StateObservedObserved s1=new StateObservedObserved(2,false,17);
        StateObservedObserved s2=new StateObservedObserved(5,false,17);
        StateObservedObserved s3=new StateObservedObserved(12,false,17);
        StateObservedObserved s4=new StateObservedObserved(21,false,17);

        episode.add(s1,CardAction.hit,0d);
        episode.add(s2,CardAction.hit,0d);
        episode.add(s3,CardAction.hit,0d);
        episode.add(s4,CardAction.stick,1d);

        Assert.assertTrue(episode.isStatePresentBeforeTimeStep(s1,3));
        Assert.assertFalse(episode.isStatePresentBeforeTimeStep(s4,1));

    }

    @NotNull
    private StateCards getGoodCardsForPlayer() {
        List<Card> cardsPlayer = StateCards.newPair(10, 9);
        List<Card> cardsDealer = StateCards.newPair(10, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }




}
