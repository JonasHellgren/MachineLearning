package black_jack;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.models_cards.Card;
import black_jack.enums.CardAction;
import black_jack.models_cards.StateCards;
import black_jack.environment.StepReturnBJ;
import org.junit.*;

import java.util.List;

public class TestEnvironment {

    public static final int NOF_PLAYS = 1000;
    EnvironmentInterface environment=BlackJackEnvironment.newHigherRewardIfAceAndFaceCard();

    @Test
    public void mostlyBustWhenStartingAt19AndHitting () {

        int nofBusts=0;
        for (int i = 0; i < NOF_PLAYS; i++) {
            StateCards cards= getGoodCardsForPlayer();
            StepReturnBJ returnBJ= environment.step(CardAction.hit,cards);
            nofBusts = returnBJ.stopPlaying ? nofBusts+1: nofBusts;
        }

        System.out.println("nofBusts = " + nofBusts);
        Assert.assertTrue(nofBusts>NOF_PLAYS-nofBusts);
    }

    @Test public void nofPlayerCardsSomeTimesHigherThanTwoWhenHittingManyTimes() {
        int nofPlayerCardsMax=0;
        for (int i = 0; i < NOF_PLAYS; i++) {
            StateCards cards= getGoodCardsForPlayer();
            StepReturnBJ returnBJ= environment.step(CardAction.hit,cards);
            nofPlayerCardsMax=Math.max(nofPlayerCardsMax,returnBJ.cards.getCardsPlayer().size());
        }
        System.out.println("nofPlayerCardsMax = " + nofPlayerCardsMax);
        Assert.assertTrue(nofPlayerCardsMax>2);
    }

    @Test public void playerMostlyWinningByStickingWithGoodCards() {
        int nofPlayerWins=0;
        for (int i = 0; i < NOF_PLAYS; i++) {
            StateCards cards= getGoodCardsForPlayer();
            StepReturnBJ returnBJ= environment.step(CardAction.stick,cards);
            nofPlayerWins=(returnBJ.reward>0) ?nofPlayerWins+1:nofPlayerWins;
        }

        System.out.println("nofPlayerWins = " + nofPlayerWins);
        Assert.assertTrue(nofPlayerWins>NOF_PLAYS-nofPlayerWins);
    }


    @Test public void playerAlwaysLooseByStickingWithVeryBadCards() {
        int nofPlayerWins=0;
        for (int i = 0; i < NOF_PLAYS; i++) {
            StateCards cards= getVeryBadCardsForPlayer();
            StepReturnBJ returnBJ= environment.step(CardAction.stick,cards);
            nofPlayerWins=(returnBJ.reward>0) ?nofPlayerWins+1:nofPlayerWins;
        }

        System.out.println("nofPlayerWins = " + nofPlayerWins);
        Assert.assertEquals(0,nofPlayerWins);
    }

    @Test public void playerMostlyLooseByStickingWithBadCards() {
        int nofPlayerWins=0;
        for (int i = 0; i < NOF_PLAYS; i++) {
            StateCards cards= getBadCardsForPlayer();
            StepReturnBJ returnBJ= environment.step(CardAction.stick,cards);
            nofPlayerWins=(returnBJ.reward>0) ?nofPlayerWins+1:nofPlayerWins;
        }

        System.out.println("nofPlayerWins = " + nofPlayerWins);
        Assert.assertTrue(nofPlayerWins<NOF_PLAYS-nofPlayerWins);
    }

    @Test public void playerMostlyLooseByStickingWhenDealerSumIs11() {
        int nofPlayerWins=0;
        for (int i = 0; i < NOF_PLAYS; i++) {
            StateCards cards= getGoodCardsForDealerSumIs11();
            StepReturnBJ returnBJ= environment.step(CardAction.stick,cards);
            nofPlayerWins=(returnBJ.reward>0) ?nofPlayerWins+1:nofPlayerWins;
        }

        System.out.println("nofPlayerWins = " + nofPlayerWins);
        Assert.assertTrue(nofPlayerWins<NOF_PLAYS-nofPlayerWins);
    }

    private StateCards getGoodCardsForPlayer() {
        List<Card> cardsPlayer = StateCards.newPair(10, 9);
        List<Card> cardsDealer = StateCards.newPair(10, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }


    private StateCards getVeryBadCardsForPlayer() {
        List<Card> cardsPlayer = StateCards.newPair(5, 2);
        List<Card> cardsDealer = StateCards.newPair(8, 10);
        return new StateCards(cardsPlayer, cardsDealer);
    }

    private StateCards getBadCardsForPlayer() {
        List<Card> cardsPlayer = StateCards.newPair(5, 5);
        List<Card> cardsDealer = StateCards.newPair(8, 3);
        return new StateCards(cardsPlayer, cardsDealer);
    }


    private StateCards getGoodCardsForDealerSumIs11() {
        List<Card> cardsPlayer = StateCards.newPair(7, 7);
        List<Card> cardsDealer = StateCards.newPair(5, 6);
        return new StateCards(cardsPlayer, cardsDealer);
    }

}
