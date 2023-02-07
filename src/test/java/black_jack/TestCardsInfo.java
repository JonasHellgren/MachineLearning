package black_jack;

import black_jack.helper.CardsInfo;
import black_jack.models_cards.Card;
import black_jack.models_cards.StateCards;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestCardsInfo {

    @Test public void sumHandPlayer() {
        StateCards cards= getCards();
        Assert.assertEquals(19,CardsInfo.sumHandPlayer(cards));
    }


    @Test public void sumHandDealer() {
        StateCards cards= getCards();
        Assert.assertEquals(18,CardsInfo.sumHandDealer(cards));
    }

    @Test public void scoreHandPlayer() {
        StateCards cards= getCards();
        Assert.assertEquals(19,CardsInfo.scoreHandPlayer(cards));
    }

    @Test public void scoreHandDealer() {
        StateCards cards= getCards();
        Assert.assertEquals(18,CardsInfo.scoreHandDealer(cards));
    }

    @Test public void scoreHandPlayerBust() {
        StateCards cards= getCards();
        cards.addPlayerCard(Card.newWithValue(10));
        Assert.assertEquals(0,CardsInfo.scoreHandPlayer(cards));
    }


    @Test public void scoreDealerPlayerBust() {
        StateCards cards= getCards();
        cards.addDealerCard(Card.newWithValue(10));
        cards.addDealerCard(Card.newWithValue(10));
        Assert.assertEquals(0,CardsInfo.scoreHandDealer(cards));
    }

    @Test
    public void sumHand()  {
        Assert.assertEquals(19, CardsInfo.sumHand(StateCards.newPair(10, 9)));
        Assert.assertEquals(21, CardsInfo.sumHand(StateCards.newPair(10, 1)));   //usable ace

        StateCards threeCards = getCards();
        threeCards.addPlayerCard(Card.newWithValue(1));
        Assert.assertEquals(10+9+1, CardsInfo.sumHand(threeCards.getCardsPlayer()));   //no usable ace for player

    }

    @Test public void usableAce()  {
        StateCards cards = getCards();
        cards.addPlayerCard(Card.newWithValue(1));
        Assert.assertFalse(CardsInfo.usableAce(cards.getCardsPlayer()));   //no usable ace for player
        Assert.assertTrue(CardsInfo.usableAce(cards.getCardsDealer()));   //usable ace for dealer

    }

    @Test public void isPlayerBust()  {
        StateCards cards = getCards();
        Assert.assertFalse(CardsInfo.isPlayerBust(cards));
        cards.addPlayerCard(Card.newWithValue(5));
        Assert.assertTrue(CardsInfo.isPlayerBust(cards));
    }

    @Test public void isDealerBust()  {
        StateCards cards = getCards();
        Assert.assertFalse(CardsInfo.isDealerBust(cards));
        cards.addDealerCard(Card.newWithValue(10));
        cards.addDealerCard(Card.newWithValue(10));
        Assert.assertTrue(CardsInfo.isDealerBust(cards));
    }

      private StateCards getCards() {
        List<Card> cardsPlayer = StateCards.newPair(10, 9);
        List<Card> cardsDealer = StateCards.newPair(1, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }



}
