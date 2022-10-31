package black_jack.models;

import black_jack.helper.CardsHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class StateCards {


    List<Card> cardsPlayer;
    List<Card> cardsDealer;

    public static List<Card> newPair(long card1Value, long card2Value)  {
        List<Card> cards=new ArrayList<>();
        cards.add(Card.newWithValue(card1Value));
        cards.add(Card.newWithValue(card2Value));
        return  cards;
    }

    public static StateCards EMPTY() {
        return new StateCards();
    }

    public static StateCards newRandomPairs() {
        StateCards cards=EMPTY();
        cards.cardsPlayer.addAll(drawTwoRandomCards());
        cards.getCardsDealer().addAll(drawTwoRandomCards());
        return cards;
    }


    public StateCards() {
        cardsPlayer=new ArrayList<>();
        cardsDealer=new ArrayList<>();

    }

    public StateCards(List<Card> cardsPlayer, List<Card> cardsDealer) {
        this.cardsPlayer = cardsPlayer;
        this.cardsDealer = cardsDealer;
    }

    public static StateCards clone(StateCards otherCards)  {
        StateCards newCards=EMPTY();
        newCards.cardsPlayer.addAll(new ArrayList<>(otherCards.getCardsPlayer()));
        newCards.cardsDealer.addAll(new ArrayList<>(otherCards.getCardsDealer()));
        return newCards;
    }

    static List<Card> drawTwoRandomCards() {
        List<Card> cards=new ArrayList<>();
        cards.add(Card.newRandom());
        cards.add(Card.newRandom());
        return  cards;
    }

    public StateObserved observeState() {
        return new StateObserved(
                CardsHelper.sumHand(cardsPlayer),
                CardsHelper.usableAce(cardsPlayer),
                cardsDealer.get(0).value
        );
    }

    public void addPlayerCard(Card card)  {
        cardsPlayer.add(card);
    }


    public void addDealerCard(Card card)  {
        cardsDealer.add(card);
    }




}
