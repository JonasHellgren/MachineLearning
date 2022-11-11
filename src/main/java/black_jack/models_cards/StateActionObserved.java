package black_jack.models_cards;

import black_jack.enums.CardAction;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@AllArgsConstructor
@ToString
public class StateActionObserved {

    StateObserved stateObserved;
    CardAction cardAction;

    @Override
    public int hashCode() {
        return Objects.hash(
                stateObserved.sumHandPlayer,
                stateObserved.playerHasUsableAce,
                stateObserved.dealerCardValue,
                cardAction);
    }

}
