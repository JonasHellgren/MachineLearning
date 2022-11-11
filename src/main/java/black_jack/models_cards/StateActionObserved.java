package black_jack.models_cards;

import black_jack.enums.CardAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@AllArgsConstructor
@ToString
@Getter
public class StateActionObserved {

    StateObserved stateObserved;
    CardAction cardAction;

    public static StateActionObserved newStateAction(long sumHandPlayer,
                                                     boolean playerHasUsableAce,
                                                     long dealerCardValue,
                                                     CardAction cardAction) {
        return new StateActionObserved(new StateObserved(sumHandPlayer, playerHasUsableAce, dealerCardValue),cardAction);
    }

    @Override
    public boolean equals(Object obj) {
        //check if the argument is a reference to this object
        if (obj == this) return true;

        //check if the argument has the correct typ
        if (!(obj instanceof StateActionObserved)) return false;

        //For each significant field in the class, check if that field matches the corresponding field of this object
        StateActionObserved otherState = (StateActionObserved) obj;
        return  otherState.stateObserved.equals(this.stateObserved) &&
                otherState.cardAction == this.cardAction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                stateObserved.sumHandPlayer,
                stateObserved.playerHasUsableAce,
                stateObserved.dealerCardValue,
                cardAction);
    }

}
