package black_jack.models_cards;

import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.Objects;

/***
 *
 * https://www.baeldung.com/java-hashcode
 * (at)EqualsAndHashCode is candidate solution to equals and hashCode
 */

@AllArgsConstructor
@ToString
public class StateObservedObserved implements StateObservedInterface {


    public long sumHandPlayer;  //the players current sum
    public boolean playerHasUsableAce; //whether or not the player holds a usable ace (0 or 1).
    public long dealerCardValue;  //   the dealer's one showing card (1-10 where 1 is ace),

    @Override
    public boolean equals(Object obj) {
        //check if the argument is a reference to this object
        if (obj == this) return true;

        //check if the argument has the correct typ
        if (!(obj instanceof StateObservedObserved)) return false;

        //For each significant field in the class, check if that field matches the corresponding field of this object
        StateObservedObserved otherState = (StateObservedObserved) obj;
        return otherState.sumHandPlayer == this.sumHandPlayer &&
                otherState.playerHasUsableAce == this.playerHasUsableAce &&
                otherState.dealerCardValue == this.dealerCardValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sumHandPlayer, playerHasUsableAce,dealerCardValue);
    }

    public boolean playerHasUsableAce() {
        return playerHasUsableAce;
    }
}
