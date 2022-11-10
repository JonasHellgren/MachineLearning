package black_jack.policies;

import black_jack.enums.CardAction;
import black_jack.models_cards.StateObserved;

public interface PolicyInterface {

    CardAction hitOrStick(StateObserved observed);

}
