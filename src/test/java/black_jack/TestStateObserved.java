package black_jack;

import black_jack.models.Card;
import black_jack.models.StateCards;
import black_jack.models.StateObserved;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestStateObserved {

    @Test
    public void testCards1() {
        StateCards cards = cards1();
        StateObserved observed = cards.observeState();

        System.out.println("observed = " + observed);

        Assert.assertEquals(19, observed.sumHandPlayer);
        Assert.assertFalse(observed.playerHasUsableAce);
        Assert.assertEquals(1, observed.dealerCardValue);
    }


    @Test
    public void testCards2() {
        StateCards cards = cards2();
        StateObserved observed = cards.observeState();

        System.out.println("observed = " + observed);

        Assert.assertEquals(20, observed.sumHandPlayer);
        Assert.assertTrue(observed.playerHasUsableAce);
        Assert.assertEquals(5, observed.dealerCardValue);
    }


    @NotNull
    private StateCards cards1() {
        List<Card> cardsPlayer = StateCards.newPair(10, 9);
        List<Card> cardsDealer = StateCards.newPair(1, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }


    @NotNull
    private StateCards cards2() {
        List<Card> cardsPlayer = StateCards.newPair(1, 9);
        List<Card> cardsDealer = StateCards.newPair(5, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }

}
