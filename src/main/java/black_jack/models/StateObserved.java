package black_jack.models;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class StateObserved {
    long sumHandPlayer;  //the players current sum
    boolean playerHasUsableAce; //whether or not the player holds a usable ace (0 or 1).
    long dealerCardValue;  //   the dealer's one showing card (1-10 where 1 is ace),

}
