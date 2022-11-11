package black_jack.policies;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateObservedObserved;

public interface PolicyInterface {

    CardAction hitOrStick(StateObservedObserved observed);

}
