package black_jack;

import black_jack.models_cards.Card;
import black_jack.models_cards.StateCards;
import black_jack.models_cards.StateObservedObserved;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestStateCards {


    @Test
    public void createCard() {
        Card card=Card.newWithValue(1);
        System.out.println("card = " + card);
        Assert.assertEquals(1,card.getValue());
    }

    @Test public void EMPTY() {
        StateCards cards=StateCards.EMPTY();
        Assert.assertTrue(cards.getCardsPlayer().isEmpty());
        Assert.assertTrue(cards.getCardsDealer().isEmpty());
    }

    @Test public void createTwoRandomHands() {
        StateCards dealerAndPlayerCards=StateCards.newRandomPairs() ;
        System.out.println("dealerAndPlayerCards = " + dealerAndPlayerCards);
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsDealer().size());
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsPlayer().size());
    }

    @Test public void createPlayerHandFaceCardAnd9DealerHandAceAnd7() {
        StateCards dealerAndPlayerCards = getCards();

        System.out.println("dealerAndPlayerCards = " + dealerAndPlayerCards);
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsDealer().size());
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsPlayer().size());
    }


    @Test public void cloneCards() {
        StateCards cards= getCards();
        StateCards cardsCopy=StateCards.clone(cards);

        for (int i = 0; i < cards.getCardsPlayer().size() ; i++) {
            Assert.assertEquals(cards.getCardsDealer().get(i).getValue(),cardsCopy.getCardsDealer().get(i).getValue());
            Assert.assertEquals(cards.getCardsPlayer().get(i).getValue(),cardsCopy.getCardsPlayer().get(i).getValue());
        }
    }

    @Test public void copyCards() {
        StateCards cards1=getCards();
        cards1.setCardsPlayer(StateCards.newPair(1, 2));
        StateCards cards2=getCards();

        cards2.copy(cards1);

        System.out.println("cards2 = " + cards2);

        Assert.assertEquals(1,cards2.getCardsPlayer().get(0).getValue());
        Assert.assertEquals(2,cards2.getCardsPlayer().get(1).getValue());
    }

    @Test public void observeState() {
        StateCards dealerAndPlayerCards = getCards();
        StateObservedObserved obs=dealerAndPlayerCards.observeState();

        System.out.println("obs = " + obs);

        Assert.assertEquals(19,obs.sumHandPlayer);
        Assert.assertFalse(obs.playerHasUsableAce);
        Assert.assertEquals(1,obs.dealerCardValue);
    }


    @NotNull
    private StateCards getCards() {
        List<Card> cardsPlayer = StateCards.newPair(10, 9);
        List<Card> cardsDealer = StateCards.newPair(1, 7);
        return new StateCards(cardsPlayer, cardsDealer);
    }


}
