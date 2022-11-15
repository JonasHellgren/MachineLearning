package black_jack.models_cards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 * StateInterface and it sub classes is not the same as StateCards. StateCards is the raw full information.
 * StateInterface expresses the information given to the player.
 */

public interface StateInterface {
    int LOWER_HANDS_SUM_PLAYER = 12;
    int MAX_HANDS_SUM_PLAYER = 21;
    int MIN_DEALER_CARD = 1;
    int MAX_DEALER_CARD = 10;

    boolean playerHasUsableAce();
    boolean equals(Object obj);
    int hashCode();

    static List<Integer> getHandsSumList() {
        return IntStream.rangeClosed(LOWER_HANDS_SUM_PLAYER, MAX_HANDS_SUM_PLAYER).boxed().collect(Collectors.toList());
    }

    static List<Integer> getDealerCardList() {
        return IntStream.rangeClosed(MIN_DEALER_CARD, MAX_DEALER_CARD).boxed().collect(Collectors.toList());
    }

    static Set<StateObserved> allStates() {
        Set<StateObserved> set=new HashSet<>();
        for (long sumHandPlayer: StateInterface.getHandsSumList()) {
            for (long dealerCard: StateInterface.getDealerCardList()) {
                set.add(new StateObserved(sumHandPlayer,false,dealerCard));
                set.add(new StateObserved(sumHandPlayer,true,dealerCard));
            }
        }
        return set;
    }

}
