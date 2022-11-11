package black_jack.models_cards;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 *
 * https://www.baeldung.com/java-hashcode
 * (at)EqualsAndHashCode is candidate solution to equals and hashCode
 */

@AllArgsConstructor
@ToString
public class StateObserved {
    public static final int LOWER_HANDS_SUM_PLAYER = 10;
    public static final int MAX_HANDS_SUM_PLAYER = 21;
    public static final int MIN_DEALER_CARD = 1;
    public static final int MAX_DEALER_CARD = 10;

    public long sumHandPlayer;  //the players current sum
    public boolean playerHasUsableAce; //whether or not the player holds a usable ace (0 or 1).
    public long dealerCardValue;  //   the dealer's one showing card (1-10 where 1 is ace),

    @Override
    public boolean equals(Object obj) {
        //check if the argument is a reference to this object
        if (obj == this) return true;

        //check if the argument has the correct typ
        if (!(obj instanceof StateObserved)) return false;

        //For each significant field in the class, check if that field matches the corresponding field of this object
        StateObserved otherState = (StateObserved) obj;
        return otherState.sumHandPlayer == this.sumHandPlayer &&
                otherState.playerHasUsableAce == this.playerHasUsableAce &&
                otherState.dealerCardValue == this.dealerCardValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sumHandPlayer, playerHasUsableAce,dealerCardValue);
    }

    public static Set<StateObserved> allStates() {
        Set<StateObserved> set=new HashSet<>();
        for (long sumHandPlayer:getHandsSumList()) {
            for (long dealerCard:getDealerCardList()) {
                set.add(new StateObserved(sumHandPlayer,false,dealerCard));
                set.add(new StateObserved(sumHandPlayer,true,dealerCard));
            }
        }
        return set;
    }

    @NotNull
    public static List<Integer> getHandsSumList() {
        return IntStream.rangeClosed(LOWER_HANDS_SUM_PLAYER, MAX_HANDS_SUM_PLAYER).boxed().collect(Collectors.toList());
    }

    @NotNull
    public static List<Integer> getDealerCardList() {
        return IntStream.rangeClosed(MIN_DEALER_CARD, MAX_DEALER_CARD).boxed().collect(Collectors.toList());
    }

}
