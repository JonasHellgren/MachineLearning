package black_jack;

import black_jack.models.Card;
import black_jack.models.StateCards;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TestStateCards {


    @Test
    public void createCard() {
        Card card=new Card(1);
        System.out.println("card = " + card);
        Assert.assertEquals(1,card.getValue());
    }

    @Test public void createTwoRandomHands() {
        StateCards dealerAndPlayerCards=new StateCards();
        System.out.println("dealerAndPlayerCards = " + dealerAndPlayerCards);
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsDealer().size());
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsPlayer().size());
    }

    @Test public void createPlayerHandFaceCardAnd9DealerHandAceAnd7() {

        List<Card> cardsPlayer= Arrays.asList(new Card(10), new Card(9));  //todo, snyggare med static constructor
        List<Card> cardsDealer= Arrays.asList(new Card(1), new Card(7));

        StateCards dealerAndPlayerCards=new StateCards(cardsPlayer,cardsDealer);
        System.out.println("dealerAndPlayerCards = " + dealerAndPlayerCards);
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsDealer().size());
        Assert.assertEquals(2,dealerAndPlayerCards.getCardsPlayer().size());
    }

}
