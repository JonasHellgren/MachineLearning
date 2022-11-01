package black_jack.helper;

import black_jack.models.Card;
import black_jack.models.StateCards;

import java.util.List;
import java.util.stream.Collectors;

public class CardsInfo {

    public static final int MAX_CARDS_SUM = 21;
    public static final int STRONG_ACE_ADDED_VALUE = 10;
    public static final int ACE_VALUE = 1;
    public static final int BUST_LIMIT = 21;
    public static final long ACE_VALUE_LONG = 1L;
    public static final long FACE_CARD_VALUE = 10L;

    public static long sumHandPlayer(StateCards state) {
        return sumHand(state.getCardsPlayer());
    }

    public static long sumHandDealer(StateCards state) {
        return sumHand(state.getCardsDealer());
    }

    public static  long scoreHandPlayer(StateCards state) {
        return (isPlayerBust(state))
                ? 0
                : sumHandPlayer(state);
    }

    public static  long scoreHandDealer(StateCards state) {
        return (isDealerBust(state))
                ? 0
                : sumHandDealer(state);
    }

    public static long sumHand(List<Card> cards) {
        return (usableAce(cards))
                ? sumCardValues(cards)+STRONG_ACE_ADDED_VALUE
                : sumCardValues(cards);
    }

    public static boolean usableAce(List<Card> cards) {
        boolean isThereAnyAce=cards.stream().anyMatch(c -> c.getValue()== ACE_VALUE);
        boolean sumHandWithUsableAceNotBust=sumCardValues(cards)+ STRONG_ACE_ADDED_VALUE <= MAX_CARDS_SUM;
        return isThereAnyAce && sumHandWithUsableAceNotBust;
    }

    private static long sumCardValues(List<Card> cards) {
        List<Long> numbers= cards.stream().map(c -> c.getValue()).collect(Collectors.toList());
        return numbers.stream().mapToInt(Long::intValue).sum();
    }

    public static boolean isPlayerBust(StateCards state)  {
        return sumHandPlayer(state)> BUST_LIMIT;
    }

    public static boolean isDealerBust(StateCards state)  {
        return sumHandDealer(state)>BUST_LIMIT;
    }

    public static boolean isAceAndFacedCardPresent(List<Card> cards) {
        List<Long> cardValues= cards.stream().map(c -> c.getValue()).collect(Collectors.toList());
        return  cardValues.contains(ACE_VALUE_LONG) && cardValues.contains(FACE_CARD_VALUE);
    }


}
