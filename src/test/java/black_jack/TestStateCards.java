package black_jack;

import black_jack.models.Card;
import black_jack.models.StateCards;
import black_jack.models.StateObserved;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestStateCards {


    @Test
    public void createCard() {
        Card card=Card.newWithValue(1);
        System.out.println("card = " + card);
        Assert.assertEquals(1,card.getValue());
    }

    @Test public void createTwoRandomHands() {
        StateCards dealerAndPlayerCards=StateCards.newRandomPairs() ;
        System.out.println("dealerAndPlayerCards = " + dealerAndPlayerCards);
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsDealer().size());
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsPlayer().size());
    }

    @Test public void createPlayerHandFaceCardAnd9DealerHandAceAnd7() {
        StateCards dealerAndPlayerCards = getStateCards();

        System.out.println("dealerAndPlayerCards = " + dealerAndPlayerCards);
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsDealer().size());
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsPlayer().size());
    }



    @Test public void observeState() {
        StateCards dealerAndPlayerCards = getStateCards();
        StateObserved obs=dealerAndPlayerCards.observeState();

        System.out.println("obs = " + obs);

        Assert.assertEquals(19,obs.sumHandPlayer);
        Assert.assertFalse(obs.playerHasUsableAce);
        Assert.assertEquals(1,obs.dealerCardValue);
    }

    @Test public void sumHand()  {
        Assert.assertEquals(19,StateCards.sumHand(StateCards.newPair(10, 9)));
        Assert.assertEquals(21,StateCards.sumHand(StateCards.newPair(10, 1)));   //usable ace

        StateCards threeCards = getStateCards();
        threeCards.addPlayerCard(Card.newWithValue(1));
        Assert.assertEquals(10+9+1,StateCards.sumHand(threeCards.getCardsPlayer()));   //no usable ace for player

    }

    @Test public void usableAce()  {
        Assert.assertEquals(19,StateCards.sumHand(StateCards.newPair(10, 9)));
        Assert.assertEquals(21,StateCards.sumHand(StateCards.newPair(10, 1)));   //usable ace

        StateCards threeCards = getStateCards();
        threeCards.addPlayerCard(Card.newWithValue(1));
        Assert.assertFalse(StateCards.usableAce(threeCards.getCardsPlayer()));   //no usable ace for player
        Assert.assertTrue(StateCards.usableAce(threeCards.getCardsDealer()));   //usable ace for dealer

    }


    @NotNull
    private StateCards getStateCards() {
        List<Card> cardsPlayer = StateCards.newPair(10, 9);
        List<Card> cardsDealer = StateCards.newPair(1, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }

    @NotNull
    private StateCards getStateCardsUcableAce() {
        List<Card> cardsPlayer = StateCards.newPair(1, 10);
        List<Card> cardsDealer = StateCards.newPair(1, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }

}
