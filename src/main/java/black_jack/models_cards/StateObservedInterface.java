package black_jack.models_cards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface StateObservedInterface {
    int LOWER_HANDS_SUM_PLAYER = 10;
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

    static Set<StateObservedObserved> allStates() {
        Set<StateObservedObserved> set=new HashSet<>();
        for (long sumHandPlayer: StateObservedInterface.getHandsSumList()) {
            for (long dealerCard: StateObservedInterface.getDealerCardList()) {
                set.add(new StateObservedObserved(sumHandPlayer,false,dealerCard));
                set.add(new StateObservedObserved(sumHandPlayer,true,dealerCard));
            }
        }
        return set;
    }

}
