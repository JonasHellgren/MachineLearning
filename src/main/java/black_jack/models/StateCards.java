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

    public StateCards() {
        cardsPlayer=drawTwoRandomCards();
        cardsDealer=drawTwoRandomCards();
    }

    public StateCards(List<Card> cardsPlayer, List<Card> cardsDealer) {
        this.cardsPlayer = cardsPlayer;
        this.cardsDealer = cardsDealer;
    }

    List<Card> drawTwoRandomCards() {
        return Arrays.asList(new Card(),new Card());
    }

    StateObserved observeState() {
        return new StateObserved(
                sumHand(cardsPlayer),
                usableAce(cardsPlayer),
                cardsPlayer.get(0).value
        );
    }

    public long sumHand(List<Card> cards) {
        return (usableAce(cards))
                ? sumCardValues(cards)
                : sumCardValues(cards)+10;
    }

    public boolean usableAce(List<Card> cards) {
        boolean isThereAnyAce=cards.stream().anyMatch(c -> c.value== ACE_VALUE);
        boolean sumHandWithUsableAceNotBust=sumCardValues(cards)+ STRONG_ACE_ADDED_VALUE <= MAX_CARDS_SUM;
        return isThereAnyAce && sumHandWithUsableAceNotBust;
    }

    public long sumCardValues(List<Card> cards) {
        List<Long> numbers= cards.stream().map(c -> c.value).collect(Collectors.toList());
        return numbers.stream().mapToInt(Long::intValue).sum();
    }

}
