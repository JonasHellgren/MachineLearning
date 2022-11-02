package black_jack;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.models.*;
import org.jcodec.common.Assert;
import org.jetbrains.annotations.NotNull;
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

    @NotNull
    private StateCards getGoodCardsForPlayer() {
        List<Card> cardsPlayer = StateCards.newPair(10, 9);
        List<Card> cardsDealer = StateCards.newPair(10, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }

    @NotNull
    private StateCards getVeryBadCardsForPlayer() {
        List<Card> cardsPlayer = StateCards.newPair(5, 2);
        List<Card> cardsDealer = StateCards.newPair(8, 10);
        return new StateCards(cardsPlayer, cardsDealer);
    }


}
