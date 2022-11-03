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

    @Test public void testEquals1() {
        StateObserved s1=new StateObserved(12,false,17);
        StateObserved s2=new StateObserved(12,false,17);
        Assert.assertTrue(s1.equals(s2));
    }


    @Test public void testEquals2() {
        StateObserved s1=new StateObserved(12,false,17);
        StateObserved s2=new StateObserved(11,false,17);
        Assert.assertFalse(s1.equals(s2));
    }

    @Test public void testDifferentHashCode() {
        StateCards cards1 = cards1();
        StateCards cards2 = cards2();

        System.out.println("cards1.observeState().hashCode() = " + cards1.observeState().hashCode());

        Assert.assertNotEquals(cards1.observeState().hashCode(),cards2.observeState().hashCode());


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
