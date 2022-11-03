package black_jack.models;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class StateObserved {
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

}
