package black_jack.models_returns;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateObserved;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ReturnItem {
    public StateObserved state;
    public CardAction cardAction;
    public Double returnValue;
}
