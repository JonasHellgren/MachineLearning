package black_jack.models_cards;

import black_jack.enums.CardAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@AllArgsConstructor
@ToString
@Getter
public class StateObservedAction implements StateInterface {

    StateObserved stateObserved;
    CardAction cardAction;

    public static StateObservedAction newFromScalars(long sumHandPlayer,
                                                     boolean playerHasUsableAce,
                                                     long dealerCardValue,
                                                     CardAction cardAction) {
        return new StateObservedAction(new StateObserved(sumHandPlayer, playerHasUsableAce, dealerCardValue),cardAction);
    }

    public static StateObservedAction newFromStateAndAction(StateObserved state,
                                                            CardAction cardAction) {
        return new StateObservedAction(state,cardAction);
    }

    @Override
    public boolean equals(Object obj) {
        //check if the argument is a reference to this object
        if (obj == this) return true;

        //check if the argument has the correct typ
        if (!(obj instanceof StateObservedAction)) return false;

        //For each significant field in the class, check if that field matches the corresponding field of this object
        StateObservedAction otherState = (StateObservedAction) obj;
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

    @Override
    public boolean playerHasUsableAce() {
        return stateObserved.playerHasUsableAce;
    }
}
