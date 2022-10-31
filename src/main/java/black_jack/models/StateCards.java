package black_jack.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
public class StateCards {

    public static final int MAX_CARDS_SUM = 21;
    public static final int STRONG_ACE_ADDED_VALUE = 10;
    public static final int ACE_VALUE = 1;
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
                sumHand(cardsPlayer),
                usableAce(cardsPlayer),
                cardsDealer.get(0).value
        );
    }

    public void addPlayerCard(Card card)  {
        cardsPlayer.add(card);
    }


    public void addDealerCard(Card card)  {
        cardsDealer.add(card);
    }


    public long sumHandPlayer() {
        return sumHand(cardsPlayer);
    }



    public long sumHandDealer() {
        return sumHand(cardsDealer);
    }

    public static long sumHand(List<Card> cards) {
        return (usableAce(cards))
                ? sumCardValues(cards)+STRONG_ACE_ADDED_VALUE
                : sumCardValues(cards);
    }

    public static boolean usableAce(List<Card> cards) {
        boolean isThereAnyAce=cards.stream().anyMatch(c -> c.value== ACE_VALUE);
        boolean sumHandWithUsableAceNotBust=sumCardValues(cards)+ STRONG_ACE_ADDED_VALUE <= MAX_CARDS_SUM;
        return isThereAnyAce && sumHandWithUsableAceNotBust;
    }

    private static long sumCardValues(List<Card> cards) {
        List<Long> numbers= cards.stream().map(c -> c.value).collect(Collectors.toList());
        return numbers.stream().mapToInt(Long::intValue).sum();
    }

}
