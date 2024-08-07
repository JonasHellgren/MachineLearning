package black_jack;

import black_jack.enums.CardAction;
import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.environment.StepReturnBJ;
import black_jack.models_cards.*;
import black_jack.models_episode.Episode;
import black_jack.models_episode.EpisodeItem;
import black_jack.policies.PolicyHitBelow20;
import black_jack.policies.PolicyInterface;
import org.jcodec.common.Assert;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestRuleBasedPolicies {


    public static final int NOF_RUNS = 1000;
    EnvironmentInterface environment;
    Episode episode;
    PolicyInterface policy;

    @Before
    public void init() {
        environment=new BlackJackEnvironment();
        episode=new Episode();
        policy=new PolicyHitBelow20();
    }

    @Test public void hitBelow20() {
        StateCards cards= StateCards.newRandomPairs();
        cards.setCardsPlayer(StateCards.newPair(5,5));
        Assert.assertTrue(policy.hitOrStick(cards.observeState()).equals(CardAction.hit));
    }


    @Test public void stickAt20() {
        StateCards cards= StateCards.newRandomPairs();
        cards.setCardsPlayer(StateCards.newPair(10,10));
        Assert.assertTrue(policy.hitOrStick(cards.observeState()).equals(CardAction.stick));
    }

    @Test
    public void generateEpisodeFromHitBelow20() {
        StateCards cards= getGoodCardsForPlayer();
        runEpisode(cards);
        System.out.println("episode = " + episode);
        Assert.assertTrue(episode.nofItems()>0);
    }


    @Test
    public void manyEpisodesShouldShowPlayerMostlyWins() {

        int nofWins=0;
        for (int i = 0; i < NOF_RUNS; i++) {
            StateCards cards = getGoodCardsForPlayer();
            runEpisode(cards);
            EpisodeItem endItem= episode.getEndItem();
            if ((endItem.reward > 0)) {
                nofWins++;
            }
        }

        System.out.println("nofWins = " + nofWins);

        Assert.assertTrue(nofWins>NOF_RUNS-nofWins);
    }

    private void runEpisode(StateCards cards) {
        episode.clear();
        boolean stopPlaying;
        do {
            CardAction action = policy.hitOrStick(cards.observeState());
            StepReturnBJ returnBJ = environment.step(action, cards);
            stopPlaying= returnBJ.stopPlaying;
            episode.add(cards.observeState(), action, returnBJ.reward);
            cards.copy(returnBJ);

        } while (!stopPlaying);
    }

    @NotNull
    private StateCards getGoodCardsForPlayer() {
        List<Card> cardsPlayer = StateCards.newPair(10, 10);
        List<Card> cardsDealer = StateCards.newPair(10, 6);
        return new StateCards(cardsPlayer, cardsDealer);
    }

}
